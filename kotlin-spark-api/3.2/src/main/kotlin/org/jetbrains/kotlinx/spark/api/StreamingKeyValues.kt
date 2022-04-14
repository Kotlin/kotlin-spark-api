/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 3.2+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2022 JetBrains
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

import org.apache.spark.HashPartitioner
import org.apache.spark.Partitioner
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.Optional
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.StateSpec
import org.apache.spark.streaming.api.java.JavaDStream
import org.apache.spark.streaming.api.java.JavaMapWithStateDStream
import org.apache.spark.streaming.api.java.JavaPairDStream
import scala.Tuple2


fun <K, V> JavaDStream<Tuple2<K, V>>.toPairDStream(): JavaPairDStream<K, V> =
    JavaPairDStream.fromJavaDStream(this)

fun <K, V> JavaPairDStream<K, V>.toTupleDStream(): JavaDStream<Tuple2<K, V>> =
    toJavaDStream()

fun <K, V> JavaRDD<Tuple2<K, V>>.toPairRDD(): JavaPairRDD<K, V> = 
    JavaPairRDD.fromJavaRDD(this)

fun <K, V> JavaPairRDD<K, V>.toTupleRDD(): JavaRDD<Tuple2<K, V>> =
    JavaPairRDD.toRDD(this).toJavaRDD()


/**
 * Return a new DStream by applying `groupByKey` to each RDD. Hash partitioning is used to
 * generate the RDDs with `numPartitions` partitions.
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.groupByKey(
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
): JavaDStream<Tuple2<K, Iterable<V>>> =
    toPairDStream()
        .groupByKey(numPartitions)
        .toTupleDStream()

/**
 * Return a new DStream by applying `groupByKey` on each RDD. The supplied
 * org.apache.spark.Partitioner is used to control the partitioning of each RDD.
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.groupByKey(partitioner: Partitioner): JavaDStream<Tuple2<K, Iterable<V>>> =
    toPairDStream()
        .groupByKey(partitioner)
        .toTupleDStream()

/**
 * Return a new DStream by applying `reduceByKey` to each RDD. The values for each key are
 * merged using the supplied reduce function. Hash partitioning is used to generate the RDDs
 * with `numPartitions` partitions.
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.reduceByKey(
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
    reduceFunc: (V, V) -> V,
): JavaDStream<Tuple2<K, V>> =
    toPairDStream()
        .reduceByKey(reduceFunc, numPartitions)
        .toTupleDStream()

/**
 * Return a new DStream by applying `reduceByKey` to each RDD. The values for each key are
 * merged using the supplied reduce function. org.apache.spark.Partitioner is used to control
 * the partitioning of each RDD.
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.reduceByKey(
    partitioner: Partitioner,
    reduceFunc: (V, V) -> V,
): JavaDStream<Tuple2<K, V>> =
    toPairDStream()
        .reduceByKey(reduceFunc, partitioner)
        .toTupleDStream()

/**
 * Combine elements of each key in DStream's RDDs using custom functions. This is similar to the
 * combineByKey for RDDs. Please refer to combineByKey in
 * org.apache.spark.rdd.PairRDDFunctions in the Spark core documentation for more information.
 */
fun <K, V, C> JavaDStream<Tuple2<K, V>>.combineByKey(
    createCombiner: (V) -> C,
    mergeValue: (C, V) -> C,
    mergeCombiner: (C, C) -> C,
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
    mapSideCombine: Boolean = true,
): JavaDStream<Tuple2<K, C>> =
    toPairDStream()
        .combineByKey(createCombiner, mergeValue, mergeCombiner, HashPartitioner(numPartitions), mapSideCombine)
        .toTupleDStream()

/**
 * Combine elements of each key in DStream's RDDs using custom functions. This is similar to the
 * combineByKey for RDDs. Please refer to combineByKey in
 * org.apache.spark.rdd.PairRDDFunctions in the Spark core documentation for more information.
 */
fun <K, V, C> JavaDStream<Tuple2<K, V>>.combineByKey(
    createCombiner: (V) -> C,
    mergeValue: (C, V) -> C,
    mergeCombiner: (C, C) -> C,
    partitioner: Partitioner,
    mapSideCombine: Boolean = true,
): JavaDStream<Tuple2<K, C>> =
    toPairDStream()
        .combineByKey(createCombiner, mergeValue, mergeCombiner, partitioner, mapSideCombine)
        .toTupleDStream()

/**
 * Return a new DStream by applying `groupByKey` over a sliding window on `this` DStream.
 * Similar to `DStream.groupByKey()`, but applies it over a sliding window.
 * Hash partitioning is used to generate the RDDs with `numPartitions` partitions.
 * @param windowDuration width of the window; must be a multiple of this DStream's
 *                       batching interval
 * @param slideDuration  sliding interval of the window (i.e., the interval after which
 *                       the new DStream will generate RDDs); must be a multiple of this
 *                       DStream's batching interval
 * @param numPartitions  number of partitions of each RDD in the new DStream; if not specified
 *                       then Spark's default number of partitions will be used
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.groupByKeyAndWindow(
    windowDuration: Duration,
    slideDuration: Duration = dstream().slideDuration(),
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
): JavaDStream<Tuple2<K, Iterable<V>>> =
    toPairDStream()
        .groupByKeyAndWindow(windowDuration, slideDuration, numPartitions)
        .toTupleDStream()

/**
 * Create a new DStream by applying `groupByKey` over a sliding window on `this` DStream.
 * Similar to `DStream.groupByKey()`, but applies it over a sliding window.
 * @param windowDuration width of the window; must be a multiple of this DStream's
 *                       batching interval
 * @param slideDuration  sliding interval of the window (i.e., the interval after which
 *                       the new DStream will generate RDDs); must be a multiple of this
 *                       DStream's batching interval
 * @param partitioner    partitioner for controlling the partitioning of each RDD in the new
 *                       DStream.
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.groupByKeyAndWindow(
    windowDuration: Duration,
    slideDuration: Duration = dstream().slideDuration(),
    partitioner: Partitioner,
): JavaDStream<Tuple2<K, Iterable<V>>> =
    toPairDStream()
        .groupByKeyAndWindow(windowDuration, slideDuration, partitioner)
        .toTupleDStream()

/**
 * Return a new DStream by applying `reduceByKey` over a sliding window. This is similar to
 * `DStream.reduceByKey()` but applies it over a sliding window. Hash partitioning is used to
 * generate the RDDs with `numPartitions` partitions.
 * @param reduceFunc associative and commutative reduce function
 * @param windowDuration width of the window; must be a multiple of this DStream's
 *                       batching interval
 * @param slideDuration  sliding interval of the window (i.e., the interval after which
 *                       the new DStream will generate RDDs); must be a multiple of this
 *                       DStream's batching interval
 * @param numPartitions  number of partitions of each RDD in the new DStream.
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.reduceByKeyAndWindow(
    windowDuration: Duration,
    slideDuration: Duration = dstream().slideDuration(),
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
    reduceFunc: (V, V) -> V,
): JavaDStream<Tuple2<K, V>> =
    toPairDStream()
        .reduceByKeyAndWindow(reduceFunc, windowDuration, slideDuration, numPartitions)
        .toTupleDStream()

/**
 * Return a new DStream by applying `reduceByKey` over a sliding window. Similar to
 * `DStream.reduceByKey()`, but applies it over a sliding window.
 * @param reduceFunc associative and commutative reduce function
 * @param windowDuration width of the window; must be a multiple of this DStream's
 *                       batching interval
 * @param slideDuration  sliding interval of the window (i.e., the interval after which
 *                       the new DStream will generate RDDs); must be a multiple of this
 *                       DStream's batching interval
 * @param partitioner    partitioner for controlling the partitioning of each RDD
 *                       in the new DStream.
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.reduceByKeyAndWindow(
    windowDuration: Duration,
    slideDuration: Duration = dstream().slideDuration(),
    partitioner: Partitioner,
    reduceFunc: (V, V) -> V,
): JavaDStream<Tuple2<K, V>> =
    toPairDStream()
        .reduceByKeyAndWindow(reduceFunc, windowDuration, slideDuration, partitioner)
        .toTupleDStream()

/**
 * Return a new DStream by applying incremental `reduceByKey` over a sliding window.
 * The reduced value of over a new window is calculated using the old window's reduced value :
 *  1. reduce the new values that entered the window (e.g., adding new counts)
 *
 *  2. "inverse reduce" the old values that left the window (e.g., subtracting old counts)
 *
 * This is more efficient than reduceByKeyAndWindow without "inverse reduce" function.
 * However, it is applicable to only "invertible reduce functions".
 * Hash partitioning is used to generate the RDDs with Spark's default number of partitions.
 * @param reduceFunc associative and commutative reduce function
 * @param invReduceFunc inverse reduce function; such that for all y, invertible x:
 *                      `invReduceFunc(reduceFunc(x, y), x) = y`
 * @param windowDuration width of the window; must be a multiple of this DStream's
 *                       batching interval
 * @param slideDuration  sliding interval of the window (i.e., the interval after which
 *                       the new DStream will generate RDDs); must be a multiple of this
 *                       DStream's batching interval
 * @param filterFunc     Optional function to filter expired key-value pairs;
 *                       only pairs that satisfy the function are retained
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.reduceByKeyAndWindow(
    invReduceFunc: (V, V) -> V,
    windowDuration: Duration,
    slideDuration: Duration = dstream().slideDuration(),
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
    filterFunc: ((Tuple2<K, V>) -> Boolean)? = null,
    reduceFunc: (V, V) -> V,
): JavaDStream<Tuple2<K, V>> =
    toPairDStream()
        .reduceByKeyAndWindow(
            /* reduceFunc = */ reduceFunc,
            /* invReduceFunc = */ invReduceFunc,
            /* windowDuration = */ windowDuration,
            /* slideDuration = */ slideDuration,
            /* numPartitions = */ numPartitions,
            /* filterFunc = */ filterFunc?.let {
                { tuple ->
                    filterFunc(tuple)
                }
            }
        )
        .toTupleDStream()

/**
 * Return a new DStream by applying incremental `reduceByKey` over a sliding window.
 * The reduced value of over a new window is calculated using the old window's reduced value :
 *  1. reduce the new values that entered the window (e.g., adding new counts)
 *  2. "inverse reduce" the old values that left the window (e.g., subtracting old counts)
 * This is more efficient than reduceByKeyAndWindow without "inverse reduce" function.
 * However, it is applicable to only "invertible reduce functions".
 * @param reduceFunc     associative and commutative reduce function
 * @param invReduceFunc  inverse reduce function
 * @param windowDuration width of the window; must be a multiple of this DStream's
 *                       batching interval
 * @param slideDuration  sliding interval of the window (i.e., the interval after which
 *                       the new DStream will generate RDDs); must be a multiple of this
 *                       DStream's batching interval
 * @param partitioner    partitioner for controlling the partitioning of each RDD in the new
 *                       DStream.
 * @param filterFunc     Optional function to filter expired key-value pairs;
 *                       only pairs that satisfy the function are retained
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.reduceByKeyAndWindow(
    invReduceFunc: (V, V) -> V,
    windowDuration: Duration,
    slideDuration: Duration = dstream().slideDuration(),
    partitioner: Partitioner,
    filterFunc: ((Tuple2<K, V>) -> Boolean)? = null,
    reduceFunc: (V, V) -> V,
): JavaDStream<Tuple2<K, V>> =
    toPairDStream()
        .reduceByKeyAndWindow(
            /* reduceFunc = */ reduceFunc,
            /* invReduceFunc = */ invReduceFunc,
            /* windowDuration = */ windowDuration,
            /* slideDuration = */ slideDuration,
            /* partitioner = */ partitioner,
            /* filterFunc = */ filterFunc?.let {
                { tuple ->
                    filterFunc(tuple)
                }
            }
        )
        .toTupleDStream()

/**
 * Return a [MapWithStateDStream] by applying a function to every key-value element of
 * `this` stream, while maintaining some state data for each unique key. The mapping function
 * and other specification (e.g. partitioners, timeouts, initial state data, etc.) of this
 * transformation can be specified using `StateSpec` class. The state data is accessible in
 * as a parameter of type `State` in the mapping function.
 *
 * Example of using `mapWithState`:
 * ```kotlin
 *    // A mapping function that maintains an integer state and return a String
 *    fun mappingFunction(key: String, value: Optional<Int>, state: State<Int>): Optional<String> {
 *      // Use state.exists(), state.get(), state.update() and state.remove()
 *      // to manage state, and return the necessary string
 *    }
 *
 *    val spec = StateSpec.function(::mappingFunction).numPartitions(10)
 *
 *    val mapWithStateDStream = keyValueDStream.mapWithState<StateType, MappedType>(spec)
 * ```
 *
 * @param spec          Specification of this transformation
 * @tparam StateType    Class type of the state data
 * @tparam MappedType   Class type of the mapped data
 */
fun <K, V, StateType, MappedType> JavaDStream<Tuple2<K, V>>.mapWithState(
    spec: StateSpec<K, V, StateType, MappedType>,
): JavaMapWithStateDStream<K, V, StateType, MappedType> =
    toPairDStream().mapWithState(spec)

/**
 * Return a new "state" DStream where the state for each key is updated by applying
 * the given function on the previous state of the key and the new values of each key.
 * In every batch the updateFunc will be called for each state even if there are no new values.
 * Hash partitioning is used to generate the RDDs with Spark's default number of partitions.
 * Note: Needs checkpoint directory to be set.
 * @param updateFunc State update function. If `this` function returns `null`, then
 *                   corresponding state key-value pair will be eliminated.
 * @tparam S State type
 */
fun <K, V, S> JavaDStream<Tuple2<K, V>>.updateStateByKey(
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
    updateFunc: (List<V>, S?) -> S?,
): JavaDStream<Tuple2<K, S>> =
    toPairDStream()
        .updateStateByKey(
            { list: List<V>, s: Optional<S> ->
                updateFunc(list, s.getOrNull()).toOptional()
            },
            numPartitions,
        )
        .toTupleDStream()

/**
 * Return a new "state" DStream where the state for each key is updated by applying
 * the given function on the previous state of the key and the new values of each key.
 * In every batch the updateFunc will be called for each state even if there are no new values.
 * [[org.apache.spark.Partitioner]] is used to control the partitioning of each RDD.
 * Note: Needs checkpoint directory to be set.
 * @param updateFunc State update function. Note, that this function may generate a different
 *                   tuple with a different key than the input key. Therefore keys may be removed
 *                   or added in this way. It is up to the developer to decide whether to
 *                   remember the partitioner despite the key being changed.
 * @param partitioner Partitioner for controlling the partitioning of each RDD in the new
 *                    DStream
 * @tparam S State type
 */
fun <K, V, S> JavaDStream<Tuple2<K, V>>.updateStateByKey(
    partitioner: Partitioner,
    updateFunc: (List<V>, S?) -> S?,
): JavaDStream<Tuple2<K, S>> =
    toPairDStream()
        .updateStateByKey(
            { list: List<V>, s: Optional<S> ->
                updateFunc(list, s.getOrNull()).toOptional()
            },
            partitioner,
        )
        .toTupleDStream()

/**
 * Return a new "state" DStream where the state for each key is updated by applying
 * the given function on the previous state of the key and the new values of the key.
 * org.apache.spark.Partitioner is used to control the partitioning of each RDD.
 * Note: Needs checkpoint directory to be set.
 * @param updateFunc State update function. If `this` function returns `null`, then
 *                   corresponding state key-value pair will be eliminated.
 * @param partitioner Partitioner for controlling the partitioning of each RDD in the new
 *                    DStream.
 * @param initialRDD initial state value of each key.
 * @tparam S State type
 */
fun <K, V, S> JavaDStream<Tuple2<K, V>>.updateStateByKey(
    partitioner: Partitioner,
    initialRDD: JavaRDD<Tuple2<K, S>>,
    updateFunc: (List<V>, S?) -> S?,
): JavaDStream<Tuple2<K, S>> =
    toPairDStream()
        .updateStateByKey(
            { list: List<V>, s: Optional<S> ->
                updateFunc(list, s.getOrNull()).toOptional()
            },
            partitioner,
            initialRDD.toPairRDD(),
        )
        .toTupleDStream()


/**
 * Return a new DStream by applying a map function to the value of each key-value pairs in
 * 'this' DStream without changing the key.
 */
fun <K, V, U> JavaDStream<Tuple2<K, V>>.mapValues(
    mapValuesFunc: (V) -> U,
): JavaDStream<Tuple2<K, U>> =
    toPairDStream()
        .mapValues(mapValuesFunc)
        .toTupleDStream()

/**
 * Return a new DStream by applying a flatmap function to the value of each key-value pairs in
 * 'this' DStream without changing the key.
 */
fun <K, V, U> JavaDStream<Tuple2<K, V>>.flatMapValues(
    flatMapValuesFunc: (V) -> Iterator<U>,
): JavaDStream<Tuple2<K, U>> =
    toPairDStream()
        .flatMapValues(flatMapValuesFunc)
        .toTupleDStream()

/**
 * Return a new DStream by applying 'cogroup' between RDDs of `this` DStream and `other` DStream.
 * Hash partitioning is used to generate the RDDs with `numPartitions` partitions.
 */
fun <K, V, W> JavaDStream<Tuple2<K, V>>.cogroup(
    other: JavaDStream<Tuple2<K, W>>,
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
): JavaDStream<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> =
    toPairDStream()
        .cogroup(
            other.toPairDStream(),
            numPartitions,
        )
        .toTupleDStream()


/**
 * Return a new DStream by applying 'cogroup' between RDDs of `this` DStream and `other` DStream.
 * The supplied org.apache.spark.Partitioner is used to partition the generated RDDs.
 */
fun <K, V, W> JavaDStream<Tuple2<K, V>>.cogroup(
    other: JavaDStream<Tuple2<K, W>>,
    partitioner: Partitioner,
): JavaDStream<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> =
    toPairDStream()
        .cogroup(
            other.toPairDStream(),
            partitioner,
        )
        .toTupleDStream()

/**
 * Return a new DStream by applying 'join' between RDDs of `this` DStream and `other` DStream.
 * Hash partitioning is used to generate the RDDs with `numPartitions` partitions.
 */
fun <K, V, W> JavaDStream<Tuple2<K, V>>.join(
    other: JavaDStream<Tuple2<K, W>>,
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
): JavaDStream<Tuple2<K, Tuple2<V, W>>> =
    toPairDStream()
        .join(
            other.toPairDStream(),
            numPartitions,
        )
        .toTupleDStream()

/**
 * Return a new DStream by applying 'join' between RDDs of `this` DStream and `other` DStream.
 * The supplied org.apache.spark.Partitioner is used to control the partitioning of each RDD.
 */
fun <K, V, W> JavaDStream<Tuple2<K, V>>.join(
    other: JavaDStream<Tuple2<K, W>>,
    partitioner: Partitioner,
): JavaDStream<Tuple2<K, Tuple2<V, W>>> =
    toPairDStream()
        .join(
            other.toPairDStream(),
            partitioner,
        )
        .toTupleDStream()

/**
 * Return a new DStream by applying 'left outer join' between RDDs of `this` DStream and
 * `other` DStream. Hash partitioning is used to generate the RDDs with `numPartitions`
 * partitions.
 */
fun <K, V, W> JavaDStream<Tuple2<K, V>>.leftOuterJoin(
    other: JavaDStream<Tuple2<K, W>>,
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
): JavaDStream<Tuple2<K, Tuple2<V, Optional<W>>>> =
    toPairDStream()
        .leftOuterJoin(
            other.toPairDStream(),
            numPartitions,
        )
        .toTupleDStream()

/**
 * Return a new DStream by applying 'left outer join' between RDDs of `this` DStream and
 * `other` DStream. The supplied org.apache.spark.Partitioner is used to control
 * the partitioning of each RDD.
 */
fun <K, V, W> JavaDStream<Tuple2<K, V>>.leftOuterJoin(
    other: JavaDStream<Tuple2<K, W>>,
    partitioner: Partitioner,
): JavaDStream<Tuple2<K, Tuple2<V, Optional<W>>>> =
    toPairDStream()
        .leftOuterJoin(
            other.toPairDStream(),
            partitioner,
        )
        .toTupleDStream()

/**
 * Return a new DStream by applying 'right outer join' between RDDs of `this` DStream and
 * `other` DStream. Hash partitioning is used to generate the RDDs with `numPartitions`
 * partitions.
 */
fun <K, V, W> JavaDStream<Tuple2<K, V>>.rightOuterJoin(
    other: JavaDStream<Tuple2<K, W>>,
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
): JavaDStream<Tuple2<K, Tuple2<Optional<V>, W>>> =
    toPairDStream()
        .rightOuterJoin(
            other.toPairDStream(),
            numPartitions,
        )
        .toTupleDStream()

/**
 * Return a new DStream by applying 'right outer join' between RDDs of `this` DStream and
 * `other` DStream. The supplied org.apache.spark.Partitioner is used to control
 * the partitioning of each RDD.
 */
fun <K, V, W> JavaDStream<Tuple2<K, V>>.rightOuterJoin(
    other: JavaDStream<Tuple2<K, W>>,
    partitioner: Partitioner,
): JavaDStream<Tuple2<K, Tuple2<Optional<V>, W>>> =
    toPairDStream()
        .rightOuterJoin(
            other.toPairDStream(),
            partitioner,
        )
        .toTupleDStream()

/**
 * Return a new DStream by applying 'full outer join' between RDDs of `this` DStream and
 * `other` DStream. Hash partitioning is used to generate the RDDs with `numPartitions`
 * partitions.
 */
fun <K, V, W> JavaDStream<Tuple2<K, V>>.fullOuterJoin(
    other: JavaDStream<Tuple2<K, W>>,
    numPartitions: Int = dstream().ssc().sc().defaultParallelism(),
): JavaDStream<Tuple2<K, Tuple2<Optional<V>, Optional<W>>>> =
    toPairDStream()
        .fullOuterJoin(
            other.toPairDStream(),
            numPartitions,
        )
        .toTupleDStream()

/**
 * Return a new DStream by applying 'full outer join' between RDDs of `this` DStream and
 * `other` DStream. The supplied org.apache.spark.Partitioner is used to control
 * the partitioning of each RDD.
 */
fun <K, V, W> JavaDStream<Tuple2<K, V>>.fullOuterJoin(
    other: JavaDStream<Tuple2<K, W>>,
    partitioner: Partitioner,
): JavaDStream<Tuple2<K, Tuple2<Optional<V>, Optional<W>>>> =
    toPairDStream()
        .fullOuterJoin(
            other.toPairDStream(),
            partitioner,
        )
        .toTupleDStream()

/**
 * Save each RDD in `this` DStream as a Hadoop file. The file name at each batch interval is
 * generated based on `prefix` and `suffix`: "prefix-TIME_IN_MS.suffix".
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.saveAsHadoopFiles(
    prefix: String,
    suffix: String,
): Unit = toPairDStream().saveAsHadoopFiles(prefix, suffix)

/**
 * Save each RDD in `this` DStream as a Hadoop file. The file name at each batch interval is
 * generated based on `prefix` and `suffix`: "prefix-TIME_IN_MS.suffix".
 */
fun <K, V> JavaDStream<Tuple2<K, V>>.saveAsNewAPIHadoopFiles(
    prefix: String,
    suffix: String,
): Unit = toPairDStream().saveAsNewAPIHadoopFiles(prefix, suffix)
