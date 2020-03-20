package org.jetbrains.spark.api

import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.api.java.function.MapGroupsFunction
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
val ENCODERS = mapOf<KClass<out Any>, Encoder<out Any?>>(
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


fun <T : Any> encoder(c: KClass<T>): Encoder<T> = ENCODERS[c] as? Encoder<T>? ?: bean(c.java)

inline fun <reified T : Any> SparkSession.toDS(list: List<T>): Dataset<T> =
        createDataset(list, genericRefEncoder<T>())

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> genericRefEncoder(): Encoder<T> = when {
    T::class.isData -> dataClassEncoder(schema(typeOf<T>()), T::class)
    else -> encoder(T::class)
}

fun <T : Any> dataClassEncoder(schema: DataType, kClass: KClass<T>): Encoder<T> {
    val isStruct = schema is StructType || schema is KDataTypeWrapper && schema.isData
    return ExpressionEncoder(
            if (isStruct) KotlinReflection.serializerForDataType(kClass.java, schema as KDataTypeWrapper) else KotlinReflection.serializerForJavaType(kClass.java),
            if (isStruct) KotlinReflection.deserializerForDataType(kClass.java, schema as KDataTypeWrapper) else KotlinReflection.serializerForJavaType(kClass.java),
            ClassTag.apply(kClass.java)
    )
}

inline fun <T, reified R : Any> Dataset<T>.map(noinline func: (T) -> R): Dataset<R> =
        map(MapFunction(func), genericRefEncoder<R>())

inline fun <T, reified R : Any> Dataset<T>.flatMap(noinline func: (T) -> Iterator<R>): Dataset<R> =
        flatMap(func, genericRefEncoder<R>())

inline fun <T, reified R : Any> Dataset<T>.groupByKey(noinline func: (T) -> R): KeyValueGroupedDataset<R, T> =
        groupByKey(MapFunction(func), genericRefEncoder<R>())

inline fun <T, reified R : Any> Dataset<T>.mapPartitions(noinline func: (Iterator<T>) -> Iterator<R>): Dataset<R> =
        mapPartitions(func, genericRefEncoder<R>())

inline fun <KEY, VALUE, reified R : Any> KeyValueGroupedDataset<KEY, VALUE>.mapValues(noinline func: (VALUE) -> R): KeyValueGroupedDataset<KEY, R> =
        mapValues(MapFunction(func), genericRefEncoder<R>())

inline fun <KEY, VALUE, reified R : Any> KeyValueGroupedDataset<KEY, VALUE>.mapGroups(noinline func: (KEY, Iterator<VALUE>) -> R): Dataset<R> =
        mapGroups(MapGroupsFunction(func), genericRefEncoder<R>())

inline fun <reified R : Any> Dataset<Row>.cast(): Dataset<R> = `as`(genericRefEncoder<R>())

inline fun <reified T> Dataset<T>.forEach(noinline func: (T) -> Unit) = foreach(ForeachFunction(func))

fun <T> Dataset<T>.debugCodegen() = also { KotlinReflection.debugCodegen(it) }

fun <T> Dataset<T>.debug() = also { KotlinReflection.debug(it) }

@JvmName("colOfSet")
fun <T> Dataset<T>.col(name: String) = KSparkExtensions.col(this, name)

fun Column.eq(c: Column) = this.`$eq$eq$eq`(c)

fun <L, R> Dataset<L>.leftJoin(right: Dataset<R>, col: Column): Dataset<Pair<L, R?>> = joinWith(right, col, "left")
        .map { it._1 to it._2 }

fun schema(type: KType, map: Map<String, KType> = mapOf()): DataType {
    val primitivesSchema = knownDataTypes[type.classifier]
    if (primitivesSchema != null) return KOtherTypeWrapper(primitivesSchema, false, (type.classifier!! as KClass<*>).java)
    val klass = type.classifier!! as KClass<*>
    val args = type.arguments

    val types = transitiveMerge(map, klass.typeParameters.zip(args).map {
        it.first.name to it.second.type!!
    }.toMap())

    return when {
        klass.isSubclassOf(Iterable::class) -> {
            val listParam = types.getValue(klass.typeParameters[0].name)
            KOtherTypeWrapper(
                    DataTypes.createArrayType(schema(listParam, types), listParam.isMarkedNullable),
                    false,
                    null
            )
        }
        klass.isSubclassOf(Map::class) -> {
            val mapKeyParam = types.getValue(klass.typeParameters[0].name)
            val mapValueParam = types.getValue(klass.typeParameters[1].name)
            KOtherTypeWrapper(
                    DataTypes.createMapType(
                            schema(mapKeyParam, types),
                            schema(mapValueParam, types),
                            mapValueParam.isMarkedNullable
                    ),
                    false,
                    null
            )
        }
        else -> KDataTypeWrapper(
                StructType(
                        klass
                                .declaredMemberProperties
                                .filter { it.findAnnotation<Transient>() == null }
                                .map {
                                    val projectedType = types[it.returnType.toString()] ?: it.returnType
                                    val tpe = knownDataTypes[projectedType.classifier]
                                            ?.let { dt -> KOtherTypeWrapper(dt, false, (projectedType.classifier as KClass<*>).java) }
                                            ?: schema(projectedType, types)
                                    StructField(it.name, tpe, it.returnType.isMarkedNullable, Metadata.empty())
                                }
                                .toTypedArray()
                ),
                true,
                klass.java
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

fun transitiveMerge(a: Map<String, KType>, b: Map<String, KType>): Map<String, KType> {
    return a + b.mapValues {
        a.getOrDefault(it.value.toString(), it.value)
    }
}