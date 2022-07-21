package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.apache.spark.api.java.JavaRDD
import org.jetbrains.kotlinx.spark.api.tuples.*
import scala.Tuple2

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

                should("Have handy functions") {
                    val rdd = rddOf(
                        1 X "a",
                        2 X "b",
                        3 X "c",
                        4 X "d",
                        5 X "e",
                        6 X "f",
                    )

                    //#if sparkMinor >= 3.1
                    val rangeFiltered: JavaRDD<Tuple2<Int, String>> = rdd.filterByRange(2..5)
                    rangeFiltered.collect().shouldContainAll(
                        2 X "b",
                        3 X "c",
                        4 X "d",
                        5 X "e",
                    )
                    //#endif

                    val result = rdd
                        .flatMapValues {
                            listOf(it + 1, it + 2, it + 3, it + 4).iterator()
                        }
                        .also {
                            it.countByKey().values.forEach { it shouldBe 4 }
                        }
                        .foldByKey("", String::plus) // (1,"a1a2a3a4") etc.
                        .mapValues { it.toSortedSet().fold("", String::plus) } // (1,"1234a") etc.
                        .map { it.swap() } // ("1234a",1) etc.
                        .mapKeys { it.take(4) } // ("1234",1) etc.
                        .groupByKey()
                        .mapValues { it.toList() } // ("1234",[1,2,3,4,5,6])
                        .collect()
                        .single()

                    result shouldBe t("1234", listOf(1, 2, 3, 4, 5, 6))
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