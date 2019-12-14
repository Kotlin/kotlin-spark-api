import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.api.java.function.MapGroupsFunction
import org.apache.spark.sql.*
import org.apache.spark.sql.Encoders.*
import org.apache.spark.sql.types.*

import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

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

inline fun <reified T : Any> genericRefEncoder(): Encoder<T> = TODO()//typeRef<T>().encoder()

//inline fun <reified T : Any> typeRef() = object : TypeRef<T>() {}

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

abstract class KTypeRef<T> protected constructor() {
    var type = this::class.supertypes[0].arguments[0].type
            ?: throw IllegalArgumentException("Internal error: TypeReference constructed without actual type information")
}

fun schema(type: KType, map: Map<String, KType> = mapOf()): DataType {
    val primitivesSchema = knownDataTypes[type.classifier]
    if (primitivesSchema != null) return primitivesSchema
    val klass = type.classifier!! as KClass<*>
    val args = type.arguments

    val types = transitiveMerge(map, klass.typeParameters.zip(args).map {
        it.first.name to it.second.type!!
    }.toMap())

    return when {
        klass.isSubclassOf(Iterable::class) -> {
            val listParam = types.getValue(klass.typeParameters[0].name)
            DataTypes.createArrayType(schema(listParam, types), listParam.isMarkedNullable)
        }
        klass.isSubclassOf(Map::class) -> {
            val mapKeyParam = types.getValue(klass.typeParameters[0].name)
            val mapValueParam = types.getValue(klass.typeParameters[1].name)
            DataTypes.createMapType(
                    schema(mapKeyParam, types),
                    schema(mapValueParam, types),
                    mapValueParam.isMarkedNullable
            )
        }
        else -> StructType(
                klass
                        .declaredMemberProperties
                        .filter { it.findAnnotation<Transient>() == null }
                        .map {
                            val projectedType = types[it.returnType.toString()] ?: it.returnType
                            val tpe = knownDataTypes[projectedType.classifier] ?: schema(projectedType, types)
                            StructField(it.name, tpe, it.returnType.isMarkedNullable, Metadata.empty())
                        }
                        .toTypedArray()
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