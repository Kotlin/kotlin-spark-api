/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 3.0+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2021 JetBrains
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

/**
 * This file contains all Dataset helper functions.
 * This includes the creation of Datasets from arrays, lists, and RDDs,
 * as well as lots of extension functions which makes working with Datasets from Kotlin
 * possible/easier.
 */

package org.jetbrains.kotlinx.spark.api

import org.apache.spark.api.java.JavaRDDLike
import org.apache.spark.api.java.function.FlatMapFunction
import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.api.java.function.ForeachPartitionFunction
import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.api.java.function.ReduceFunction
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Column
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.KeyValueGroupedDataset
import org.apache.spark.sql.TypedColumn
import org.jetbrains.kotlinx.spark.extensions.KSparkExtensions
import scala.Tuple2
import scala.Tuple3
import scala.Tuple4
import scala.Tuple5
import kotlin.reflect.KProperty1


/**
 * Utility method to create dataset from list
 */
inline fun <reified T> SparkSession.toDS(list: List<T>): Dataset<T> =
    createDataset(list, encoder<T>())

/**
 * Utility method to create dataset from *array or vararg arguments
 */
inline fun <reified T> SparkSession.dsOf(vararg t: T): Dataset<T> =
    createDataset(listOf(*t), encoder<T>())

/**
 * Utility method to create dataset from list
 */
inline fun <reified T> List<T>.toDS(spark: SparkSession): Dataset<T> =
    spark.createDataset(this, encoder<T>())

/**
 * Utility method to create dataset from RDD
 */
inline fun <reified T> RDD<T>.toDS(spark: SparkSession): Dataset<T> =
    spark.createDataset(this, encoder<T>())

/**
 * Utility method to create dataset from JavaRDD
 */
inline fun <reified T> JavaRDDLike<T, *>.toDS(spark: SparkSession): Dataset<T> =
    spark.createDataset(this.rdd(), encoder<T>())

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
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
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
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
inline fun <T1, reified T2> Dataset<Arity2<T1, T2>>.takeValues(): Dataset<T2> = map { it._2 }

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
 * It's hard to call `Dataset.debug` from kotlin, so here is utility for that
 */
fun <T> Dataset<T>.debug(): Dataset<T> = also { KSparkExtensions.debug(it) }


/**
 * Alias for [Dataset.joinWith] which passes "left" argument
 * and respects the fact that in result of left join right relation is nullable
 *
 * @receiver left dataset
 * @param right right dataset
 * @param col join condition
 *
 * @return dataset of [Tuple2] where right element is forced nullable
 */
inline fun <reified L, reified R : Any?> Dataset<L>.leftJoin(right: Dataset<R>, col: Column): Dataset<Tuple2<L, R?>> =
    joinWith(right, col, "left")

/**
 * Alias for [Dataset.joinWith] which passes "right" argument
 * and respects the fact that in result of right join left relation is nullable
 *
 * @receiver left dataset
 * @param right right dataset
 * @param col join condition
 *
 * @return dataset of [Tuple2] where left element is forced nullable
 */
inline fun <reified L : Any?, reified R> Dataset<L>.rightJoin(right: Dataset<R>, col: Column): Dataset<Tuple2<L?, R>> =
    joinWith(right, col, "right")

/**
 * Alias for [Dataset.joinWith] which passes "inner" argument
 *
 * @receiver left dataset
 * @param right right dataset
 * @param col join condition
 *
 * @return resulting dataset of [Tuple2]
 */
inline fun <reified L, reified R> Dataset<L>.innerJoin(right: Dataset<R>, col: Column): Dataset<Tuple2<L, R>> =
    joinWith(right, col, "inner")

/**
 * Alias for [Dataset.joinWith] which passes "full" argument
 * and respects the fact that in result of join any element of resulting tuple is nullable
 *
 * @receiver left dataset
 * @param right right dataset
 * @param col join condition
 *
 * @return dataset of [Tuple2] where both elements are forced nullable
 */
inline fun <reified L : Any?, reified R : Any?> Dataset<L>.fullJoin(
    right: Dataset<R>,
    col: Column,
): Dataset<Tuple2<L?, R?>> = joinWith(right, col, "full")

/**
 * Alias for [Dataset.sort] which forces user to provide sorted columns from the source dataset
 *
 * @receiver source [Dataset]
 * @param columns producer of sort columns
 * @return sorted [Dataset]
 */
inline fun <reified T> Dataset<T>.sort(columns: (Dataset<T>) -> Array<Column>): Dataset<T> = sort(*columns(this))

/** Returns a dataset sorted by the first (`_1`) value of each [Tuple2] inside. */
@JvmName("sortByTuple2Key")
fun <T1, T2> Dataset<Tuple2<T1, T2>>.sortByKey(): Dataset<Tuple2<T1, T2>> = sort("_1")

/** Returns a dataset sorted by the second (`_2`) value of each [Tuple2] inside. */
@JvmName("sortByTuple2Value")
fun <T1, T2> Dataset<Tuple2<T1, T2>>.sortByValue(): Dataset<Tuple2<T1, T2>> = sort("_2")

/** Returns a dataset sorted by the first (`_1`) value of each [Arity2] inside. */
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
@JvmName("sortByArity2Key")
fun <T1, T2> Dataset<Arity2<T1, T2>>.sortByKey(): Dataset<Arity2<T1, T2>> = sort("_1")

/** Returns a dataset sorted by the second (`_2`) value of each [Arity2] inside. */
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
@JvmName("sortByArity2Value")
fun <T1, T2> Dataset<Arity2<T1, T2>>.sortByValue(): Dataset<Arity2<T1, T2>> = sort("_2")

/** Returns a dataset sorted by the first (`first`) value of each [Pair] inside. */
@JvmName("sortByPairKey")
fun <T1, T2> Dataset<Pair<T1, T2>>.sortByKey(): Dataset<Pair<T1, T2>> = sort("first")

/** Returns a dataset sorted by the second (`second`) value of each [Pair] inside. */
@JvmName("sortByPairValue")
fun <T1, T2> Dataset<Pair<T1, T2>>.sortByValue(): Dataset<Pair<T1, T2>> = sort("second")

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
fun <T> Dataset<T>.showDS(numRows: Int = 20, truncate: Boolean = true): Dataset<T> = apply { show(numRows, truncate) }

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
): Dataset<Tuple2<U1, U2>> =
    select(
        c1 as TypedColumn<T, U1>,
        c2 as TypedColumn<T, U2>,
    )

/**
 * Returns a new Dataset by computing the given [Column] expressions for each element.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified U1, reified U2, reified U3> Dataset<T>.selectTyped(
    c1: TypedColumn<out Any, U1>,
    c2: TypedColumn<out Any, U2>,
    c3: TypedColumn<out Any, U3>,
): Dataset<Tuple3<U1, U2, U3>> =
    select(
        c1 as TypedColumn<T, U1>,
        c2 as TypedColumn<T, U2>,
        c3 as TypedColumn<T, U3>,
    )

/**
 * Returns a new Dataset by computing the given [Column] expressions for each element.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified U1, reified U2, reified U3, reified U4> Dataset<T>.selectTyped(
    c1: TypedColumn<out Any, U1>,
    c2: TypedColumn<out Any, U2>,
    c3: TypedColumn<out Any, U3>,
    c4: TypedColumn<out Any, U4>,
): Dataset<Tuple4<U1, U2, U3, U4>> =
    select(
        c1 as TypedColumn<T, U1>,
        c2 as TypedColumn<T, U2>,
        c3 as TypedColumn<T, U3>,
        c4 as TypedColumn<T, U4>,
    )

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
): Dataset<Tuple5<U1, U2, U3, U4, U5>> =
    select(
        c1 as TypedColumn<T, U1>,
        c2 as TypedColumn<T, U2>,
        c3 as TypedColumn<T, U3>,
        c4 as TypedColumn<T, U4>,
        c5 as TypedColumn<T, U5>,
    )

