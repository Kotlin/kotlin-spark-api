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

package org.jetbrains.kotlinx.spark.api

import org.apache.spark.SparkContext
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.api.java.function.*
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.*
import org.apache.spark.sql.Encoders.*
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.streaming.GroupState
import org.apache.spark.sql.streaming.GroupStateTimeout
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.*
import org.apache.spark.unsafe.types.CalendarInterval
import org.jetbrains.kotlinx.spark.extensions.KSparkExtensions
import scala.Product
import scala.Tuple2
import scala.reflect.ClassTag
import scala.reflect.api.StandardDefinitions
import java.beans.PropertyDescriptor
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.BooleanArray
import kotlin.Byte
import kotlin.ByteArray
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Double
import kotlin.DoubleArray
import kotlin.ExperimentalStdlibApi
import kotlin.Float
import kotlin.FloatArray
import kotlin.IllegalArgumentException
import kotlin.Int
import kotlin.IntArray
import kotlin.Long
import kotlin.LongArray
import kotlin.OptIn
import kotlin.Pair
import kotlin.ReplaceWith
import kotlin.Short
import kotlin.ShortArray
import kotlin.String
import kotlin.Suppress
import kotlin.Triple
import kotlin.Unit
import kotlin.also
import kotlin.apply
import kotlin.invoke
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.to

@JvmField
val ENCODERS: Map<KClass<*>, Encoder<*>> = mapOf(
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
    LocalDate::class to LOCALDATE(), // 3.0+
    Timestamp::class to TIMESTAMP(),
    Instant::class to INSTANT(), // 3.0+
    ByteArray::class to BINARY(),
    Duration::class to DURATION(), // 3.2+
    Period::class to PERIOD(), // 3.2+
)


/**
 * Broadcast a read-only variable to the cluster, returning a
 * [org.apache.spark.broadcast.Broadcast] object for reading it in distributed functions.
 * The variable will be sent to each cluster only once.
 *
 * @param value value to broadcast to the Spark nodes
 * @return `Broadcast` object, a read-only variable cached on each machine
 */
inline fun <reified T> SparkSession.broadcast(value: T): Broadcast<T> = try {
    sparkContext.broadcast(value, encoder<T>().clsTag())
} catch (e: ClassNotFoundException) {
    JavaSparkContext(sparkContext).broadcast(value)
}

/**
 * Broadcast a read-only variable to the cluster, returning a
 * [org.apache.spark.broadcast.Broadcast] object for reading it in distributed functions.
 * The variable will be sent to each cluster only once.
 *
 * @param value value to broadcast to the Spark nodes
 * @return `Broadcast` object, a read-only variable cached on each machine
 * @see broadcast
 */
@Deprecated(
    "You can now use `spark.broadcast()` instead.",
    ReplaceWith("spark.broadcast(value)"),
    DeprecationLevel.WARNING
)
inline fun <reified T> SparkContext.broadcast(value: T): Broadcast<T> = try {
    broadcast(value, encoder<T>().clsTag())
} catch (e: ClassNotFoundException) {
    JavaSparkContext(this).broadcast(value)
}

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
 * Main method of API, which gives you seamless integration with Spark:
 * It creates encoder for any given supported type T
 *
 * Supported types are data classes, primitives, and Lists, Maps and Arrays containing them
 *
 * @param T type, supported by Spark
 * @return generated encoder
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> encoder(): Encoder<T> = generateEncoder(typeOf<T>(), T::class)

/**
 * @see encoder
 */
fun <T> generateEncoder(type: KType, cls: KClass<*>): Encoder<T> {
    @Suppress("UNCHECKED_CAST")
    return when {
        isSupportedClass(cls) -> kotlinClassEncoder(memoizedSchema(type), cls)
        else -> ENCODERS[cls] as? Encoder<T>? ?: bean(cls.java)
    } as Encoder<T>
}

private fun isSupportedClass(cls: KClass<*>): Boolean = when {
        cls == ByteArray::class -> false // uses binary encoder
        cls.isData -> true
        cls.isSubclassOf(Map::class) -> true
        cls.isSubclassOf(Iterable::class) -> true
        cls.isSubclassOf(Product::class) -> true
        cls.java.isArray -> true
        else -> false
    }


private fun <T> kotlinClassEncoder(schema: DataType, kClass: KClass<*>): Encoder<T> {
    return ExpressionEncoder(
        if (schema is DataTypeWithClass) KotlinReflection.serializerFor(
            kClass.java,
            schema
        ) else KotlinReflection.serializerForType(KotlinReflection.getType(kClass.java)),
        if (schema is DataTypeWithClass) KotlinReflection.deserializerFor(
            kClass.java,
            schema
        ) else KotlinReflection.deserializerForType(KotlinReflection.getType(kClass.java)),
        ClassTag.apply(kClass.java)
    )
}

/**
 * (Kotlin-specific)
 * Returns a new Dataset that contains the result of applying [func] to each element.
 */
inline fun <reified T, reified R> Dataset<T>.map(noinline func: (T) -> R): Dataset<R> =
    map(MapFunction(func), encoder<R>())

/**
 * (Kotlin-specific)
 * Returns a new Dataset by first applying a function to all elements of this Dataset,
 * and then flattening the results.
 */
inline fun <T, reified R> Dataset<T>.flatMap(noinline func: (T) -> Iterator<R>): Dataset<R> =
    flatMap(func, encoder<R>())

/**
 * (Kotlin-specific)
 * Returns a new Dataset by flattening. This means that a Dataset of an iterable such as
 * `listOf(listOf(1, 2, 3), listOf(4, 5, 6))` will be flattened to a Dataset of `listOf(1, 2, 3, 4, 5, 6)`.
 */
inline fun <reified T, I : Iterable<T>> Dataset<I>.flatten(): Dataset<T> =
    flatMap(FlatMapFunction { it.iterator() }, encoder<T>())

/**
 * (Kotlin-specific)
 * Returns a [KeyValueGroupedDataset] where the data is grouped by the given key [func].
 */
inline fun <T, reified R> Dataset<T>.groupByKey(noinline func: (T) -> R): KeyValueGroupedDataset<R, T> =
    groupByKey(MapFunction(func), encoder<R>())

/**
 * (Kotlin-specific)
 * Returns a new Dataset that contains the result of applying [func] to each partition.
 */
inline fun <T, reified R> Dataset<T>.mapPartitions(noinline func: (Iterator<T>) -> Iterator<R>): Dataset<R> =
    mapPartitions(func, encoder<R>())

/**
 * (Kotlin-specific)
 * Filters rows to eliminate [null] values.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> Dataset<T?>.filterNotNull(): Dataset<T> = filter { it != null } as Dataset<T>

/**
 * Returns a new [KeyValueGroupedDataset] where the given function [func] has been applied
 * to the data. The grouping key is unchanged by this.
 *
 * ```kotlin
 *   // Create values grouped by key from a Dataset<Arity2<K, V>>
 *   ds.groupByKey { it._1 }.mapValues { it._2 }
 * ```
 */
inline fun <KEY, VALUE, reified R> KeyValueGroupedDataset<KEY, VALUE>.mapValues(noinline func: (VALUE) -> R): KeyValueGroupedDataset<KEY, R> =
    mapValues(MapFunction(func), encoder<R>())

/**
 * (Kotlin-specific)
 * Applies the given function to each group of data. For each unique group, the function will
 * be passed the group key and an iterator that contains all the elements in the group. The
 * function can return an element of arbitrary type which will be returned as a new [Dataset].
 *
 * This function does not support partial aggregation, and as a result requires shuffling all
 * the data in the [Dataset]. If an application intends to perform an aggregation over each
 * key, it is best to use the reduce function or an
 * [org.apache.spark.sql.expressions.Aggregator].
 *
 * Internally, the implementation will spill to disk if any given group is too large to fit into
 * memory.  However, users must take care to avoid materializing the whole iterator for a group
 * (for example, by calling [toList]) unless they are sure that this is possible given the memory
 * constraints of their cluster.
 */
inline fun <KEY, VALUE, reified R> KeyValueGroupedDataset<KEY, VALUE>.mapGroups(noinline func: (KEY, Iterator<VALUE>) -> R): Dataset<R> =
    mapGroups(MapGroupsFunction(func), encoder<R>())

/**
 * (Kotlin-specific)
 * Reduces the elements of each group of data using the specified binary function.
 * The given function must be commutative and associative or the result may be non-deterministic.
 *
 * Note that you need to use [reduceGroupsK] always instead of the Java- or Scala-specific
 * [KeyValueGroupedDataset.reduceGroups] to make the compiler work.
 */
inline fun <reified KEY, reified VALUE> KeyValueGroupedDataset<KEY, VALUE>.reduceGroupsK(noinline func: (VALUE, VALUE) -> VALUE): Dataset<Pair<KEY, VALUE>> =
    reduceGroups(ReduceFunction(func))
        .map { t -> t._1 to t._2 }

/**
 * (Kotlin-specific)
 * Reduces the elements of this Dataset using the specified binary function. The given `func`
 * must be commutative and associative or the result may be non-deterministic.
 */
inline fun <reified T> Dataset<T>.reduceK(noinline func: (T, T) -> T): T =
    reduce(ReduceFunction(func))

/**
 * (Kotlin-specific)
 * Maps the Dataset to only retain the "keys" or [Tuple2._1] values.
 */
@JvmName("takeKeysTuple2")
inline fun <reified T1, T2> Dataset<Tuple2<T1, T2>>.takeKeys(): Dataset<T1> = map { it._1() }

/**
 * (Kotlin-specific)
 * Maps the Dataset to only retain the "keys" or [Pair.first] values.
 */
inline fun <reified T1, T2> Dataset<Pair<T1, T2>>.takeKeys(): Dataset<T1> = map { it.first }

/**
 * (Kotlin-specific)
 * Maps the Dataset to only retain the "keys" or [Arity2._1] values.
 */
@JvmName("takeKeysArity2")
inline fun <reified T1, T2> Dataset<Arity2<T1, T2>>.takeKeys(): Dataset<T1> = map { it._1 }

/**
 * (Kotlin-specific)
 * Maps the Dataset to only retain the "values" or [Tuple2._2] values.
 */
@JvmName("takeValuesTuple2")
inline fun <T1, reified T2> Dataset<Tuple2<T1, T2>>.takeValues(): Dataset<T2> = map { it._2() }

/**
 * (Kotlin-specific)
 * Maps the Dataset to only retain the "values" or [Pair.second] values.
 */
inline fun <T1, reified T2> Dataset<Pair<T1, T2>>.takeValues(): Dataset<T2> = map { it.second }

/**
 * (Kotlin-specific)
 * Maps the Dataset to only retain the "values" or [Arity2._2] values.
 */
@JvmName("takeValuesArity2")
inline fun <T1, reified T2> Dataset<Arity2<T1, T2>>.takeValues(): Dataset<T2> = map { it._2 }

/**
 * (Kotlin-specific)
 * Applies the given function to each group of data. For each unique group, the function will
 * be passed the group key and an iterator that contains all the elements in the group. The
 * function can return an iterator containing elements of an arbitrary type which will be returned
 * as a new [Dataset].
 *
 * This function does not support partial aggregation, and as a result requires shuffling all
 * the data in the [Dataset]. If an application intends to perform an aggregation over each
 * key, it is best to use the reduce function or an
 * [org.apache.spark.sql.expressions.Aggregator].
 *
 * Internally, the implementation will spill to disk if any given group is too large to fit into
 * memory.  However, users must take care to avoid materializing the whole iterator for a group
 * (for example, by calling [toList]) unless they are sure that this is possible given the memory
 * constraints of their cluster.
 */
inline fun <K, V, reified U> KeyValueGroupedDataset<K, V>.flatMapGroups(
    noinline func: (key: K, values: Iterator<V>) -> Iterator<U>,
): Dataset<U> = flatMapGroups(
    FlatMapGroupsFunction(func),
    encoder<U>()
)

/**
 * (Kotlin-specific)
 * Returns the group state value if it exists, else [null].
 * This is comparable to [GroupState.getOption], but instead utilises Kotlin's nullability features
 * to get the same result.
 */
fun <S> GroupState<S>.getOrNull(): S? = if (exists()) get() else null

/**
 * (Kotlin-specific)
 * Allows the group state object to be used as a delegate. Will be [null] if it does not exist.
 *
 * For example:
 * ```kotlin
 * groupedDataset.mapGroupsWithState(GroupStateTimeout.NoTimeout()) { key, values, state: GroupState<Int> ->
 *     var s by state
 *     ...
 * }
 * ```
 */
operator fun <S> GroupState<S>.getValue(thisRef: Any?, property: KProperty<*>): S? = getOrNull()

/**
 * (Kotlin-specific)
 * Allows the group state object to be used as a delegate. Will be [null] if it does not exist.
 *
 * For example:
 * ```kotlin
 * groupedDataset.mapGroupsWithState(GroupStateTimeout.NoTimeout()) { key, values, state: GroupState<Int> ->
 *     var s by state
 *     ...
 * }
 * ```
 */
operator fun <S> GroupState<S>.setValue(thisRef: Any?, property: KProperty<*>, value: S?): Unit = update(value)

/**
 * (Kotlin-specific)
 * Applies the given function to each group of data, while maintaining a user-defined per-group
 * state. The result Dataset will represent the objects returned by the function.
 * For a static batch Dataset, the function will be invoked once per group. For a streaming
 * Dataset, the function will be invoked for each group repeatedly in every trigger, and
 * updates to each group's state will be saved across invocations.
 * See [org.apache.spark.sql.streaming.GroupState] for more details.
 *
 * @param S The type of the user-defined state. Must be encodable to Spark SQL types.
 * @param U The type of the output objects. Must be encodable to Spark SQL types.
 * @param func Function to be called on every group.
 *
 * See [Encoder] for more details on what types are encodable to Spark SQL.
 */
inline fun <K, V, reified S, reified U> KeyValueGroupedDataset<K, V>.mapGroupsWithState(
    noinline func: (key: K, values: Iterator<V>, state: GroupState<S>) -> U,
): Dataset<U> = mapGroupsWithState(
    MapGroupsWithStateFunction(func),
    encoder<S>(),
    encoder<U>()
)

/**
 * (Kotlin-specific)
 * Applies the given function to each group of data, while maintaining a user-defined per-group
 * state. The result Dataset will represent the objects returned by the function.
 * For a static batch Dataset, the function will be invoked once per group. For a streaming
 * Dataset, the function will be invoked for each group repeatedly in every trigger, and
 * updates to each group's state will be saved across invocations.
 * See [org.apache.spark.sql.streaming.GroupState] for more details.
 *
 * @param S The type of the user-defined state. Must be encodable to Spark SQL types.
 * @param U The type of the output objects. Must be encodable to Spark SQL types.
 * @param func Function to be called on every group.
 * @param timeoutConf Timeout configuration for groups that do not receive data for a while.
 *
 * See [Encoder] for more details on what types are encodable to Spark SQL.
 */
inline fun <K, V, reified S, reified U> KeyValueGroupedDataset<K, V>.mapGroupsWithState(
    timeoutConf: GroupStateTimeout,
    noinline func: (key: K, values: Iterator<V>, state: GroupState<S>) -> U,
): Dataset<U> = mapGroupsWithState(
    MapGroupsWithStateFunction(func),
    encoder<S>(),
    encoder<U>(),
    timeoutConf
)

/**
 * (Kotlin-specific)
 * Applies the given function to each group of data, while maintaining a user-defined per-group
 * state. The result Dataset will represent the objects returned by the function.
 * For a static batch Dataset, the function will be invoked once per group. For a streaming
 * Dataset, the function will be invoked for each group repeatedly in every trigger, and
 * updates to each group's state will be saved across invocations.
 * See [GroupState] for more details.
 *
 * @param S The type of the user-defined state. Must be encodable to Spark SQL types.
 * @param U The type of the output objects. Must be encodable to Spark SQL types.
 * @param func Function to be called on every group.
 * @param outputMode The output mode of the function.
 * @param timeoutConf Timeout configuration for groups that do not receive data for a while.
 *
 * See [Encoder] for more details on what types are encodable to Spark SQL.
 */
inline fun <K, V, reified S, reified U> KeyValueGroupedDataset<K, V>.flatMapGroupsWithState(
    outputMode: OutputMode,
    timeoutConf: GroupStateTimeout,
    noinline func: (key: K, values: Iterator<V>, state: GroupState<S>) -> Iterator<U>,
): Dataset<U> = flatMapGroupsWithState(
    FlatMapGroupsWithStateFunction(func),
    outputMode,
    encoder<S>(),
    encoder<U>(),
    timeoutConf
)

/**
 * (Kotlin-specific)
 * Applies the given function to each cogrouped data. For each unique group, the function will
 * be passed the grouping key and 2 iterators containing all elements in the group from
 * [Dataset] [this] and [other].  The function can return an iterator containing elements of an
 * arbitrary type which will be returned as a new [Dataset].
 */
inline fun <K, V, U, reified R> KeyValueGroupedDataset<K, V>.cogroup(
    other: KeyValueGroupedDataset<K, U>,
    noinline func: (key: K, left: Iterator<V>, right: Iterator<U>) -> Iterator<R>,
): Dataset<R> = cogroup(
    other,
    CoGroupFunction(func),
    encoder<R>()
)

/** DEPRECATED: Use [as] or [to] for this. */
@Deprecated(
    message = "Deprecated, since we already have `as`() and to().",
    replaceWith = ReplaceWith("this.to<R>()"),
    level = DeprecationLevel.ERROR,
)
inline fun <T, reified R> Dataset<T>.downcast(): Dataset<R> = `as`(encoder<R>())

/**
 * (Kotlin-specific)
 * Returns a new Dataset where each record has been mapped on to the specified type. The
 * method used to map columns depend on the type of [R]:
 * - When [R] is a class, fields for the class will be mapped to columns of the same name
 *   (case sensitivity is determined by [spark.sql.caseSensitive]).
 * - When [R] is a tuple, the columns will be mapped by ordinal (i.e. the first column will
 *   be assigned to `_1`).
 * - When [R] is a primitive type (i.e. [String], [Int], etc.), then the first column of the
 *   `DataFrame` will be used.
 *
 * If the schema of the Dataset does not match the desired [R] type, you can use [Dataset.select]/[selectTyped]
 * along with [Dataset.alias] or [as]/[to] to rearrange or rename as required.
 *
 * Note that [as]/[to] only changes the view of the data that is passed into typed operations,
 * such as [map], and does not eagerly project away any columns that are not present in
 * the specified class.
 *
 * @see to as alias for [as]
 */
inline fun <reified R> Dataset<*>.`as`(): Dataset<R> = `as`(encoder<R>())

/**
 * (Kotlin-specific)
 * Returns a new Dataset where each record has been mapped on to the specified type. The
 * method used to map columns depend on the type of [R]:
 * - When [R] is a class, fields for the class will be mapped to columns of the same name
 *   (case sensitivity is determined by [spark.sql.caseSensitive]).
 * - When [R] is a tuple, the columns will be mapped by ordinal (i.e. the first column will
 *   be assigned to `_1`).
 * - When [R] is a primitive type (i.e. [String], [Int], etc.), then the first column of the
 *   `DataFrame` will be used.
 *
 * If the schema of the Dataset does not match the desired [R] type, you can use [Dataset.select]/[selectTyped]
 * along with [Dataset.alias] or [as]/[to] to rearrange or rename as required.
 *
 * Note that [as]/[to] only changes the view of the data that is passed into typed operations,
 * such as [map], and does not eagerly project away any columns that are not present in
 * the specified class.
 *
 * @see as as alias for [to]
 */
inline fun <reified R> Dataset<*>.to(): Dataset<R> = `as`(encoder<R>())

/**
 * (Kotlin-specific)
 * Applies a function [func] to all rows.
 */
inline fun <reified T> Dataset<T>.forEach(noinline func: (T) -> Unit): Unit = foreach(ForeachFunction(func))

/**
 * (Kotlin-specific)
 * Runs [func] on each partition of this Dataset.
 */
inline fun <reified T> Dataset<T>.forEachPartition(noinline func: (Iterator<T>) -> Unit): Unit =
    foreachPartition(ForeachPartitionFunction(func))

/**
 * It's hard to call `Dataset.debugCodegen` from kotlin, so here is utility for that
 */
fun <T> Dataset<T>.debugCodegen(): Dataset<T> = also { KSparkExtensions.debugCodegen(it) }

/**
 * Returns the Spark context associated with this Spark session.
 */
val SparkSession.sparkContext: SparkContext
    get() = KSparkExtensions.sparkContext(this)

/**
 * It's hard to call `Dataset.debug` from kotlin, so here is utility for that
 */
fun <T> Dataset<T>.debug(): Dataset<T> = also { KSparkExtensions.debug(it) }

@Suppress("FunctionName")
@Deprecated(
    message = "Changed to \"`===`\" to better reflect Scala API.",
    replaceWith = ReplaceWith("this `===` c"),
    level = DeprecationLevel.ERROR,
)
infix fun Column.`==`(c: Column) = `$eq$eq$eq`(c)

/**
 * Unary minus, i.e. negate the expression.
 * ```
 *   // Scala: select the amount column and negates all values.
 *   df.select( -df("amount") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.select( -df("amount") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.select( negate(col("amount") );
 * ```
 */
operator fun Column.unaryMinus(): Column = `unary_$minus`()

/**
 * Inversion of boolean expression, i.e. NOT.
 * ```
 *   // Scala: select rows that are not active (isActive === false)
 *   df.filter( !df("isActive") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.filter( !df("amount") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.filter( not(df.col("isActive")) );
 * ```
 */
operator fun Column.not(): Column = `unary_$bang`()

/**
 * Equality test.
 * ```
 *   // Scala:
 *   df.filter( df("colA") === df("colB") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.filter( df("colA") eq df("colB") )
 *   // or
 *   df.filter( df("colA") `===` df("colB") )
 *
 *   // Java
 *   import static org.apache.spark.sql.functions.*;
 *   df.filter( col("colA").equalTo(col("colB")) );
 * ```
 */
infix fun Column.eq(other: Any): Column = `$eq$eq$eq`(other)

/**
 * Equality test.
 * ```
 *   // Scala:
 *   df.filter( df("colA") === df("colB") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.filter( df("colA") eq df("colB") )
 *   // or
 *   df.filter( df("colA") `===` df("colB") )
 *
 *   // Java
 *   import static org.apache.spark.sql.functions.*;
 *   df.filter( col("colA").equalTo(col("colB")) );
 * ```
 */
infix fun Column.`===`(other: Any): Column = `$eq$eq$eq`(other)

/**
 * Inequality test.
 * ```
 *   // Scala:
 *   df.select( df("colA") =!= df("colB") )
 *   df.select( !(df("colA") === df("colB")) )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.select( df("colA") neq df("colB") )
 *   df.select( !(df("colA") eq df("colB")) )
 *   // or
 *   df.select( df("colA") `=!=` df("colB") )
 *   df.select( !(df("colA") `===` df("colB")) )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.select( col("colA").notEqual(col("colB")) );
 * ```
 */
infix fun Column.neq(other: Any): Column = `$eq$bang$eq`(other)

/**
 * Inequality test.
 * ```
 *   // Scala:
 *   df.select( df("colA") =!= df("colB") )
 *   df.select( !(df("colA") === df("colB")) )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.select( df("colA") neq df("colB") )
 *   df.select( !(df("colA") eq df("colB")) )
 *   // or
 *   df.select( df("colA") `=!=` df("colB") )
 *   df.select( !(df("colA") `===` df("colB")) )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.select( col("colA").notEqual(col("colB")) );
 * ```
 */
infix fun Column.`=!=`(other: Any): Column = `$eq$bang$eq`(other)

/**
 * Greater than.
 * ```
 *   // Scala: The following selects people older than 21.
 *   people.select( people("age") > 21 )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("age") gt 21 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("age").gt(21) );
 * ```
 */
infix fun Column.gt(other: Any): Column = `$greater`(other)

/**
 * Less than.
 * ```
 *   // Scala: The following selects people younger than 21.
 *   people.select( people("age") < 21 )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("age") lt 21 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("age").lt(21) );
 * ```
 */
infix fun Column.lt(other: Any): Column = `$less`(other)

/**
 * Less than or equal to.
 * ```
 *   // Scala: The following selects people age 21 or younger than 21.
 *   people.select( people("age") <= 21 )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("age") leq 21 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("age").leq(21) );
 * ```
 */
infix fun Column.leq(other: Any): Column = `$less$eq`(other)

/**
 * Greater than or equal to an expression.
 * ```
 *   // Scala: The following selects people age 21 or older than 21.
 *   people.select( people("age") >= 21 )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("age") geq 21 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("age").geq(21) );
 * ```
 */
infix fun Column.geq(other: Any): Column = `$greater$eq`(other)

/**
 * True if the current column is in the given [range].
 * ```
 *   // Scala:
 *   df.where( df("colA").between(1, 5) )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.where( df("colA") inRangeOf 1..5 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.where( df.col("colA").between(1, 5) );
 * ```
 */
infix fun Column.inRangeOf(range: ClosedRange<*>): Column = between(range.start, range.endInclusive)

/**
 * Boolean OR.
 * ```
 *   // Scala: The following selects people that are in school or employed.
 *   people.filter( people("inSchool") || people("isEmployed") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.filter( people("inSchool") or people("isEmployed") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.filter( people.col("inSchool").or(people.col("isEmployed")) );
 * ```
 */
infix fun Column.or(other: Any): Column = `$bar$bar`(other)

/**
 * Boolean AND.
 * ```
 *   // Scala: The following selects people that are in school and employed at the same time.
 *   people.select( people("inSchool") && people("isEmployed") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("inSchool") and people("isEmployed") )
 *   // or
 *   people.select( people("inSchool") `&&` people("isEmployed") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("inSchool").and(people.col("isEmployed")) );
 * ```
 */
infix fun Column.and(other: Any): Column = `$amp$amp`(other)

/**
 * Boolean AND.
 * ```
 *   // Scala: The following selects people that are in school and employed at the same time.
 *   people.select( people("inSchool") && people("isEmployed") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("inSchool") and people("isEmployed") )
 *   // or
 *   people.select( people("inSchool") `&&` people("isEmployed") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("inSchool").and(people.col("isEmployed")) );
 * ```
 */
infix fun Column.`&&`(other: Any): Column = `$amp$amp`(other)

/**
 * Multiplication of this expression and another expression.
 * ```
 *   // Scala: The following multiplies a person's height by their weight.
 *   people.select( people("height") * people("weight") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("height") * people("weight") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("height").multiply(people.col("weight")) );
 * ```
 */
operator fun Column.times(other: Any): Column = `$times`(other)

/**
 * Division this expression by another expression.
 * ```
 *   // Scala: The following divides a person's height by their weight.
 *   people.select( people("height") / people("weight") )
 *
 *   // Kotlin
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("height") / people("weight") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("height").divide(people.col("weight")) );
 * ```
 */
operator fun Column.div(other: Any): Column = `$div`(other)

/**
 * Modulo (a.k.a. remainder) expression.
 * ```
 *   // Scala:
 *   df.where( df("colA") % 2 === 0 )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.where( df("colA") % 2 eq 0 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.where( df.col("colA").mod(2).equalTo(0) );
 * ```
 */
operator fun Column.rem(other: Any): Column = `$percent`(other)

/**
 * An expression that gets an item at position `ordinal` out of an array,
 * or gets a value by key `key` in a `MapType`.
 * ```
 *   // Scala:
 *   df.where( df("arrayColumn").getItem(0) === 5 )
 *
 *   // Kotlin
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.where( df("arrayColumn")[0] eq 5 )
 *
 *   // Java
 *   import static org.apache.spark.sql.functions.*;
 *   df.where( df.col("arrayColumn").getItem(0).equalTo(5) );
 * ```
 */
operator fun Column.get(key: Any): Column = getItem(key)

/**
 * Creates a [Column] of literal value.
 *
 * The passed in object is returned directly if it is already a [Column].
 * If the object is a Scala Symbol, it is converted into a [Column] also.
 * Otherwise, a new [Column] is created to represent the literal value.
 *
 * This is just a shortcut to the function from [org.apache.spark.sql.functions].
 * For all the functions, simply add `import org.apache.spark.sql.functions.*` to your file.
 */
fun lit(a: Any): Column = functions.lit(a)

/**
 * Provides a type hint about the expected return value of this column. This information can
 * be used by operations such as `select` on a [Dataset] to automatically convert the
 * results into the correct JVM types.
 *
 * ```
 * val df: Dataset<Row> = ...
 * val typedColumn: Dataset<Int> = df.selectTyped( col("a").`as`<Int>() )
 * ```
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T> Column.`as`(): TypedColumn<Any, T> = `as`(encoder<T>())

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
inline fun <reified L : Any?, reified R : Any?> Dataset<L>.fullJoin(
    right: Dataset<R>,
    col: Column,
): Dataset<Pair<L?, R?>> {
    return joinWith(right, col, "full").map { it._1 to it._2 }
}

/**
 * Alias for [Dataset.sort] which forces user to provide sorted columns from the source dataset
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
 * it may be useful in many situations, for example, when one needs to write data to several targets
 * ```kotlin
 * ds.withCached {
 *   write()
 *      .also { it.orc("First destination") }
 *      .also { it.avro("Second destination") }
 * }
 * ```
 *
 * @param blockingUnpersist if execution should be blocked until everything persisted will be deleted
 * @param executeOnCached Block which should be executed on cached dataset.
 * @return result of block execution for further usage. It may be anything including source or new dataset
 */
inline fun <reified T, R> Dataset<T>.withCached(
    blockingUnpersist: Boolean = false,
    executeOnCached: Dataset<T>.() -> R,
): R {
    val cached = this.cache()
    return cached.executeOnCached().also { cached.unpersist(blockingUnpersist) }
}

/**
 * Collects the dataset as list where each item has been mapped to type [T].
 */
inline fun <reified T> Dataset<*>.toList(): List<T> = to<T>().collectAsList() as List<T>

/**
 * Collects the dataset as Array where each item has been mapped to type [T].
 */
inline fun <reified T> Dataset<*>.toArray(): Array<T> = to<T>().collect() as Array<T>

/**
 * Selects column based on the column name and returns it as a [Column].
 *
 * @note The column name can also reference to a nested column like `a.b`.
 */
operator fun <T> Dataset<T>.invoke(colName: String): Column = col(colName)

/**
 * Helper function to quickly get a [TypedColumn] (or [Column]) from a dataset in a refactor-safe manner.
 * ```kotlin
 *    val dataset: Dataset<YourClass> = ...
 *    val columnA: TypedColumn<YourClass, TypeOfA> = dataset.col(YourClass::a)
 * ```
 * @see invoke
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified U> Dataset<T>.col(column: KProperty1<T, U>): TypedColumn<T, U> =
    col(column.name).`as`<U>() as TypedColumn<T, U>

/**
 * Returns a [Column] based on the given class attribute, not connected to a dataset.
 * ```kotlin
 *    val dataset: Dataset<YourClass> = ...
 *    val new: Dataset<Pair<TypeOfA, TypeOfB>> = dataset.select( col(YourClass::a), col(YourClass::b) )
 * ```
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified U> col(column: KProperty1<T, U>): TypedColumn<T, U> =
    functions.col(column.name).`as`<U>() as TypedColumn<T, U>

/**
 * Helper function to quickly get a [TypedColumn] (or [Column]) from a dataset in a refactor-safe manner.
 * ```kotlin
 *    val dataset: Dataset<YourClass> = ...
 *    val columnA: TypedColumn<YourClass, TypeOfA> = dataset(YourClass::a)
 * ```
 * @see col
 */
inline operator fun <reified T, reified U> Dataset<T>.invoke(column: KProperty1<T, U>): TypedColumn<T, U> = col(column)

/**
 * Allows to sort data class dataset on one or more of the properties of the data class.
 * ```kotlin
 * val sorted: Dataset<YourClass> = unsorted.sort(YourClass::a)
 * val sorted2: Dataset<YourClass> = unsorted.sort(YourClass::a, YourClass::b)
 * ```
 */
fun <T> Dataset<T>.sort(col: KProperty1<T, *>, vararg cols: KProperty1<T, *>): Dataset<T> =
    sort(col.name, *cols.map { it.name }.toTypedArray())

/**
 * Alternative to [Dataset.show] which returns source dataset.
 * Useful for debug purposes when you need to view content of a dataset as an intermediate operation
 */
fun <T> Dataset<T>.showDS(numRows: Int = 20, truncate: Boolean = true) = apply { show(numRows, truncate) }

/**
 * Returns a new Dataset by computing the given [Column] expressions for each element.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified U1> Dataset<T>.selectTyped(
    c1: TypedColumn<out Any, U1>,
): Dataset<U1> = select(c1 as TypedColumn<T, U1>)

/**
 * Returns a new Dataset by computing the given [Column] expressions for each element.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified U1, reified U2> Dataset<T>.selectTyped(
    c1: TypedColumn<out Any, U1>,
    c2: TypedColumn<out Any, U2>,
): Dataset<Pair<U1, U2>> =
    select(
        c1 as TypedColumn<T, U1>,
        c2 as TypedColumn<T, U2>,
    ).map { Pair(it._1(), it._2()) }

/**
 * Returns a new Dataset by computing the given [Column] expressions for each element.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified U1, reified U2, reified U3> Dataset<T>.selectTyped(
    c1: TypedColumn<out Any, U1>,
    c2: TypedColumn<out Any, U2>,
    c3: TypedColumn<out Any, U3>,
): Dataset<Triple<U1, U2, U3>> =
    select(
        c1 as TypedColumn<T, U1>,
        c2 as TypedColumn<T, U2>,
        c3 as TypedColumn<T, U3>,
    ).map { Triple(it._1(), it._2(), it._3()) }

/**
 * Returns a new Dataset by computing the given [Column] expressions for each element.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified U1, reified U2, reified U3, reified U4> Dataset<T>.selectTyped(
    c1: TypedColumn<out Any, U1>,
    c2: TypedColumn<out Any, U2>,
    c3: TypedColumn<out Any, U3>,
    c4: TypedColumn<out Any, U4>,
): Dataset<Arity4<U1, U2, U3, U4>> =
    select(
        c1 as TypedColumn<T, U1>,
        c2 as TypedColumn<T, U2>,
        c3 as TypedColumn<T, U3>,
        c4 as TypedColumn<T, U4>,
    ).map { Arity4(it._1(), it._2(), it._3(), it._4()) }

/**
 * Returns a new Dataset by computing the given [Column] expressions for each element.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified U1, reified U2, reified U3, reified U4, reified U5> Dataset<T>.selectTyped(
    c1: TypedColumn<out Any, U1>,
    c2: TypedColumn<out Any, U2>,
    c3: TypedColumn<out Any, U3>,
    c4: TypedColumn<out Any, U4>,
    c5: TypedColumn<out Any, U5>,
): Dataset<Arity5<U1, U2, U3, U4, U5>> =
    select(
        c1 as TypedColumn<T, U1>,
        c2 as TypedColumn<T, U2>,
        c3 as TypedColumn<T, U3>,
        c4 as TypedColumn<T, U4>,
        c5 as TypedColumn<T, U5>,
    ).map { Arity5(it._1(), it._2(), it._3(), it._4(), it._5()) }


/**
 * Not meant to be used by the user explicitly.
 *
 * This function generates the DataType schema for supported classes, including Kotlin data classes, [Map],
 * [Iterable], [Product], [Array], and combinations of those.
 *
 * It's mainly used by [generateEncoder]/[encoder].
 */
@OptIn(ExperimentalStdlibApi::class)
fun schema(type: KType, map: Map<String, KType> = mapOf()): DataType {
    if (type.classifier == ByteArray::class) return KComplexTypeWrapper(
        DataTypes.BinaryType,
        ByteArray::class.java,
        type.isMarkedNullable,
    )

    val primitiveSchema = knownDataTypes[type.classifier]
    if (primitiveSchema != null) return KSimpleTypeWrapper(
        primitiveSchema,
        (type.classifier!! as KClass<*>).java,
        type.isMarkedNullable
    )
    val klass = type.classifier as? KClass<*> ?: throw IllegalArgumentException("Unsupported type $type")
    val args = type.arguments

    val types = transitiveMerge(map, klass.typeParameters.zip(args).map {
        it.first.name to it.second.type!!
    }.toMap())
    return when {
        klass.isSubclassOf(Enum::class) -> {
            KSimpleTypeWrapper(DataTypes.StringType, klass.java, type.isMarkedNullable)
        }
        klass.isSubclassOf(Iterable::class) || klass.java.isArray -> {
            val listParam = if (klass.java.isArray) {
                when (klass) {
                    IntArray::class -> typeOf<Int>()
                    LongArray::class -> typeOf<Long>()
                    FloatArray::class -> typeOf<Float>()
                    DoubleArray::class -> typeOf<Double>()
                    BooleanArray::class -> typeOf<Boolean>()
                    ShortArray::class -> typeOf<Short>()
//                    ByteArray::class -> typeOf<Byte>()
                    else -> types.getValue(klass.typeParameters[0].name)
                }
            } else types.getValue(klass.typeParameters[0].name)
            KComplexTypeWrapper(
                DataTypes.createArrayType(schema(listParam, types), listParam.isMarkedNullable),
                klass.java,
                type.isMarkedNullable
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
                type.isMarkedNullable
            )
        }
        klass.isData -> {
            val structType = StructType(
                klass
                    .primaryConstructor!!
                    .parameters
                    .filter { it.findAnnotation<Transient>() == null }
                    .map {
                        val projectedType = types[it.type.toString()] ?: it.type
                        val propertyDescriptor = PropertyDescriptor(
                            it.name,
                            klass.java,
                            "is" + it.name?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            null
                        )
                        KStructField(
                            propertyDescriptor.readMethod.name,
                            StructField(
                                it.name,
                                schema(projectedType, types),
                                projectedType.isMarkedNullable,
                                Metadata.empty()
                            )
                        )
                    }
                    .toTypedArray()
            )
            KDataTypeWrapper(structType, klass.java, true)
        }
        klass.isSubclassOf(Product::class) -> {
            val params = type.arguments.mapIndexed { i, it ->
                "_${i + 1}" to it.type!!
            }

            val structType = DataTypes.createStructType(
                params.map { (fieldName, fieldType) ->
                    val dataType = schema(fieldType, types)
                    KStructField(
                        fieldName,
                        StructField(fieldName, dataType, fieldType.isMarkedNullable, Metadata.empty())
                    )
                }.toTypedArray()
            )

            KComplexTypeWrapper(structType, klass.java, true)
        }
        else -> throw IllegalArgumentException("$type is unsupported")
    }
}

/**
 * The entry point to programming Spark with the Dataset and DataFrame API.
 *
 * @see org.apache.spark.sql.SparkSession
 */
typealias SparkSession = org.apache.spark.sql.SparkSession

/**
 * Control our logLevel. This overrides any user-defined log settings.
 * @param level The desired log level as [SparkLogLevel].
 */
fun SparkContext.setLogLevel(level: SparkLogLevel): Unit = setLogLevel(level.name)

enum class SparkLogLevel {
    ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
}

private val knownDataTypes: Map<KClass<out Any>, DataType> = mapOf(
    Byte::class to DataTypes.ByteType,
    Short::class to DataTypes.ShortType,
    Int::class to DataTypes.IntegerType,
    Long::class to DataTypes.LongType,
    Boolean::class to DataTypes.BooleanType,
    Float::class to DataTypes.FloatType,
    Double::class to DataTypes.DoubleType,
    String::class to DataTypes.StringType,
    LocalDate::class to DataTypes.DateType,
    Date::class to DataTypes.DateType,
    Timestamp::class to DataTypes.TimestampType,
    Instant::class to DataTypes.TimestampType,
    ByteArray::class to DataTypes.BinaryType,
    Decimal::class to DecimalType.SYSTEM_DEFAULT(),
    CalendarInterval::class to DataTypes.CalendarIntervalType,
    Nothing::class to DataTypes.NullType,
)

private fun transitiveMerge(a: Map<String, KType>, b: Map<String, KType>): Map<String, KType> {
    return a + b.mapValues {
        a.getOrDefault(it.value.toString(), it.value)
    }
}

class Memoize1<in T, out R>(val f: (T) -> R) : (T) -> R {

    private val values = ConcurrentHashMap<T, R>()

    override fun invoke(x: T): R = values.getOrPut(x) { f(x) }
}

private fun <T, R> ((T) -> R).memoize(): (T) -> R = Memoize1(this)

private val memoizedSchema: (KType) -> DataType = { x: KType -> schema(x) }.memoize()
