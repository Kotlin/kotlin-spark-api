package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.spark.api.tuples.*

class RddTest : ShouldSpec({
    context("RDD extension functions") {

        withSpark(logLevel = SparkLogLevel.DEBUG) {

            context("Key/value") {
                should("work with spark example") {
                    val rdd = rddOf(1, 1, 2, 2, 2, 3).map(Int::toString)

                    val pairs = rdd.map { it X 1 }
                    val counts = pairs.reduceByKey { a, b -> a + b }
                    val list = counts.collect().toList()
                    list.shouldContainAll("1" X 2, "2" X 3, "3" X 1)
                }
            }

            context("Double functions") {
                should("get max/min") {
                    val rdd = rddOf(1, 1, 2, 2, 2, 3)

                    rdd.max() shouldBe 3.0
                    rdd.min() shouldBe 1.0
                }

                context("Work with any number") {

                    should("Work with Bytes") {
                        val data = listOf(1, 1, 2, 2, 2, 3).map(Int::toByte)
                        val rdd = data.toRDD()
                        rdd.sum() shouldBe data.sum().toDouble()
                    }

                    should("Work with Shorts") {
                        val data = listOf(1, 1, 2, 2, 2, 3).map(Int::toShort)
                        val rdd = data.toRDD()
                        rdd.sum() shouldBe data.sum().toDouble()
                    }

                    should("Work with Ints") {
                        val data = listOf(1, 1, 2, 2, 2, 3)
                        val rdd = data.toRDD()
                        rdd.sum() shouldBe data.sum().toDouble()
                    }

                    should("Work with Longs") {
                        val data = listOf(1, 1, 2, 2, 2, 3).map(Int::toLong)
                        val rdd = data.toRDD()
                        rdd.sum() shouldBe data.sum().toDouble()
                    }

                    should("Work with Floats") {
                        val data = listOf(1, 1, 2, 2, 2, 3).map(Int::toFloat)
                        val rdd = data.toRDD()
                        rdd.sum() shouldBe data.sum().toDouble()
                    }

                    should("Work with Doubles") {
                        val data = listOf(1, 1, 2, 2, 2, 3).map(Int::toDouble)
                        val rdd = data.toRDD().toJavaDoubleRDD()
                        rdd.sum() shouldBe data.sum().toDouble()
                    }
                }
            }
        }
    }
})