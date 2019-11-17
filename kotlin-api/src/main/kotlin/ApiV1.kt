import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.api.java.function.MapGroupsFunction
import org.apache.spark.sql.*
import org.apache.spark.sql.Encoders.*
import org.apache.spark.sql.catalyst.JavaTypeInference
import org.apache.spark.sql.types.DataType
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
import java.lang.reflect.ParameterizedType
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.starProjectedType

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

inline fun <reified T : Any> genericRefEncoder() = genericDataEncoder((typeRef<T>()))

inline fun <reified T : Any> typeRef() = object : TypeRef<T>() {}

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

// TODO: should we copyright Jackson here? It's almost copypaste
abstract class TypeRef<T> protected constructor() {
    var type: ParameterizedType

    init {
        val sC = this::class.java.genericSuperclass
        require(sC !is Class<*>) { "Internal error: TypeReference constructed without actual type information" }
        this.type = sC as ParameterizedType
    }
}

fun <T> genericDataEncoder(ref: TypeRef<T>): Encoder<T> {
    val typeImpl = ref.type.actualTypeArguments[0] as ParameterizedTypeImpl
    return genericDataEncoder(typeImpl)
}

private fun <T> genericDataEncoder(typeImpl: ParameterizedTypeImpl): Encoder<T> {
    val rawType = typeImpl.rawType.kotlin
    if (ENCODERS[rawType] != null) {
        return ENCODERS[rawType] as Encoder<T>
    }
    if (rawType.typeParameters.isNotEmpty() && rawType.isData) {
        /**
         * All the generic data is known here from typeImpl (because of TypeRef). We need to build serializer for
         * generics by reifying their types
         *
         * Also we need to build deserializer based on constructor, not setters
         */
        val schema = obtainGenericDataSchema(typeImpl)
    }
    // TODO: implement non-generic data encoder here
    // TODO: delegate to bean encoder for non-data classes
    TODO()
}

fun obtainGenericDataSchema(typeImpl: ParameterizedTypeImpl): DataType {
    val z = typeImpl.rawType.kotlin.declaredMemberProperties
    val y = typeImpl.actualTypeArguments
    return StructType(
            KotlinReflectionHelper
                    .dataClassProps(typeImpl.rawType.kotlin)
                    .map {
                        val dt = if(!it.c.isData) JavaTypeInference.inferDataType(it.c.java)._1 else null
                        // TODO: refine type is it's Any from typeImpl.
                        // TODO: is reified class is not data — we should delegete to JavaTypeInference
                        // TODO: if we have concrete type and it's not data - we should delegate to JavaTypeInference. It won't cover data-class inside non-data, but who cares?
                        StructField(it.name, dt, it.nullable, Metadata.empty())
                    }
                    .toTypedArray()
    )
}
