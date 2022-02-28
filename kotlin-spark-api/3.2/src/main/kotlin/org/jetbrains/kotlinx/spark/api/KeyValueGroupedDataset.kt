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
package org.jetbrains.kotlinx.spark.api

import org.apache.spark.api.java.function.CoGroupFunction
import org.apache.spark.api.java.function.FlatMapGroupsFunction
import org.apache.spark.api.java.function.FlatMapGroupsWithStateFunction
import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.api.java.function.MapGroupsFunction
import org.apache.spark.api.java.function.MapGroupsWithStateFunction
import org.apache.spark.api.java.function.ReduceFunction
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Encoder
import org.apache.spark.sql.KeyValueGroupedDataset
import org.apache.spark.sql.streaming.GroupState
import org.apache.spark.sql.streaming.GroupStateTimeout
import org.apache.spark.sql.streaming.OutputMode


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