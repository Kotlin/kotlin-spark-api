package org.jetbrains.spark.api

import org.apache.spark.api.java.function.FlatMapFunction
import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.api.java.function.MapGroupsFunction
import org.apache.spark.api.java.function.ReduceFunction
import org.apache.spark.sql.*
import org.apache.spark.sql.Encoders.*
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.types.*
import org.jetbrains.spark.extensions.KSparkExtensions
import scala.reflect.ClassTag
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
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


inline fun <reified T> SparkSession.toDS(list: List<T>): Dataset<T> =
        createDataset(list, genericRefEncoder<T>())

inline fun <reified T> SparkSession.dsOf(vararg t: T): Dataset<T> =
        createDataset(listOf(*t), genericRefEncoder<T>())

inline fun <reified T> List<T>.toDS(spark: SparkSession): Dataset<T> =
        spark.createDataset(this, genericRefEncoder<T>())


@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> genericRefEncoder(): Encoder<T> = encoder(typeOf<T>(), T::class)

fun <T> encoder(type: KType, cls: KClass<*>): Encoder<T> {
    @Suppress("UNCHECKED_CAST")
    return when {
        isSupportedClass(cls) -> kotlinClassEncoder(schema(type), cls)
        else -> ENCODERS[cls] as? Encoder<T>? ?: bean(cls.java)
    } as Encoder<T>
}

private fun isSupportedClass(cls: KClass<*>): Boolean = cls.isData
        || cls.isSubclassOf(Map::class)
        || cls.isSubclassOf(Iterable::class)

private fun <T> kotlinClassEncoder(schema: DataType, kClass: KClass<*>): Encoder<T> {
    return ExpressionEncoder(
            if (schema is DataTypeWithClass) KotlinReflection.serializerFor(kClass.java, schema) else KotlinReflection.serializerForJavaType(kClass.java),
            if (schema is DataTypeWithClass) KotlinReflection.deserializerFor(kClass.java, schema) else KotlinReflection.serializerForJavaType(kClass.java),
            ClassTag.apply(kClass.java)
    )
}

inline fun <reified T, reified R> Dataset<T>.map(noinline func: (T) -> R): Dataset<R> =
        map(MapFunction(func), genericRefEncoder())

inline fun <T, reified R> Dataset<T>.flatMap(noinline func: (T) -> Iterator<R>): Dataset<R> =
        flatMap(func, genericRefEncoder<R>())

inline fun <reified T, I : Iterable<T>> Dataset<I>.flatten(): Dataset<T> =
        flatMap(FlatMapFunction { it.iterator() }, genericRefEncoder<T>())

inline fun <T, reified R> Dataset<T>.groupByKey(noinline func: (T) -> R): KeyValueGroupedDataset<R, T> =
        groupByKey(MapFunction(func), genericRefEncoder<R>())

inline fun <T, reified R> Dataset<T>.mapPartitions(noinline func: (Iterator<T>) -> Iterator<R>): Dataset<R> =
        mapPartitions(func, genericRefEncoder<R>())

inline fun <KEY, VALUE, reified R> KeyValueGroupedDataset<KEY, VALUE>.mapValues(noinline func: (VALUE) -> R): KeyValueGroupedDataset<KEY, R> =
        mapValues(MapFunction(func), genericRefEncoder<R>())

inline fun <KEY, VALUE, reified R> KeyValueGroupedDataset<KEY, VALUE>.mapGroups(noinline func: (KEY, Iterator<VALUE>) -> R): Dataset<R> =
        mapGroups(MapGroupsFunction(func), genericRefEncoder<R>())

fun <KEY, VALUE> KeyValueGroupedDataset<KEY, VALUE>.reduceGroups(func: (VALUE, VALUE) -> VALUE): Dataset<Pair<KEY, VALUE>> =
        reduceGroups(ReduceFunction(func))
                .map { t -> t._1 to t._2 }

inline fun <reified R> Dataset<Row>.cast(): Dataset<R> = `as`(genericRefEncoder<R>())

inline fun <reified T> Dataset<T>.forEach(noinline func: (T) -> Unit) = foreach(ForeachFunction(func))

fun <T> Dataset<T>.debugCodegen() = also { KotlinReflection.debugCodegen(it) }

fun <T> Dataset<T>.debug() = also { KotlinReflection.debug(it) }

@JvmName("colOfSet")
fun <T> Dataset<T>.col(name: String) = KSparkExtensions.col(this, name)

fun Column.eq(c: Column) = this.`$eq$eq$eq`(c)

infix fun Column.`==`(c: Column) = `$eq$eq$eq`(c)

inline fun <reified L, reified R : Any?> Dataset<L>.leftJoin(right: Dataset<R>, col: Column): Dataset<Pair<L, R?>> {
    return joinWith(right, col, "left").map { it._1 to it._2 }
}

inline fun <reified T> Dataset<T>.sort(columns: (Dataset<T>) -> Array<Column>) = sort(*columns(this))

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
        java.lang.Integer::class to DataTypes.IntegerType.asNullable(),
        Long::class to DataTypes.LongType,
        Boolean::class to DataTypes.BooleanType,
        Float::class to DataTypes.FloatType,
        Double::class to DataTypes.DoubleType,
        String::class to DataTypes.StringType
)

fun transitiveMerge(a: Map<String, KType>, b: Map<String, KType>): Map<String, KType> {
    return a + b.mapValues {
        a.getOrDefault(it.value.toString(), it.value)
    }
}