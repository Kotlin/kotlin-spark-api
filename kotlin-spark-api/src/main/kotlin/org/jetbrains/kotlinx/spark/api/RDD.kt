package org.jetbrains.kotlinx.spark.api

import org.apache.spark.Partitioner
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.rdd.PairRDDFunctions
import org.apache.spark.serializer.Serializer
import org.jetbrains.kotlinx.spark.api.tuples.*
import org.jetbrains.kotlinx.spark.extensions.`KSparkExtensions$`
import scala.Tuple2
import scala.reflect.ClassTag

fun main() = withSpark {
    val ds = sc.parallelize(
        listOf(1 X "a", 2 X "b", 3 X "c", 4 X "d", 5 X "e", 6 X "f", 7 X "g", 8 X "h", 9 X "i", 10 X "j")
    ).toPairRDD()




}

/**
 * Generic function to combine the elements for each key using a custom set of aggregation
 * functions. Turns a JavaPairRDD[(K, V)] into a result of type JavaPairRDD[(K, C)], for a
 * "combined type" C.
 *
 * Users provide three functions:
 *
 *  - `createCombiner`, which turns a V into a C (e.g., creates a one-element list)
 *  - `mergeValue`, to merge a V into a C (e.g., adds it to the end of a list)
 *  - `mergeCombiners`, to combine two C's into a single one.
 *
 * In addition, users can control the partitioning of the output RDD, the serializer that is use
 * for the shuffle, and whether to perform map-side aggregation (if a mapper can produce multiple
 * items with the same key).
 *
 * @note V and C can be different -- for example, one might group an RDD of type (Int, Int) into
 * an RDD of type (Int, List[Int]).
 */
fun <K, V, C> JavaRDD<Tuple2<K, V>>.combineByKey(
    createCombiner: (V) -> C,
    mergeValue: (C, V) -> C,
    mergeCombiners: (C, C) -> C,
    partitioner: Partitioner,
    mapSideCombine: Boolean = true,
    serializer: Serializer? = null,
): JavaRDD<Tuple2<K, C>> =
    toPairRDD()
        .combineByKey(createCombiner, mergeValue, mergeCombiners, partitioner, mapSideCombine, serializer)
        .toTupleRDD()


/**
 * Simplified version of combineByKey that hash-partitions the output RDD and uses map-side
 * aggregation.
 */
fun <K, V, C> JavaRDD<Tuple2<K, V>>.combineByKey(
    createCombiner: (V) -> C,
    mergeValue: (C, V) -> C,
    mergeCombiners: (C, C) -> C,
    numPartitions: Int = rdd().sparkContext().defaultParallelism(),
): JavaRDD<Tuple2<K, C>> =
    toPairRDD()
        .combineByKey(createCombiner, mergeValue, mergeCombiners, numPartitions)
        .toTupleRDD()

/**
 * Merge the values for each key using an associative and commutative reduce function. This will
 * also perform the merging locally on each mapper before sending results to a reducer, similarly
 * to a "combiner" in MapReduce.
 */
fun <K, V> JavaRDD<Tuple2<K, V>>.reduceByKey(
    partitioner: Partitioner = Partitioner.defaultPartitioner(rdd(), emptyImmutableSeq()),
    func: (V, V) -> V,
): JavaRDD<Tuple2<K, V>> =
    toPairRDD()
        .reduceByKey(partitioner, func)
        .toTupleRDD()

