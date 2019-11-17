import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.api.java.function.MapGroupsFunction
import org.apache.spark.sql.*
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import kotlin.reflect.KClass

@JvmField
val ENCODERS = mapOf<KClass<out Any>, Encoder<out Any>>(
    Boolean::class to Encoders.BOOLEAN(),
    Byte::class to Encoders.BYTE(),
    Short::class to Encoders.SHORT(),
    Int::class to Encoders.INT(),
    Long::class to Encoders.LONG(),
    Float::class to Encoders.FLOAT(),
    Double::class to Encoders.DOUBLE(),
    String::class to Encoders.STRING(),
    BigDecimal::class to Encoders.DECIMAL(),
    Date::class to Encoders.DATE(),
    Timestamp::class to Encoders.TIMESTAMP(),
    ByteArray::class to Encoders.BINARY()
)

inline fun <reified T> encoder(): Encoder<T>? = ENCODERS[T::class] as? Encoder<T>? ?: Encoders.bean(T::class.java)//Encoders.kryo(T::class.java)
inline fun <reified T> SparkSession.toDS(list: List<T>): Dataset<T> = createDataset(list, encoder<T>())
inline fun <T, reified R> Dataset<T>.map(crossinline func: (T) -> R): Dataset<R> =
    map(MapFunction { func(it) }, encoder<R>())

inline fun <T, reified R> Dataset<T>.flatMap(noinline func: (T) -> Iterator<R>): Dataset<R> =
    flatMap(func, encoder<R>())

inline fun <T, reified R> Dataset<T>.groupByKey(noinline func: (T) -> R): KeyValueGroupedDataset<R, T> =
    groupByKey(MapFunction { func(it) }, encoder<R>())

inline fun <T, reified R> Dataset<T>.mapPartitions(noinline func: (Iterator<T>) -> Iterator<R>): Dataset<R> =
    mapPartitions(func, encoder<R>())

inline fun <KEY, VALUE, reified R> KeyValueGroupedDataset<KEY, VALUE>.mapValues(crossinline func: (VALUE) -> R) =
    mapValues(MapFunction { func(it) }, encoder<R>())

inline fun <KEY, VALUE, reified R> KeyValueGroupedDataset<KEY, VALUE>.mapGroups(crossinline func: (KEY, Iterator<VALUE>) -> R) =
    mapGroups(MapGroupsFunction { a, b -> func(a, b) }, encoder<R>())

inline fun <reified R> Dataset<Row>.cast(): Dataset<R> = `as`(encoder<R>())

fun <T, R> Iterator<T>.map(func: (T) -> R): Iterator<R> {
    val self = this
    return object : AbstractIterator<R>() {
        override fun computeNext() {
            while (self.hasNext()) {
                setNext(func(self.next()))
            }
            done()
        }
    }
}

//class MyEncoder: Encoder