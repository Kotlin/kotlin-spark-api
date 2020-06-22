/*-
 * =LICENSE=
 * Kotlin Spark API
 * ----------
 * Copyright (C) 2019 - 2020 JetBrains
 * ----------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =LICENSEEND=
 */
@file:Suppress("HasPlatformType", "unused", "FunctionName")

package org.jetbrains.spark.api

import org.apache.spark.SparkContext
import org.apache.spark.api.java.function.*
import org.apache.spark.sql.*
import org.apache.spark.sql.Encoders.*
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.types.*
import org.jetbrains.spark.extensions.KSparkExtensions
import scala.reflect.ClassTag
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

@JvmField
val ENCODERS = mapOf<KClass<*>, Encoder<*>>(
        Boolean::class to BOOLEAN(),
        Byte::class to BYTE(),
        Short::class to SHORT(),
        Int::class to INT(),
        Long::class to LONG(),
        Float::class to FLOAT(),
        Double::class to DOUBLE(),
        String::class to STRING(),
        BigDecimal::class to DECIMAL(),
        Date::class to DATE(),
        Timestamp::class to TIMESTAMP(),
        ByteArray::class to BINARY()
)

/**
 * Utility method to create dataset from list
 */
inline fun <reified T> SparkSession.toDS(list: List<T>): Dataset<T> =
        createDataset(list, encoder<T>())

/**
 * Utility method to create dataset from list
 */
inline fun <reified T> SparkSession.dsOf(vararg t: T): Dataset<T> =
        createDataset(listOf(*t), encoder<T>())

/**
 * Utility method to create dataset from list
 */
inline fun <reified T> List<T>.toDS(spark: SparkSession): Dataset<T> =
        spark.createDataset(this, encoder<T>())

/**
 * Main method of API, which gives you seamless integraion with Spark:
 * It creates encoder for any given supported type T
 *
 * Supported types are data classes, primitives, and Lists, Maps and Arrays containing them
 *
 * @param T type, supported by Spark
 * @return generated encoder
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> encoder(): Encoder<T> = generateEncoder(typeOf<T>(), T::class)

fun <T> generateEncoder(type: KType, cls: KClass<*>): Encoder<T> {
    @Suppress("UNCHECKED_CAST")
    return when {
        isSupportedClass(cls) -> kotlinClassEncoder(memoizedSchema(type), cls)
        else -> ENCODERS[cls] as? Encoder<T>? ?: bean(cls.java)
    } as Encoder<T>
}

private fun isSupportedClass(cls: KClass<*>): Boolean = cls.isData
        || cls.isSubclassOf(Map::class)
        || cls.isSubclassOf(Iterable::class)

private fun <T> kotlinClassEncoder(schema: DataType, kClass: KClass<*>): Encoder<T> {
    return ExpressionEncoder(
            if (schema is DataTypeWithClass) KotlinReflection.serializerFor(kClass.java, schema) else KotlinReflection.serializerForType(KotlinReflection.getType(kClass.java)),
            if (schema is DataTypeWithClass) KotlinReflection.deserializerFor(kClass.java, schema) else KotlinReflection.deserializerForType(KotlinReflection.getType(kClass.java)),
            ClassTag.apply(kClass.java)
    )
}

inline fun <reified T, reified R> Dataset<T>.map(noinline func: (T) -> R): Dataset<R> =
        map(MapFunction(func), encoder<R>())

inline fun <T, reified R> Dataset<T>.flatMap(noinline func: (T) -> Iterator<R>): Dataset<R> =
        flatMap(func, encoder<R>())

inline fun <reified T, I : Iterable<T>> Dataset<I>.flatten(): Dataset<T> =
        flatMap(FlatMapFunction { it.iterator() }, encoder<T>())

inline fun <T, reified R> Dataset<T>.groupByKey(noinline func: (T) -> R): KeyValueGroupedDataset<R, T> =
        groupByKey(MapFunction(func), encoder<R>())

inline fun <T, reified R> Dataset<T>.mapPartitions(noinline func: (Iterator<T>) -> Iterator<R>): Dataset<R> =
        mapPartitions(func, encoder<R>())

fun <T> Dataset<T>.filterNotNull() = filter { it != null }

inline fun <KEY, VALUE, reified R> KeyValueGroupedDataset<KEY, VALUE>.mapValues(noinline func: (VALUE) -> R): KeyValueGroupedDataset<KEY, R> =
        mapValues(MapFunction(func), encoder<R>())

inline fun <KEY, VALUE, reified R> KeyValueGroupedDataset<KEY, VALUE>.mapGroups(noinline func: (KEY, Iterator<VALUE>) -> R): Dataset<R> =
        mapGroups(MapGroupsFunction(func), encoder<R>())

inline fun <reified KEY, reified VALUE> KeyValueGroupedDataset<KEY, VALUE>.reduceGroups(noinline func: (VALUE, VALUE) -> VALUE): Dataset<Pair<KEY, VALUE>> =
        reduceGroups(ReduceFunction(func))
                .map { t -> t._1 to t._2 }

inline fun <T, reified R> Dataset<T>.downcast(): Dataset<R> = `as`(encoder<R>())

inline fun <reified T> Dataset<T>.forEach(noinline func: (T) -> Unit) = foreach(ForeachFunction(func))

/**
 * It's hard to call `Dataset.debugCodegen` from kotlin, so here is utility for that
 */
fun <T> Dataset<T>.debugCodegen() = also { KSparkExtensions.debugCodegen(it) }

val SparkSession.sparkContext
    get() = KSparkExtensions.sparkContext(this)

/**
 * It's hard to call `Dataset.debug` from kotlin, so here is utility for that
 */
fun <T> Dataset<T>.debug() = also { KSparkExtensions.debug(it) }

fun Column.eq(c: Column) = this.`$eq$eq$eq`(c)

@Suppress("FunctionName")
infix fun Column.`==`(c: Column) = `$eq$eq$eq`(c)
infix fun Column.`&&`(c: Column) = and(c)

fun lit(a: Any) = functions.lit(a)

/**
 * Alias for [Dataset.joinWith] which passes "left" argument
 * and respects the fact that in result of left join right relation is nullable
 *
 * @receiver left dataset
 * @param right right dataset
 * @param col join condition
 *
 * @return dataset of pairs where right element is forced nullable
 */
inline fun <reified L, reified R : Any?> Dataset<L>.leftJoin(right: Dataset<R>, col: Column): Dataset<Pair<L, R?>> {
    return joinWith(right, col, "left").map { it._1 to it._2 }
}

/**
 * Alias for [Dataset.joinWith] which passes "right" argument
 * and respects the fact that in result of right join left relation is nullable
 *
 * @receiver left dataset
 * @param right right dataset
 * @param col join condition
 *
 * @return dataset of [Pair] where left element is forced nullable
 */
inline fun <reified L : Any?, reified R> Dataset<L>.rightJoin(right: Dataset<R>, col: Column): Dataset<Pair<L?, R>> {
    return joinWith(right, col, "right").map { it._1 to it._2 }
}

/**
 * Alias for [Dataset.joinWith] which passes "inner" argument
 *
 * @receiver left dataset
 * @param right right dataset
 * @param col join condition
 *
 * @return resulting dataset of [Pair]
 */
inline fun <reified L, reified R> Dataset<L>.innerJoin(right: Dataset<R>, col: Column): Dataset<Pair<L, R>> {
    return joinWith(right, col, "inner").map { it._1 to it._2 }
}

/**
 * Alias for [Dataset.joinWith] which passes "full" argument
 * and respects the fact that in result of join any element of resulting tuple is nullable
 *
 * @receiver left dataset
 * @param right right dataset
 * @param col join condition
 *
 * @return dataset of [Pair] where both elements are forced nullable
 */
inline fun <reified L : Any?, reified R : Any?> Dataset<L>.fullJoin(right: Dataset<R>, col: Column): Dataset<Pair<L?, R?>> {
    return joinWith(right, col, "full").map { it._1 to it._2 }
}

/**
 * Alias for [Dataset.sort] which forces user to provide sortedcolumns from source dataset
 *
 * @receiver source [Dataset]
 * @param columns producer of sort columns
 * @return sorted [Dataset]
 */
inline fun <reified T> Dataset<T>.sort(columns: (Dataset<T>) -> Array<Column>) = sort(*columns(this))

/**
 * This function creates block, where one can call any further computations on already cached dataset
 * Data will be unpersisted automatically at the end of computation
 *
 * it may be useful in many situatiions, for example when one needs to write data to several targets
 * ```kotlin
 * ds.withCached {
 *   write()
 *      .also { it.orc("First destination") }
 *      .also { it.avro("Second destination") }
 * }
 * ```
 *
 * @param blockingUnpersist if execution should be blocked until everyting persisted will be deleted
 * @param executeOnCached Block which should be executed on cached dataset.
 * @return result of block execution for further usage. It may be anything including source or new dataset
 */
inline fun <reified T, R> Dataset<T>.withCached(blockingUnpersist: Boolean = false, executeOnCached: Dataset<T>.() -> R): R {
    val cached = this.cache()
    return cached.executeOnCached().also { cached.unpersist(blockingUnpersist) }
}

/**
 * Alternative to [Dataset.show] which returns surce dataset.
 * Useful in debug purposes when you need to view contant of dataset as intermediate operation
 */
fun <T> Dataset<T>.showDS(numRows: Int = 20, truncate: Boolean = true) = apply { show(numRows, truncate) }

fun schema(type: KType, map: Map<String, KType> = mapOf()): DataType {
    val primitiveSchema = knownDataTypes[type.classifier]
    if (primitiveSchema != null) return KSimpleTypeWrapper(primitiveSchema, (type.classifier!! as KClass<*>).java, type.isMarkedNullable)
    val klass = type.classifier as? KClass<*> ?: throw IllegalArgumentException("Unsupported type $type")
    val args = type.arguments

    val types = transitiveMerge(map, klass.typeParameters.zip(args).map {
        it.first.name to it.second.type!!
    }.toMap())
    return when {
        klass.isSubclassOf(Iterable::class) -> {
            val listParam = types.getValue(klass.typeParameters[0].name)
            KComplexTypeWrapper(
                    DataTypes.createArrayType(schema(listParam, types), listParam.isMarkedNullable),
                    klass.java,
                    listParam.isMarkedNullable
            )
        }
        klass.isSubclassOf(Map::class) -> {
            val mapKeyParam = types.getValue(klass.typeParameters[0].name)
            val mapValueParam = types.getValue(klass.typeParameters[1].name)
            KComplexTypeWrapper(
                    DataTypes.createMapType(
                            schema(mapKeyParam, types),
                            schema(mapValueParam, types),
                            true
                    ),
                    klass.java,
                    mapValueParam.isMarkedNullable
            )
        }
        else -> KDataTypeWrapper(
                StructType(
                        klass
                                .declaredMemberProperties
                                .filter { it.findAnnotation<Transient>() == null }
                                .map {
                                    val projectedType = types[it.returnType.toString()] ?: it.returnType
                                    StructField(it.name, schema(projectedType, types), projectedType.isMarkedNullable, Metadata.empty())
                                }
                                .toTypedArray()
                ),
                klass.java,
                true
        )
    }
}

private val knownDataTypes = mapOf(
        Byte::class to DataTypes.ByteType,
        Short::class to DataTypes.ShortType,
        Int::class to DataTypes.IntegerType,
        Long::class to DataTypes.LongType,
        Boolean::class to DataTypes.BooleanType,
        Float::class to DataTypes.FloatType,
        Double::class to DataTypes.DoubleType,
        String::class to DataTypes.StringType
)

private fun transitiveMerge(a: Map<String, KType>, b: Map<String, KType>): Map<String, KType> {
    return a + b.mapValues {
        a.getOrDefault(it.value.toString(), it.value)
    }
}

class Memoize1<in T, out R>(val f: (T) -> R) : (T) -> R {
    private val values = ConcurrentHashMap<T, R>()
    override fun invoke(x: T) =
            values.getOrPut(x, { f(x) })
}

private fun <T, R> ((T) -> R).memoize(): (T) -> R = Memoize1(this)

private val memoizedSchema = { x: KType -> schema(x) }.memoize()
