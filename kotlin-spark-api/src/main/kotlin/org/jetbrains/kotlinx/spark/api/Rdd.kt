package org.jetbrains.kotlinx.spark.api

import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext
import java.io.Serializable

/**
 * Utility method to create an RDD from a list.
 * NOTE: [T] must be [Serializable].
 */
fun <T> JavaSparkContext.rddOf(
    vararg elements: T,
    numSlices: Int = defaultParallelism(),
): JavaRDD<T> = parallelize(elements.asList(), numSlices)

/**
 * Utility method to create an RDD from a list.
 * NOTE: [T] must be [Serializable].
 */
fun <T> JavaSparkContext.toRDD(
    elements: List<T>,
    numSlices: Int = defaultParallelism(),
): JavaRDD<T> = parallelize(elements, numSlices)

/**
 * Returns the minimum element from this RDD as defined by the specified
 * [Comparator].
 *
 * @return the minimum of the RDD
 */
fun <T : Comparable<T>> JavaRDD<T>.min(): T = min(
    object : Comparator<T>, Serializable {
        override fun compare(o1: T, o2: T): Int = o1.compareTo(o2)
    }
)

/**
 * Returns the maximum element from this RDD as defined by the specified
 * [Comparator].
 *
 * @return the maximum of the RDD
 */
fun <T : Comparable<T>> JavaRDD<T>.max(): T = max(
    object : Comparator<T>, Serializable {
        override fun compare(o1: T, o2: T): Int = o1.compareTo(o2)
    }
)
