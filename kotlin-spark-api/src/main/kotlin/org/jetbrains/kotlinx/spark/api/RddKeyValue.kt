@file:Suppress("unused")

package org.jetbrains.kotlinx.spark.api

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.compress.CompressionCodec
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapred.OutputFormat
import org.apache.spark.Partitioner
import org.apache.spark.RangePartitioner
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.Optional
import org.apache.spark.partial.BoundedDouble
import org.apache.spark.partial.PartialResult
import org.apache.spark.serializer.Serializer
import org.jetbrains.kotlinx.spark.api.tuples.*
import scala.Tuple2
import scala.Tuple3
import scala.Tuple4
import kotlin.random.Random
import org.apache.hadoop.mapreduce.OutputFormat as NewOutputFormat

/** Utility method to convert [JavaRDD]<[Tuple2]> to [JavaPairRDD]. */
fun <K, V> JavaRDD<Tuple2<K, V>>.toJavaPairRDD(): JavaPairRDD<K, V> =
    JavaPairRDD.fromJavaRDD(this)

/** Utility method to convert [JavaPairRDD] to [JavaRDD]<[Tuple2]>. */
fun <K, V> JavaPairRDD<K, V>.toTupleRDD(): JavaRDD<Tuple2<K, V>> =
    JavaPairRDD.toRDD(this).toJavaRDD()

/**
 * Generic function to combine the elements for each key using a custom set of aggregation
 * functions. This method is here for backward compatibility. It does not provide combiner
 * classtag information to the shuffle.
 */
fun <K, V, C> JavaRDD<Tuple2<K, V>>.combineByKey(
    createCombiner: (V) -> C,
    mergeValue: (C, V) -> C,
    mergeCombiners: (C, C) -> C,
    partitioner: Partitioner,
    mapSideCombine: Boolean = true,
    serializer: Serializer? = null,
): JavaRDD<Tuple2<K, C>> = toJavaPairRDD()
    .combineByKey(createCombiner, mergeValue, mergeCombiners, partitioner, mapSideCombine, serializer)
    .toTupleRDD()

/**
 * Simplified version of combineByKeyWithClassTag that hash-partitions the output RDD.
 * This method is here for backward compatibility. It does not provide combiner
 * classtag information to the shuffle.
 */
fun <K, V, C> JavaRDD<Tuple2<K, V>>.combineByKey(
    createCombiner: (V) -> C,
    mergeValue: (C, V) -> C,
    mergeCombiners: (C, C) -> C,
    numPartitions: Int,
): JavaRDD<Tuple2<K, C>> = toJavaPairRDD()
    .combineByKey(createCombiner, mergeValue, mergeCombiners, numPartitions)
    .toTupleRDD()


/**
 * Aggregate the values of each key, using given combine functions and a neutral "zero value".
 * This function can return a different result type, [U], than the type of the values in this RDD,
 * [V]. Thus, we need one operation for merging a [V] into a [U] and one operation for merging two [U]'s,
 * as in scala.TraversableOnce. The former operation is used for merging values within a
 * partition, and the latter is used for merging values between partitions. To avoid memory
 * allocation, both of these functions are allowed to modify and return their first argument
 * instead of creating a new [U].
 */
fun <K, V, U> JavaRDD<Tuple2<K, V>>.aggregateByKey(
    zeroValue: U,
    partitioner: Partitioner,
    seqFunc: (U, V) -> U,
    combFunc: (U, U) -> U,
): JavaRDD<Tuple2<K, U>> = toJavaPairRDD()
    .aggregateByKey(zeroValue, partitioner, seqFunc, combFunc)
    .toTupleRDD()

/**
 * Aggregate the values of each key, using given combine functions and a neutral "zero value".
 * This function can return a different result type, [U], than the type of the values in this RDD,
 * [V]. Thus, we need one operation for merging a [V] into a [U] and one operation for merging two [U]'s,
 * as in scala.TraversableOnce. The former operation is used for merging values within a
 * partition, and the latter is used for merging values between partitions. To avoid memory
 * allocation, both of these functions are allowed to modify and return their first argument
 * instead of creating a new [U].
 */
fun <K, V, U> JavaRDD<Tuple2<K, V>>.aggregateByKey(
    zeroValue: U,
    numPartitions: Int,
    seqFunc: (U, V) -> U,
    combFunc: (U, U) -> U,
): JavaRDD<Tuple2<K, U>> = toJavaPairRDD()
    .aggregateByKey(zeroValue, numPartitions, seqFunc, combFunc)
    .toTupleRDD()

/**
 * Aggregate the values of each key, using given combine functions and a neutral "zero value".
 * This function can return a different result type, [U], than the type of the values in this RDD,
 * [V]. Thus, we need one operation for merging a [V] into a [U] and one operation for merging two [U]'s,
 * as in scala.TraversableOnce. The former operation is used for merging values within a
 * partition, and the latter is used for merging values between partitions. To avoid memory
 * allocation, both of these functions are allowed to modify and return their first argument
 * instead of creating a new [U].
 */
fun <K, V, U> JavaRDD<Tuple2<K, V>>.aggregateByKey(
    zeroValue: U,
    seqFunc: (U, V) -> U,
    combFunc: (U, U) -> U,
): JavaRDD<Tuple2<K, U>> = toJavaPairRDD()
    .aggregateByKey(zeroValue, seqFunc, combFunc)
    .toTupleRDD()

/**
 * Merge the values for each key using an associative function and a neutral "zero value" which
 * may be added to the result an arbitrary number of times, and must not change the result
 * (e.g., [emptyList] for list concatenation, 0 for addition, or 1 for multiplication.).
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.foldByKey(
    zeroValue: V,
    partitioner: Partitioner,
    func: (V, V) -> V,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .foldByKey(zeroValue, partitioner, func)
    .toTupleRDD()

/**
 * Merge the values for each key using an associative function and a neutral "zero value" which
 * may be added to the result an arbitrary number of times, and must not change the result
 * (e.g., [emptyList] for list concatenation, 0 for addition, or 1 for multiplication.).
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.foldByKey(
    zeroValue: V,
    numPartitions: Int,
    func: (V, V) -> V,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .foldByKey(zeroValue, numPartitions, func)
    .toTupleRDD()

/**
 * Merge the values for each key using an associative function and a neutral "zero value" which
 * may be added to the result an arbitrary number of times, and must not change the result
 * (e.g., [emptyList] for list concatenation, 0 for addition, or 1 for multiplication.).
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.foldByKey(
    zeroValue: V,
    func: (V, V) -> V,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .foldByKey(zeroValue, func)
    .toTupleRDD()

/**
 * Return a subset of this RDD sampled by key (via stratified sampling).
 *
 * Create a sample of this RDD using variable sampling rates for different keys as specified by
 * [fractions], a key to sampling rate map, via simple random sampling with one pass over the
 * RDD, to produce a sample of size that's approximately equal to the sum of
 * math.ceil(numItems * samplingRate) over all key values.
 *
 * @param withReplacement whether to sample with or without replacement
 * @param fractions map of specific keys to sampling rates
 * @param seed seed for the random number generator
 * @return RDD containing the sampled subset
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.sampleByKey(
    withReplacement: Boolean,
    fractions: Map<K, Double>,
    seed: Long = Random.nextLong(),
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .sampleByKey(withReplacement, fractions, seed)
    .toTupleRDD()

/**
 * Return a subset of this RDD sampled by key (via stratified sampling) containing exactly
 * math.ceil(numItems * samplingRate) for each stratum (group of pairs with the same key).
 *
 * This method differs from [sampleByKey] in that we make additional passes over the RDD to
 * create a sample size that's exactly equal to the sum of math.ceil(numItems * samplingRate)
 * over all key values with a 99.99% confidence. When sampling without replacement, we need one
 * additional pass over the RDD to guarantee sample size; when sampling with replacement, we need
 * two additional passes.
 *
 * @param withReplacement whether to sample with or without replacement
 * @param fractions map of specific keys to sampling rates
 * @param seed seed for the random number generator
 * @return RDD containing the sampled subset
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.sampleByKeyExact(
    withReplacement: Boolean,
    fractions: Map<K, Double>,
    seed: Long = Random.nextLong(),
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .sampleByKeyExact(withReplacement, fractions, seed)
    .toTupleRDD()

/**
 * Merge the values for each key using an associative and commutative reduce function. This will
 * also perform the merging locally on each mapper before sending results to a reducer, similarly
 * to a "combiner" in MapReduce.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.reduceByKey(
    partitioner: Partitioner,
    func: (V, V) -> V,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .reduceByKey(partitioner, func)
    .toTupleRDD()

/**
 * Merge the values for each key using an associative and commutative reduce function. This will
 * also perform the merging locally on each mapper before sending results to a reducer, similarly
 * to a "combiner" in MapReduce. Output will be hash-partitioned with numPartitions partitions.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.reduceByKey(
    numPartitions: Int,
    func: (V, V) -> V,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .reduceByKey(func, numPartitions)
    .toTupleRDD()

/**
 * Merge the values for each key using an associative and commutative reduce function. This will
 * also perform the merging locally on each mapper before sending results to a reducer, similarly
 * to a "combiner" in MapReduce. Output will be hash-partitioned with the existing partitioner/
 * parallelism level.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.reduceByKey(
    func: (V, V) -> V,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .reduceByKey(func)
    .toTupleRDD()

/**
 * Merge the values for each key using an associative and commutative reduce function, but return
 * the results immediately to the master as a Map. This will also perform the merging locally on
 * each mapper before sending results to a reducer, similarly to a "combiner" in MapReduce.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.reduceByKeyLocally(
    func: (V, V) -> V,
): Map<K, V> = toJavaPairRDD()
    .reduceByKeyLocally(func)

/**
 * Count the number of elements for each key, collecting the results to a local Map.
 *
 * This method should only be used if the resulting map is expected to be small, as
 * the whole thing is loaded into the driver's memory.
 * To handle very large results, consider using `rdd.mapValues { 1L }.reduceByKey(Long::plus)`, which
 * returns an [RDD<T, Long>] instead of a map.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.countByKey(): Map<K, Long> =
    toJavaPairRDD()
        .countByKey()

/**
 * Approximate version of countByKey that can return a partial result if it does
 * not finish within a timeout.
 *
 * The confidence is the probability that the error bounds of the result will
 * contain the true value. That is, if countApprox were called repeatedly
 * with confidence 0.9, we would expect 90% of the results to contain the
 * true count. The confidence must be in the range <0,1> or an exception will
 * be thrown.
 *
 * @param timeout maximum time to wait for the job, in milliseconds
 * @param confidence the desired statistical confidence in the result
 * @return a potentially incomplete result, with error bounds
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.countByKeyApprox(
    timeout: Long,
    confidence: Double = 0.95,
): PartialResult<Map<K, BoundedDouble>> = toJavaPairRDD()
    .countByKeyApprox(timeout, confidence)

/**
 * Group the values for each key in the RDD into a single sequence. Allows controlling the
 * partitioning of the resulting key-value pair RDD by passing a Partitioner.
 * The ordering of elements within each group is not guaranteed, and may even differ
 * each time the resulting RDD is evaluated.
 *
 * Note: This operation may be very expensive. If you are grouping in order to perform an
 * aggregation (such as a sum or average) over each key, using [aggregateByKey]
 * or [reduceByKey] will provide much better performance.
 *
 * Note: As currently implemented, groupByKey must be able to hold all the key-value pairs for any
 * key in memory. If a key has too many values, it can result in an [OutOfMemoryError].
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.groupByKey(
    partitioner: Partitioner,
): JavaRDD<Tuple2<K, Iterable<V>>> = toJavaPairRDD()
    .groupByKey(partitioner)
    .toTupleRDD()

/**
 * Group the values for each key in the RDD into a single sequence. Hash-partitions the
 * resulting RDD with into [numPartitions] partitions. The ordering of elements within
 * each group is not guaranteed, and may even differ each time the resulting RDD is evaluated.
 *
 * Note: This operation may be very expensive. If you are grouping in order to perform an
 * aggregation (such as a sum or average) over each key, using [aggregateByKey]
 * or [reduceByKey] will provide much better performance.
 *
 * Note: As currently implemented, groupByKey must be able to hold all the key-value pairs for any
 * key in memory. If a key has too many values, it can result in an [OutOfMemoryError].
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.groupByKey(
    numPartitions: Int,
): JavaRDD<Tuple2<K, Iterable<V>>> = toJavaPairRDD()
    .groupByKey(numPartitions)
    .toTupleRDD()

/**
 * Return a copy of the RDD partitioned using the specified partitioner.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.partitionBy(
    partitioner: Partitioner,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .partitionBy(partitioner)
    .toTupleRDD()

/**
 * Return an RDD containing all pairs of elements with matching keys in [this] and [other]. Each
 * pair of elements will be returned as a (k, (v1, v2)) tuple, where (k, v1) is in [this] and
 * (k, v2) is in [other]. Uses the given Partitioner to partition the output RDD.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.join(
    other: JavaRDD<Tuple2<K, W>>,
    partitioner: Partitioner,
): JavaRDD<Tuple2<K, Tuple2<V, W>>> = toJavaPairRDD()
    .join(other.toJavaPairRDD(), partitioner)
    .toTupleRDD()

/**
 * Perform a left outer join of [this] and [other]. For each element (k, v) in [this], the
 * resulting RDD will either contain all pairs (k, (v, Some(w))) for w in [other], or the
 * pair (k, (v, None)) if no elements in [other] have key k. Uses the given Partitioner to
 * partition the output RDD.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.leftOuterJoin(
    other: JavaRDD<Tuple2<K, W>>,
    partitioner: Partitioner,
): JavaRDD<Tuple2<K, Tuple2<V, Optional<W>>>> = toJavaPairRDD()
    .leftOuterJoin(other.toJavaPairRDD(), partitioner)
    .toTupleRDD()

/**
 * Perform a right outer join of [this] and [other]. For each element (k, w) in [other], the
 * resulting RDD will either contain all pairs (k, (Some(v), w)) for v in [this], or the
 * pair (k, (None, w)) if no elements in [this] have key k. Uses the given Partitioner to
 * partition the output RDD.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.rightOuterJoin(
    other: JavaRDD<Tuple2<K, W>>,
    partitioner: Partitioner,
): JavaRDD<Tuple2<K, Tuple2<Optional<V>, W>>> = toJavaPairRDD()
    .rightOuterJoin(other.toJavaPairRDD(), partitioner)
    .toTupleRDD()

/**
 * Perform a full outer join of [this] and [other]. For each element (k, v) in [this], the
 * resulting RDD will either contain all pairs (k, (Some(v), Some(w))) for w in [other], or
 * the pair (k, (Some(v), None)) if no elements in [other] have key k. Similarly, for each
 * element (k, w) in [other], the resulting RDD will either contain all pairs
 * (k, (Some(v), Some(w))) for v in [this], or the pair (k, (None, Some(w))) if no elements
 * in [this] have key k. Uses the given Partitioner to partition the output RDD.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.fullOuterJoin(
    other: JavaRDD<Tuple2<K, W>>,
    partitioner: Partitioner,
): JavaRDD<Tuple2<K, Tuple2<Optional<V>, Optional<W>>>> = toJavaPairRDD()
    .fullOuterJoin(other.toJavaPairRDD(), partitioner)
    .toTupleRDD()

/**
 * Simplified version of combineByKeyWithClassTag that hash-partitions the resulting RDD using the
 * existing partitioner/parallelism level. This method is here for backward compatibility. It
 * does not provide combiner classtag information to the shuffle.
 */
fun <K, V, C> JavaRDD<Tuple2<K, V>>.combineByKey(
    createCombiner: (V) -> C,
    mergeValue: (C, V) -> C,
    mergeCombiners: (C, C) -> C,
): JavaRDD<Tuple2<K, C>> = toJavaPairRDD()
    .combineByKey(createCombiner, mergeValue, mergeCombiners)
    .toTupleRDD()


/**
 * Group the values for each key in the RDD into a single sequence. Hash-partitions the
 * resulting RDD with the existing partitioner/parallelism level. The ordering of elements
 * within each group is not guaranteed, and may even differ each time the resulting RDD is
 * evaluated.
 *
 * Note: This operation may be very expensive. If you are grouping in order to perform an
 * aggregation (such as a sum or average) over each key, using [aggregateByKey]
 * or [reduceByKey] will provide much better performance.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.groupByKey(): JavaRDD<Tuple2<K, Iterable<V>>> =
    toJavaPairRDD()
        .groupByKey()
        .toTupleRDD()

/**
 * Return an RDD containing all pairs of elements with matching keys in [this] and [other]. Each
 * pair of elements will be returned as a (k, (v1, v2)) tuple, where (k, v1) is in [this] and
 * (k, v2) is in [other]. Performs a hash join across the cluster.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.join(other: JavaRDD<Tuple2<K, W>>): JavaRDD<Tuple2<K, Tuple2<V, W>>> =
    toJavaPairRDD()
        .join(other.toJavaPairRDD())
        .toTupleRDD()

/**
 * Return an RDD containing all pairs of elements with matching keys in [this] and [other]. Each
 * pair of elements will be returned as a (k, (v1, v2)) tuple, where (k, v1) is in [this] and
 * (k, v2) is in [other]. Performs a hash join across the cluster.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.join(
    other: JavaRDD<Tuple2<K, W>>,
    numPartitions: Int,
): JavaRDD<Tuple2<K, Tuple2<V, W>>> =
    toJavaPairRDD()
        .join(other.toJavaPairRDD(), numPartitions)
        .toTupleRDD()

/**
 * Perform a left outer join of [this] and [other]. For each element (k, v) in [this], the
 * resulting RDD will either contain all pairs (k, (v, Some(w))) for w in [other], or the
 * pair (k, (v, None)) if no elements in [other] have key k. Hash-partitions the output
 * using the existing partitioner/parallelism level.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.leftOuterJoin(
    other: JavaRDD<Tuple2<K, W>>,
): JavaRDD<Tuple2<K, Tuple2<V, Optional<W>>>> =
    toJavaPairRDD()
        .leftOuterJoin(other.toJavaPairRDD())
        .toTupleRDD()

/**
 * Perform a left outer join of [this] and [other]. For each element (k, v) in [this], the
 * resulting RDD will either contain all pairs (k, (v, Some(w))) for w in [other], or the
 * pair (k, (v, None)) if no elements in [other] have key k. Hash-partitions the output
 * into [numPartitions] partitions.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.leftOuterJoin(
    other: JavaRDD<Tuple2<K, W>>,
    numPartitions: Int,
): JavaRDD<Tuple2<K, Tuple2<V, Optional<W>>>> = toJavaPairRDD()
    .leftOuterJoin(other.toJavaPairRDD(), numPartitions)
    .toTupleRDD()

/**
 * Perform a right outer join of [this] and [other]. For each element (k, w) in [other], the
 * resulting RDD will either contain all pairs (k, (Some(v), w)) for v in [this], or the
 * pair (k, (None, w)) if no elements in [this] have key k. Hash-partitions the resulting
 * RDD using the existing partitioner/parallelism level.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.rightOuterJoin(
    other: JavaRDD<Tuple2<K, W>>,
): JavaRDD<Tuple2<K, Tuple2<Optional<V>, W>>> =
    toJavaPairRDD()
        .rightOuterJoin(other.toJavaPairRDD())
        .toTupleRDD()

/**
 * Perform a right outer join of [this] and [other]. For each element (k, w) in [other], the
 * resulting RDD will either contain all pairs (k, (Some(v), w)) for v in [this], or the
 * pair (k, (None, w)) if no elements in [this] have key k. Hash-partitions the resulting
 * RDD into the given number of partitions.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.rightOuterJoin(
    other: JavaRDD<Tuple2<K, W>>,
    numPartitions: Int,
): JavaRDD<Tuple2<K, Tuple2<Optional<V>, W>>> = toJavaPairRDD()
    .rightOuterJoin(other.toJavaPairRDD(), numPartitions)
    .toTupleRDD()

/**
 * Perform a full outer join of [this] and [other]. For each element (k, v) in [this], the
 * resulting RDD will either contain all pairs (k, (Some(v), Some(w))) for w in [other], or
 * the pair (k, (Some(v), None)) if no elements in [other] have key k. Similarly, for each
 * element (k, w) in [other], the resulting RDD will either contain all pairs
 * (k, (Some(v), Some(w))) for v in [this], or the pair (k, (None, Some(w))) if no elements
 * in [this] have key k. Hash-partitions the resulting RDD using the existing partitioner/
 * parallelism level.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.fullOuterJoin(
    other: JavaRDD<Tuple2<K, W>>,
): JavaRDD<Tuple2<K, Tuple2<Optional<V>, Optional<W>>>> =
    toJavaPairRDD()
        .fullOuterJoin(other.toJavaPairRDD())
        .toTupleRDD()

/**
 * Perform a full outer join of [this] and [other]. For each element (k, v) in [this], the
 * resulting RDD will either contain all pairs (k, (Some(v), Some(w))) for w in [other], or
 * the pair (k, (Some(v), None)) if no elements in [other] have key k. Similarly, for each
 * element (k, w) in [other], the resulting RDD will either contain all pairs
 * (k, (Some(v), Some(w))) for v in [this], or the pair (k, (None, Some(w))) if no elements
 * in [this] have key k. Hash-partitions the resulting RDD into the given number of partitions.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.fullOuterJoin(
    other: JavaRDD<Tuple2<K, W>>,
    numPartitions: Int,
): JavaRDD<Tuple2<K, Tuple2<Optional<V>, Optional<W>>>> =
    toJavaPairRDD()
        .fullOuterJoin(other.toJavaPairRDD(), numPartitions)
        .toTupleRDD()

/**
 * Return the key-value pairs in this RDD to the master as a Map.
 *
 * Warning: this doesn't return a multimap (so if you have multiple values to the same key, only
 *          one value per key is preserved in the map returned)
 *
 * Note: this method should only be used if the resulting data is expected to be small, as
 * all the data is loaded into the driver's memory.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.collectAsMap(): Map<K, V> = toJavaPairRDD().collectAsMap()

/**
 * Pass each key in the key-value pair RDD through a map function without changing the values;
 * this also retains the original RDD's partitioning.
 */
fun <K, V, U> JavaRDD<Tuple2<K, V>>.mapKeys(f: (K) -> U): JavaRDD<Tuple2<U, V>> =
    mapPartitions({
        it.map { (_1, _2) ->
            tupleOf(f(_1), _2)
        }
    }, true)

/**
 * Pass each value in the key-value pair RDD through a map function without changing the keys;
 * this also retains the original RDD's partitioning.
 */
fun <K, V, U> JavaRDD<Tuple2<K, V>>.mapValues(f: (V) -> U): JavaRDD<Tuple2<K, U>> =
    toJavaPairRDD().mapValues(f).toTupleRDD()

/**
 * Pass each value in the key-value pair RDD through a flatMap function without changing the
 * keys; this also retains the original RDD's partitioning.
 */
fun <K, V, U> JavaRDD<Tuple2<K, V>>.flatMapValues(f: (V) -> Iterator<U>): JavaRDD<Tuple2<K, U>> =
    toJavaPairRDD().flatMapValues(f).toTupleRDD()

/**
 * For each key k in [this] or [other1] or [other2] or [other3],
 * return a resulting RDD that contains a tuple with the list of values
 * for that key in [this], [other1], [other2] and [other3].
 */
fun <K, V, W1, W2, W3> JavaRDD<Tuple2<K, V>>.cogroup(
    other1: JavaRDD<Tuple2<K, W1>>,
    other2: JavaRDD<Tuple2<K, W2>>,
    other3: JavaRDD<Tuple2<K, W3>>,
    partitioner: Partitioner,
): JavaRDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> =
    toJavaPairRDD().cogroup(other1.toJavaPairRDD(), other2.toJavaPairRDD(), other3.toJavaPairRDD(), partitioner)
        .toTupleRDD()

/**
 * For each key k in [this] or [other], return a resulting RDD that contains a tuple with the
 * list of values for that key in [this] as well as [other].
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.cogroup(
    other: JavaRDD<Tuple2<K, W>>,
    partitioner: Partitioner,
): JavaRDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> = toJavaPairRDD()
    .cogroup(other.toJavaPairRDD(), partitioner)
    .toTupleRDD()

/**
 * For each key k in [this] or [other1] or [other2], return a resulting RDD that contains a
 * tuple with the list of values for that key in [this], [other1] and [other2].
 */
fun <K, V, W1, W2> JavaRDD<Tuple2<K, V>>.cogroup(
    other1: JavaRDD<Tuple2<K, W1>>,
    other2: JavaRDD<Tuple2<K, W2>>,
    partitioner: Partitioner,
): JavaRDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> =
    toJavaPairRDD()
        .cogroup(other1.toJavaPairRDD(), other2.toJavaPairRDD(), partitioner)
        .toTupleRDD()

/**
 * For each key k in [this] or [other1] or [other2] or [other3],
 * return a resulting RDD that contains a tuple with the list of values
 * for that key in [this], [other1], [other2] and [other3].
 */
fun <K, V, W1, W2, W3> JavaRDD<Tuple2<K, V>>.cogroup(
    other1: JavaRDD<Tuple2<K, W1>>,
    other2: JavaRDD<Tuple2<K, W2>>,
    other3: JavaRDD<Tuple2<K, W3>>,
): JavaRDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> =
    toJavaPairRDD()
        .cogroup(other1.toJavaPairRDD(), other2.toJavaPairRDD(), other3.toJavaPairRDD())
        .toTupleRDD()

/**
 * For each key k in [this] or [other], return a resulting RDD that contains a tuple with the
 * list of values for that key in [this] as well as [other].
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.cogroup(
    other: JavaRDD<Tuple2<K, W>>,
): JavaRDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> =
    toJavaPairRDD().cogroup(other.toJavaPairRDD()).toTupleRDD()

/**
 * For each key k in [this] or [other1] or [other2], return a resulting RDD that contains a
 * tuple with the list of values for that key in [this], [other1] and [other2].
 */
fun <K, V, W1, W2> JavaRDD<Tuple2<K, V>>.cogroup(
    other1: JavaRDD<Tuple2<K, W1>>,
    other2: JavaRDD<Tuple2<K, W2>>,
): JavaRDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> =
    toJavaPairRDD()
        .cogroup(other1.toJavaPairRDD(), other2.toJavaPairRDD())
        .toTupleRDD()

/**
 * For each key k in [this] or [other], return a resulting RDD that contains a tuple with the
 * list of values for that key in [this] as well as [other].
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.cogroup(
    other: JavaRDD<Tuple2<K, W>>,
    numPartitions: Int,
): JavaRDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> =
    toJavaPairRDD().cogroup(other.toJavaPairRDD(), numPartitions).toTupleRDD()

/**
 * For each key k in [this] or [other1] or [other2], return a resulting RDD that contains a
 * tuple with the list of values for that key in [this], [other1] and [other2].
 */
fun <K, V, W1, W2> JavaRDD<Tuple2<K, V>>.cogroup(
    other1: JavaRDD<Tuple2<K, W1>>,
    other2: JavaRDD<Tuple2<K, W2>>,
    numPartitions: Int,
): JavaRDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> =
    toJavaPairRDD()
        .cogroup(other1.toJavaPairRDD(), other2.toJavaPairRDD(), numPartitions)
        .toTupleRDD()

/**
 * For each key k in [this] or [other1] or [other2] or [other3],
 * return a resulting RDD that contains a tuple with the list of values
 * for that key in [this], [other1], [other2] and [other3].
 */
fun <K, V, W1, W2, W3> JavaRDD<Tuple2<K, V>>.cogroup(
    other1: JavaRDD<Tuple2<K, W1>>,
    other2: JavaRDD<Tuple2<K, W2>>,
    other3: JavaRDD<Tuple2<K, W3>>,
    numPartitions: Int,
): JavaRDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> =
    toJavaPairRDD()
        .cogroup(other1.toJavaPairRDD(), other2.toJavaPairRDD(), other3.toJavaPairRDD(), numPartitions)
        .toTupleRDD()


/** Alias for [cogroup]. */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.groupWith(
    other: JavaRDD<Tuple2<K, W>>,
): JavaRDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> =
    toJavaPairRDD().groupWith(other.toJavaPairRDD()).toTupleRDD()

/** Alias for [cogroup]. */
fun <K, V, W1, W2> JavaRDD<Tuple2<K, V>>.groupWith(
    other1: JavaRDD<Tuple2<K, W1>>,
    other2: JavaRDD<Tuple2<K, W2>>,
): JavaRDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> =
    toJavaPairRDD().groupWith(other1.toJavaPairRDD(), other2.toJavaPairRDD()).toTupleRDD()

/** Alias for [cogroup]. */
fun <K, V, W1, W2, W3> JavaRDD<Tuple2<K, V>>.groupWith(
    other1: JavaRDD<Tuple2<K, W1>>,
    other2: JavaRDD<Tuple2<K, W2>>,
    other3: JavaRDD<Tuple2<K, W3>>,
): JavaRDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> =
    toJavaPairRDD().groupWith(other1.toJavaPairRDD(), other2.toJavaPairRDD(), other3.toJavaPairRDD()).toTupleRDD()

/**
 * Return an RDD with the pairs from [this] whose keys are not in [other].
 *
 * Uses [this] partitioner/partition size, because even if [other] is huge, the resulting
 * RDD will be less than or equal to us.
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.subtractByKey(other: JavaRDD<Tuple2<K, W>>): JavaRDD<Tuple2<K, V>> =
    toJavaPairRDD().subtractByKey(other.toJavaPairRDD()).toTupleRDD()

/**
 * Return an RDD with the pairs from [this] whose keys are not in [other].
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.subtractByKey(
    other: JavaRDD<Tuple2<K, W>>,
    numPartitions: Int,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .subtractByKey(other.toJavaPairRDD(), numPartitions)
    .toTupleRDD()

/**
 * Return an RDD with the pairs from [this] whose keys are not in [other].
 */
fun <K, V, W> JavaRDD<Tuple2<K, V>>.subtractByKey(
    other: JavaRDD<Tuple2<K, W>>,
    p: Partitioner,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .subtractByKey(other.toJavaPairRDD(), p)
    .toTupleRDD()

/**
 * Return the list of values in the RDD for key [key]. This operation is done efficiently if the
 * RDD has a known partitioner by only searching the partition that the key maps to.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.lookup(key: K): List<V> = toJavaPairRDD().lookup(key)

/** Output the RDD to any Hadoop-supported file system. */
fun <K, V, F : OutputFormat<*, *>> JavaRDD<Tuple2<K, V>>.saveAsHadoopFile(
    path: String,
    keyClass: Class<*>,
    valueClass: Class<*>,
    outputFormatClass: Class<F>,
    conf: JobConf,
): Unit = toJavaPairRDD().saveAsHadoopFile(path, keyClass, valueClass, outputFormatClass, conf)

/** Output the RDD to any Hadoop-supported file system. */
fun <K, V, F : OutputFormat<*, *>> JavaRDD<Tuple2<K, V>>.saveAsHadoopFile(
    path: String,
    keyClass: Class<*>,
    valueClass: Class<*>,
    outputFormatClass: Class<F>,
): Unit = toJavaPairRDD().saveAsHadoopFile(path, keyClass, valueClass, outputFormatClass)

/** Output the RDD to any Hadoop-supported file system, compressing with the supplied codec. */
fun <K, V, F : OutputFormat<*, *>> JavaRDD<Tuple2<K, V>>.saveAsHadoopFile(
    path: String,
    keyClass: Class<*>,
    valueClass: Class<*>,
    outputFormatClass: Class<F>,
    codec: Class<CompressionCodec>,
): Unit = toJavaPairRDD().saveAsHadoopFile(path, keyClass, valueClass, outputFormatClass, codec)

/** Output the RDD to any Hadoop-supported file system. */
fun <K, V, F : NewOutputFormat<*, *>> JavaRDD<Tuple2<K, V>>.saveAsNewAPIHadoopFile(
    path: String,
    keyClass: Class<*>,
    valueClass: Class<*>,
    outputFormatClass: Class<F>,
    conf: Configuration,
): Unit = toJavaPairRDD().saveAsNewAPIHadoopFile(path, keyClass, valueClass, outputFormatClass, conf)

/**
 * Output the RDD to any Hadoop-supported storage system, using
 * a Configuration object for that storage system.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.saveAsNewAPIHadoopDataset(conf: Configuration): Unit =
    toJavaPairRDD().saveAsNewAPIHadoopDataset(conf)

/** Output the RDD to any Hadoop-supported file system. */
fun <K, V, F : NewOutputFormat<*, *>> JavaRDD<Tuple2<K, V>>.saveAsNewAPIHadoopFile(
    path: String,
    keyClass: Class<*>,
    valueClass: Class<*>,
    outputFormatClass: Class<F>,
): Unit = toJavaPairRDD().saveAsNewAPIHadoopFile(path, keyClass, valueClass, outputFormatClass)

/**
 * Output the RDD to any Hadoop-supported storage system, using a Hadoop JobConf object for
 * that storage system. The JobConf should set an OutputFormat and any output paths required
 * (e.g. a table name to write to) in the same way as it would be configured for a Hadoop
 * MapReduce job.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.saveAsHadoopDataset(conf: JobConf): Unit =
    toJavaPairRDD().saveAsHadoopDataset(conf)

/**
 * Repartition the RDD according to the given partitioner and, within each resulting partition,
 * sort records by their keys.
 *
 * This is more efficient than calling [JavaRDD.repartition] and then sorting within each partition
 * because it can push the sorting down into the shuffle machinery.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.repartitionAndSortWithinPartitions(partitioner: Partitioner): JavaRDD<Tuple2<K, V>> =
    toJavaPairRDD().repartitionAndSortWithinPartitions(partitioner).toTupleRDD()

/**
 * Repartition the RDD according to the given partitioner and, within each resulting partition,
 * sort records by their keys.
 *
 * This is more efficient than calling [JavaRDD.repartition] and then sorting within each partition
 * because it can push the sorting down into the shuffle machinery.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.repartitionAndSortWithinPartitions(
    partitioner: Partitioner,
    comp: Comparator<K>,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD().repartitionAndSortWithinPartitions(partitioner, comp).toTupleRDD()

/**
 * Sort the RDD by key, so that each partition contains a sorted range of the elements. Calling
 * [JavaRDD.collect] or `save` on the resulting RDD will return or output an ordered list of records
 * (in the `save` case, they will be written to multiple `part-X` files in the filesystem, in
 * order of the keys).
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.sortByKey(ascending: Boolean = true): JavaRDD<Tuple2<K, V>> =
    toJavaPairRDD().sortByKey(ascending).toTupleRDD()

/**
 * Sort the RDD by key, so that each partition contains a sorted range of the elements. Calling
 * [JavaRDD.collect] or `save` on the resulting RDD will return or output an ordered list of records
 * (in the `save` case, they will be written to multiple `part-X` files in the filesystem, in
 * order of the keys).
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.sortByKey(ascending: Boolean, numPartitions: Int): JavaRDD<Tuple2<K, V>> =
    toJavaPairRDD().sortByKey(ascending, numPartitions).toTupleRDD()

/**
 * Sort the RDD by key, so that each partition contains a sorted range of the elements. Calling
 * [JavaRDD.collect] or `save` on the resulting RDD will return or output an ordered list of records
 * (in the `save` case, they will be written to multiple `part-X` files in the filesystem, in
 * order of the keys).
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.sortByKey(comp: Comparator<K>, ascending: Boolean = true): JavaRDD<Tuple2<K, V>> =
    toJavaPairRDD().sortByKey(comp, ascending).toTupleRDD()

/**
 * Sort the RDD by key, so that each partition contains a sorted range of the elements. Calling
 * [JavaRDD.collect] or `save` on the resulting RDD will return or output an ordered list of records
 * (in the `save` case, they will be written to multiple `part-X` files in the filesystem, in
 * order of the keys).
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.sortByKey(
    comp: Comparator<K>,
    ascending: Boolean,
    numPartitions: Int,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD().sortByKey(comp, ascending, numPartitions).toTupleRDD()

//#if sparkMinor >= 3.1
/**
 * Return a RDD containing only the elements in the inclusive range [lower] to [upper].
 * If the RDD has been partitioned using a [RangePartitioner], then this operation can be
 * performed efficiently by only scanning the partitions that might contain matching elements.
 * Otherwise, a standard [filter] is applied to all partitions.
 *
 * @since 3.1.0
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.filterByRange(lower: K, upper: K): JavaRDD<Tuple2<K, V>> =
    toJavaPairRDD().filterByRange(lower, upper).toTupleRDD()

/**
 * Return a RDD containing only the elements in the range [range].
 * If the RDD has been partitioned using a [RangePartitioner], then this operation can be
 * performed efficiently by only scanning the partitions that might contain matching elements.
 * Otherwise, a standard [filter] is applied to all partitions.
 *
 * @since 3.1.0
 */
fun <K : Comparable<K>, V> JavaRDD<Tuple2<K, V>>.filterByRange(range: ClosedRange<K>): JavaRDD<Tuple2<K, V>> =
    filterByRange(range.start, range.endInclusive)

/**
 * Return a RDD containing only the elements in the inclusive range [lower] to [upper].
 * If the RDD has been partitioned using a [RangePartitioner], then this operation can be
 * performed efficiently by only scanning the partitions that might contain matching elements.
 * Otherwise, a standard [filter] is applied to all partitions.
 *
 * @since 3.1.0
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.filterByRange(
    comp: Comparator<K>,
    lower: K,
    upper: K,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .filterByRange(comp, lower, upper)
    .toTupleRDD()

/**
 * Return a RDD containing only the elements in the inclusive range [range].
 * If the RDD has been partitioned using a [RangePartitioner], then this operation can be
 * performed efficiently by only scanning the partitions that might contain matching elements.
 * Otherwise, a standard [filter] is applied to all partitions.
 *
 * @since 3.1.0
 */
fun <K : Comparable<K>, V> JavaRDD<Tuple2<K, V>>.filterByRange(
    comp: Comparator<K>,
    range: ClosedRange<K>,
): JavaRDD<Tuple2<K, V>> = toJavaPairRDD()
    .filterByRange(comp, range.start, range.endInclusive)
    .toTupleRDD()
//#endif

/**
 * Return an RDD with the keys of each tuple.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.keys(): JavaRDD<K> = toJavaPairRDD().keys()

/**
 * Return an RDD with the values of each tuple.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.values(): JavaRDD<V> = toJavaPairRDD().values()

/**
 * Return approximate number of distinct values for each key in this RDD.
 *
 * The algorithm used is based on streamlib's implementation of "HyperLogLog in Practice:
 * Algorithmic Engineering of a State of The Art Cardinality Estimation Algorithm", available
 * <a href="https://doi.org/10.1145/2452376.2452456">here</a>.
 *
 * @param relativeSD Relative accuracy. Smaller values create counters that require more space.
 *                   It must be greater than 0.000017.
 * @param partitioner partitioner of the resulting RDD.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.countApproxDistinctByKey(
    relativeSD: Double,
    partitioner: Partitioner,
): JavaRDD<Tuple2<K, Long>> = toJavaPairRDD().countApproxDistinctByKey(relativeSD, partitioner).toTupleRDD()

/**
 * Return approximate number of distinct values for each key in this RDD.
 *
 * The algorithm used is based on streamlib's implementation of "HyperLogLog in Practice:
 * Algorithmic Engineering of a State of The Art Cardinality Estimation Algorithm", available
 * [here](https://doi.org/10.1145/2452376.2452456).
 *
 * @param relativeSD Relative accuracy. Smaller values create counters that require more space.
 *                   It must be greater than 0.000017.
 * @param numPartitions number of partitions of the resulting RDD.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.countApproxDistinctByKey(
    relativeSD: Double,
    numPartitions: Int,
): JavaRDD<Tuple2<K, Long>> = toJavaPairRDD().countApproxDistinctByKey(relativeSD, numPartitions).toTupleRDD()

/**
 * Return approximate number of distinct values for each key in this RDD.
 *
 * The algorithm used is based on streamlib's implementation of "HyperLogLog in Practice:
 * Algorithmic Engineering of a State of The Art Cardinality Estimation Algorithm", available
 * [here](https://doi.org/10.1145/2452376.2452456).
 *
 * @param relativeSD Relative accuracy. Smaller values create counters that require more space.
 *                   It must be greater than 0.000017.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.countApproxDistinctByKey(relativeSD: Double): JavaRDD<Tuple2<K, Long>> =
    toJavaPairRDD().countApproxDistinctByKey(relativeSD).toTupleRDD()

