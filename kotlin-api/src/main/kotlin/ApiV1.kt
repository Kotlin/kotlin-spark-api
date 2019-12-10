import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.api.java.function.MapGroupsFunction
import org.apache.spark.sql.*
import org.apache.spark.sql.Encoders.*
import org.apache.spark.sql.catalyst.WalkedTypePath
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.types.*

import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

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
    var type = this::class.supertypes[0].arguments[0].type ?:
    throw IllegalArgumentException("Internal error: TypeReference constructed without actual type information")
}

fun schema(type: KType): DataType {
    val klass = type.classifier!! as KClass<*>
    val args = type.arguments

    val types = klass.typeParameters.zip(args).map {
        it.first.name to it.second.type
    }.toMap()

    return StructType(klass.declaredMemberProperties.filter { it.findAnnotation<Transient>() == null }.map {
        val projectedType = types[it.returnType.toString()] ?: it.returnType
        val tpe = when (projectedType.classifier) {
            Byte::class -> DataTypes.ByteType
            Short::class -> DataTypes.ShortType
            Int::class -> DataTypes.IntegerType
            Long::class -> DataTypes.LongType
            Boolean::class -> DataTypes.BooleanType
            Float::class -> DataTypes.FloatType
            Double::class -> DataTypes.DoubleType
            String::class -> DataTypes.StringType
            // Data/Timestamp
            else -> schema(projectedType)
        }
        StructField(it.name, tpe, it.returnType.isMarkedNullable, Metadata.empty())
    }.toTypedArray())
}