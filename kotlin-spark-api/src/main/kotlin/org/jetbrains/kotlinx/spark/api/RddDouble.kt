package org.jetbrains.kotlinx.spark.api

import org.apache.spark.api.java.JavaDoubleRDD
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.partial.BoundedDouble
import org.apache.spark.partial.PartialResult
import org.apache.spark.rdd.RDD
import org.apache.spark.util.StatCounter
import scala.Tuple2

/** Utility method to convert [JavaRDD]<[Number]> to [JavaDoubleRDD]. */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Number> JavaRDD<T>.toJavaDoubleRDD(): JavaDoubleRDD =
    JavaDoubleRDD.fromRDD(
        when (T::class) {
            Double::class -> this
            else -> map(Number::toDouble)
        }.rdd() as RDD<Any>
    )

/** Utility method to convert [JavaDoubleRDD] to [JavaRDD]<[Double]>. */
@Suppress("UNCHECKED_CAST")
fun JavaDoubleRDD.toDoubleRDD(): JavaRDD<Double> =
    JavaDoubleRDD.toRDD(this).toJavaRDD() as JavaRDD<Double>

/** Add up the elements in this RDD. */
inline fun <reified T : Number> JavaRDD<T>.sum(): Double = toJavaDoubleRDD().sum()

/**
 * Return a [org.apache.spark.util.StatCounter] object that captures the mean, variance and
 * count of the RDD's elements in one operation.
 */
inline fun <reified T : Number> JavaRDD<T>.stats(): StatCounter = toJavaDoubleRDD().stats()

/** Compute the mean of this RDD's elements. */
inline fun <reified T : Number> JavaRDD<T>.mean(): Double = toJavaDoubleRDD().mean()

/** Compute the population variance of this RDD's elements. */
inline fun <reified T : Number> JavaRDD<T>.variance(): Double = toJavaDoubleRDD().variance()

/** Compute the population standard deviation of this RDD's elements. */
inline fun <reified T : Number> JavaRDD<T>.stdev(): Double = toJavaDoubleRDD().stdev()

/**
 * Compute the sample standard deviation of this RDD's elements (which corrects for bias in
 * estimating the standard deviation by dividing by N-1 instead of N).
 */
inline fun <reified T : Number> JavaRDD<T>.sampleStdev(): Double = toJavaDoubleRDD().sampleStdev()

/**
 * Compute the sample variance of this RDD's elements (which corrects for bias in
 * estimating the variance by dividing by N-1 instead of N).
 */
inline fun <reified T : Number> JavaRDD<T>.sampleVariance(): Double = toJavaDoubleRDD().sampleVariance()

/** Compute the population standard deviation of this RDD's elements. */
inline fun <reified T : Number> JavaRDD<T>.popStdev(): Double = toJavaDoubleRDD().popStdev()

/** Compute the population variance of this RDD's elements. */
inline fun <reified T : Number> JavaRDD<T>.popVariance(): Double = toJavaDoubleRDD().popVariance()

/** Approximate operation to return the mean within a timeout. */
inline fun <reified T : Number> JavaRDD<T>.meanApprox(
    timeout: Long,
    confidence: Double = 0.95,
): PartialResult<BoundedDouble> = toJavaDoubleRDD().meanApprox(timeout, confidence)

/** Approximate operation to return the sum within a timeout. */
inline fun <reified T : Number> JavaRDD<T>.sumApprox(
    timeout: Long,
    confidence: Double = 0.95,
): PartialResult<BoundedDouble> = toJavaDoubleRDD().sumApprox(timeout, confidence)

/**
 * Compute a histogram of the data using bucketCount number of buckets evenly
 *  spaced between the minimum and maximum of the RDD. For example if the min
 *  value is 0 and the max is 100 and there are two buckets the resulting
 *  buckets will be `[0, 50)` `[50, 100]`. bucketCount must be at least 1
 * If the RDD contains infinity, NaN throws an exception
 * If the elements in RDD do not vary (max == min) always returns a single bucket.
 */
inline fun <reified T : Number> JavaRDD<T>.histogram(bucketCount: Int): Tuple2<DoubleArray, LongArray> =
    toJavaDoubleRDD().histogram(bucketCount)

/**
 * Compute a histogram using the provided buckets. The buckets are all open
 * to the right except for the last which is closed.
 *  e.g. for the array
 *  `[1, 10, 20, 50]` the buckets are `[1, 10) [10, 20) [20, 50]`
 *  e.g. ` <=x<10, 10<=x<20, 20<=x<=50`
 *  And on the input of 1 and 50 we would have a histogram of 1, 0, 1
 *
 * Note: If your histogram is evenly spaced (e.g. `[0, 10, 20, 30]`) this can be switched
 * from an O(log n) insertion to O(1) per element. (where n = # buckets) if you set evenBuckets
 * to true.
 * buckets must be sorted and not contain any duplicates.
 * buckets array must be at least two elements
 * All NaN entries are treated the same. If you have a NaN bucket it must be
 * the maximum value of the last position and all NaN entries will be counted
 * in that bucket.
 */
inline fun <reified T : Number> JavaRDD<T>.histogram(
    buckets: Array<Double>,
    evenBuckets: Boolean = false,
): LongArray = toJavaDoubleRDD().histogram(buckets, evenBuckets)