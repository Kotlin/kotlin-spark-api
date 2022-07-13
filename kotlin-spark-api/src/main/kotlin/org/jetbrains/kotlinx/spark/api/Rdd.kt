package org.jetbrains.kotlinx.spark.api

import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext


fun <T> JavaSparkContext.rddOf(
    vararg elements: T,
    numSlices: Int = defaultParallelism(),
): JavaRDD<T> = parallelize(elements.toList(), numSlices)

fun <T> JavaSparkContext.toRDD(
    elements: List<T>,
    numSlices: Int = defaultParallelism(),
): JavaRDD<T> = parallelize(elements, numSlices)
