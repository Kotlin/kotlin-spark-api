package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.spark.api.tuples.*

class RddTest : ShouldSpec({
    context("RDD extension functions") {

        withSpark(logLevel = SparkLogLevel.DEBUG) {

            context("Key/value") {
                should("work with spark example") {
                    val data = listOf(1, 1, 2, 2, 2, 3).map(Int::toString)
                    val rdd = sc.parallelize(data)

                    val pairs = rdd.map { it X 1 }
                    val counts = pairs.reduceByKey { a, b -> a + b }
                    val list = counts.collect().toList()
                    list.shouldContainAll("1" X 2, "2" X 3, "3" X 1)
                }
            }

            context("Double") {
                should("get max/min") {
                    val data = listOf(1, 1, 2, 2, 2, 3).map(Int::toDouble)
                    val rdd = sc.parallelize(data)
                    rdd.max() shouldBe 3.0
                    rdd.min() shouldBe 1.0
                }
            }
        }
    }
})