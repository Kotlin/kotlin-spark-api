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

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.apache.spark.api.java.JavaDoubleRDD
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions
import org.apache.spark.sql.streaming.GroupState
import org.apache.spark.sql.streaming.GroupStateTimeout
import org.jetbrains.kotlinx.spark.api.tuples.*
import scala.Tuple2
import scala.Tuple3
import scala.Tuple4
import scala.Tuple5
import java.io.Serializable

class DatasetFunctionTest : ShouldSpec({

    context("dataset extensions") {
        withSpark(props = mapOf("spark.sql.codegen.comments" to true)) {

            should("handle cached operations") {
                val result = dsOf(1, 2, 3, 4, 5)
                    .map { it X (it + 2) }
                    .withCached {
                        expect(collectAsList()).contains.inAnyOrder.only.values(
                            1 X 3,
                            2 X 4,
                            3 X 5,
                            4 X 6,
                            5 X 7,
                        )

                        val next = filter { it._1 % 2 == 0 }
                        expect(next.collectAsList()).contains.inAnyOrder.only.values(2 X 4, 4 X 6)
                        next
                    }
                    .map { it: Tuple2<Int, Int> ->
                        it + (it._1 + it._2) * 2
                    }
                    .collectAsList()
                expect(result).contains.inOrder.only.values(2 X 4 X 12, 4 X 6 X 20)
            }

            should("handle join operations") {
                data class Left(val id: Int, val name: String)

                data class Right(val id: Int, val value: Int)

                val first = dsOf(Left(1, "a"), Left(2, "b"))
                val second = dsOf(Right(1, 100), Right(3, 300))
                val result = first
                    .leftJoin(second, first.col("id") eq second.col("id"))
                    .map { it._1.id X it._1.name X it._2?.value }
                    .collectAsList()
                expect(result).contains.inOrder.only.values(t(1, "a", 100), t(2, "b", null))
            }

            should("handle map operations") {
                val result = dsOf(listOf(1, 2, 3, 4), listOf(3, 4, 5, 6))
                    .flatMap { it.iterator() }
                    .map { it + 4 }
                    .filter { it < 10 }
                    .collectAsList()
                expect(result).contains.inAnyOrder.only.values(5, 6, 7, 8, 7, 8, 9)
            }

            should("Allow simple forEachPartition in datasets") {
                val dataset = dsOf(
                    SomeClass(intArrayOf(1, 2, 3), 1),
                    SomeClass(intArrayOf(4, 3, 2), 1),
                )
                dataset.forEachPartition {
                    it.forEach {
                        it.b shouldBe 1
                    }
                }
            }

            should("Have easier access to keys and values for key/value datasets") {
                val dataset: Dataset<SomeClass> = dsOf(
                    SomeClass(intArrayOf(1, 2, 3), 1),
                    SomeClass(intArrayOf(4, 3, 2), 1),
                )
                    .groupByKey { it.b }
                    .reduceGroupsK { a, b -> SomeClass(a.a + b.a, a.b) }
                    .takeValues()

                dataset.count() shouldBe 1
            }

            should("Be able to sort datasets with property reference") {
                val dataset: Dataset<SomeClass> = dsOf(
                    SomeClass(intArrayOf(1, 2, 3), 2),
                    SomeClass(intArrayOf(4, 3, 2), 1),
                )
                dataset.sort(SomeClass::b)
                dataset.takeAsList(1).first().b shouldBe 2

                dataset.sort(SomeClass::a, SomeClass::b)
                dataset.takeAsList(1).first().b shouldBe 2
            }

            should("Have Kotlin ready functions in place of overload ambiguity") {
                val dataset: Tuple2<Int, SomeClass> = dsOf(
                    SomeClass(intArrayOf(1, 2, 3), 1),
                    SomeClass(intArrayOf(4, 3, 2), 1),
                )
                    .groupByKey { it: SomeClass -> it.b }
                    .reduceGroupsK { v1: SomeClass, v2: SomeClass -> v1 }
                    .filter { it: Tuple2<Int, SomeClass> -> true } // not sure why this does work, but reduce doesn't
                    .reduceK { v1: Tuple2<Int, SomeClass>, v2: Tuple2<Int, SomeClass> -> v1 }

                dataset._2.a shouldBe intArrayOf(1, 2, 3)
            }
        }
    }

    context("grouped dataset extensions") {
        withSpark(props = mapOf("spark.sql.codegen.comments" to true)) {

            should("perform flat map on grouped datasets") {
                val groupedDataset = listOf(t(1, "a"), t(1, "b"), t(2, "c"))
                    .toDS()
                    .groupByKey { it._1 }

                val flatMapped = groupedDataset.flatMapGroups { key, values ->
                    val collected = values.asSequence().toList()

                    if (collected.size > 1) collected.iterator()
                    else emptyList<Tuple2<Int, String>>().iterator()
                }

                flatMapped.count() shouldBe 2
            }

            should("perform map group with state and timeout conf on grouped datasets") {
                val groupedDataset = listOf(t(1, "a"), t(1, "b"), t(2, "c"))
                    .toDS()
                    .groupByKey { it._1 }

                val mappedWithStateTimeoutConf =
                    groupedDataset.mapGroupsWithState(GroupStateTimeout.NoTimeout()) { key, values, state: GroupState<Int> ->
                        var s by state
                        val collected = values.asSequence().toList()

                        s = key
                        s shouldBe key

                        s!! X collected.map { it._2 }
                    }

                mappedWithStateTimeoutConf.count() shouldBe 2
            }

            should("perform map group with state on grouped datasets") {
                val groupedDataset = listOf(t(1, "a"), t(1, "b"), t(2, "c"))
                    .toDS()
                    .groupByKey { it._1 }

                val mappedWithState = groupedDataset.mapGroupsWithState { key, values, state: GroupState<Int> ->
                    var s by state
                    val collected = values.asSequence().toList()

                    s = key
                    s shouldBe key

                    s!! X collected.map { it._2 }
                }

                mappedWithState.count() shouldBe 2
            }

            should("perform flat map group with state on grouped datasets") {
                val groupedDataset = listOf(t(1, "a"), t(1, "b"), t(2, "c"))
                    .toDS()
                    .groupByKey { it._1 }

                val flatMappedWithState = groupedDataset.mapGroupsWithState { key, values, state: GroupState<Int> ->
                    var s by state
                    val collected = values.asSequence().toList()

                    s = key
                    s shouldBe key

                    if (collected.size > 1) collected.iterator()
                    else emptyList<Tuple2<Int, String>>().iterator()
                }

                flatMappedWithState.count() shouldBe 2
            }

            should("be able to cogroup grouped datasets") {
                val groupedDataset1 = listOf(1 X "a", 1 X "b", 2 X "c")
                    .toDS()
                    .groupByKey { it._1 }

                val groupedDataset2 = listOf(1 X "d", 5 X "e", 3 X "f")
                    .toDS()
                    .groupByKey { it._1 }

                val cogrouped = groupedDataset1.cogroup(groupedDataset2) { key, left, right ->
                    listOf(
                        key to (left.asSequence() + right.asSequence()).map { it._2 }.toList()
                    ).iterator()
                }

                cogrouped.count() shouldBe 4
            }
        }
    }

    context("RDD conversions") {
        withSpark(props = mapOf("spark.sql.codegen.comments" to true)) {

            should("Convert Scala RDD to Dataset") {
                val rdd0: RDD<Int> = sc.parallelize(
                    listOf(1, 2, 3, 4, 5, 6)
                ).rdd()
                val dataset0: Dataset<Int> = rdd0.toDS()

                dataset0.toList<Int>() shouldBe listOf(1, 2, 3, 4, 5, 6)
            }

            should("Convert a JavaRDD to a Dataset") {
                val rdd1: JavaRDD<Int> = sc.parallelize(
                    listOf(1, 2, 3, 4, 5, 6)
                )
                val dataset1: Dataset<Int> = rdd1.toDS()

                dataset1.toList<Int>() shouldBe listOf(1, 2, 3, 4, 5, 6)
            }

            should("Convert JavaDoubleRDD to Dataset") {

                // JavaDoubleRDD
                val rdd2: JavaDoubleRDD = sc.parallelizeDoubles(
                    listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
                )
                val dataset2: Dataset<Double> = rdd2.toDS()

                dataset2.toList<Double>() shouldBe listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
            }

            should("Convert JavaPairRDD to Dataset") {
                val rdd3: JavaPairRDD<Int, Double> = sc.parallelizePairs(
                    listOf(t(1, 1.0), t(2, 2.0), t(3, 3.0))
                )
                val dataset3: Dataset<Tuple2<Int, Double>> = rdd3.toDS()

                dataset3.toList<Tuple2<Int, Double>>() shouldBe listOf(t(1, 1.0), t(2, 2.0), t(3, 3.0))
            }

            should("Convert Kotlin Serializable data class RDD to Dataset") {
                val rdd4 = sc.parallelize(
                    listOf(SomeClass(intArrayOf(1, 2), 0))
                )
                val dataset4 = rdd4.toDS()

                dataset4.toList<SomeClass>().first().let { (a, b) ->
                    a contentEquals intArrayOf(1, 2) shouldBe true
                    b shouldBe 0
                }
            }

            should("Convert Tuple RDD to Dataset") {
                val rdd5 = sc.parallelize(
                    listOf(t(1.0, 4))
                )
                val dataset5 = rdd5.toDS()

                dataset5.toList<Tuple2<Double, Int>>() shouldBe listOf(t(1.0, 4))
            }

            should("Convert List RDD to Dataset") {
                val rdd6 = sc.parallelize(
                    listOf(listOf(1, 2, 3), listOf(4, 5, 6))
                )
                val dataset6 = rdd6.toDS()

                dataset6.toList<List<Int>>() shouldBe listOf(listOf(1, 2, 3), listOf(4, 5, 6))
            }

            should("Sort Tuple2 Dataset") {
                val list = listOf(
                    t(1, 6),
                    t(2, 5),
                    t(3, 4),
                )
                val dataset = list.toDS()

                dataset.sortByKey().collectAsList() shouldBe list.sortedBy { it._1 }
                dataset.sortByValue().collectAsList() shouldBe list.sortedBy { it._2 }
            }

            should("Sort Pair Dataset") {
                val list = listOf(
                    Pair(1, 6),
                    Pair(2, 5),
                    Pair(3, 4),
                )
                val dataset = list.toDS()

                dataset.sortByKey().collectAsList() shouldBe list.sortedBy { it.first }
                dataset.sortByValue().collectAsList() shouldBe list.sortedBy { it.second }
            }
        }
    }

    context("Column functions") {
        withSpark(props = mapOf("spark.sql.codegen.comments" to true)) {

            @Suppress("UNCHECKED_CAST")
            should("support dataset select") {
                val dataset = dsOf(
                    SomeClass(intArrayOf(1, 2, 3), 3),
                    SomeClass(intArrayOf(1, 2, 4), 5),
                )

                val newDS1WithAs: Dataset<IntArray> = dataset.selectTyped(
                    functions.col("a").`as`<IntArray>(),
                )
                newDS1WithAs.collectAsList()

                val newDS2: Dataset<Tuple2<IntArray, Int>> = dataset.selectTyped(
                    col(SomeClass::a), // NOTE: this only works on 3.0, returning a data class with an array in it
                    col(SomeClass::b),
                )
                newDS2.collectAsList()

                val newDS3: Dataset<Tuple3<IntArray, Int, Int>> = dataset.selectTyped(
                    col(SomeClass::a),
                    col(SomeClass::b),
                    col(SomeClass::b),
                )
                newDS3.collectAsList()

                val newDS4: Dataset<Tuple4<IntArray, Int, Int, Int>> = dataset.selectTyped(
                    col(SomeClass::a),
                    col(SomeClass::b),
                    col(SomeClass::b),
                    col(SomeClass::b),
                )
                newDS4.collectAsList()

                val newDS5: Dataset<Tuple5<IntArray, Int, Int, Int, Int>> = dataset.selectTyped(
                    col(SomeClass::a),
                    col(SomeClass::b),
                    col(SomeClass::b),
                    col(SomeClass::b),
                    col(SomeClass::b),
                )
                newDS5.collectAsList()
            }

            should("Access columns using invoke on datasets") {
                val dataset = dsOf(
                    SomeClass(intArrayOf(1, 2, 3), 4),
                    SomeClass(intArrayOf(4, 3, 2), 1),
                )

                dataset.col("a") shouldBe dataset("a")
            }

            should("Use infix- and operator funs on columns") {
                val dataset = dsOf(
                    SomeOtherClass(intArrayOf(1, 2, 3), 4, true),
                    SomeOtherClass(intArrayOf(4, 3, 2), 1, true),
                )

                (dataset("a") == dataset("a")) shouldBe dataset("a").equals(dataset("a"))
                (dataset("a") != dataset("a")) shouldBe !dataset("a").equals(dataset("a"))
                (dataset("a") eq dataset("a")) shouldBe dataset("a").equalTo(dataset("a"))
                dataset("a").equalTo(dataset("a")) shouldBe (dataset("a") `===` dataset("a"))
                (dataset("a") neq dataset("a")) shouldBe dataset("a").notEqual(dataset("a"))
                dataset("a").notEqual(dataset("a")) shouldBe (dataset("a") `=!=` dataset("a"))
                !(dataset("a") eq dataset("a")) shouldBe dataset("a").notEqual(dataset("a"))
                dataset("a").notEqual(dataset("a")) shouldBe (!(dataset("a") `===` dataset("a")))
                -dataset("b") shouldBe functions.negate(dataset("b"))
                !dataset("c") shouldBe functions.not(dataset("c"))
                dataset("b") gt 3 shouldBe dataset("b").gt(3)
                dataset("b") lt 3 shouldBe dataset("b").lt(3)
                dataset("b") leq 3 shouldBe dataset("b").leq(3)
                dataset("b") geq 3 shouldBe dataset("b").geq(3)
                dataset("b") inRangeOf 0..2 shouldBe dataset("b").between(0, 2)
                dataset("c") or dataset("c") shouldBe dataset("c").or(dataset("c"))
                dataset("c") and dataset("c") shouldBe dataset("c").and(dataset("c"))
                dataset("c").and(dataset("c")) shouldBe (dataset("c") `&&` dataset("c"))
                dataset("b") + dataset("b") shouldBe dataset("b").plus(dataset("b"))
                dataset("b") - dataset("b") shouldBe dataset("b").minus(dataset("b"))
                dataset("b") * dataset("b") shouldBe dataset("b").multiply(dataset("b"))
                dataset("b") / dataset("b") shouldBe dataset("b").divide(dataset("b"))
                dataset("b") % dataset("b") shouldBe dataset("b").mod(dataset("b"))
                dataset("b")[0] shouldBe dataset("b").getItem(0)
            }

            should("Handle TypedColumns") {
                val dataset = dsOf(
                    SomeOtherClass(intArrayOf(1, 2, 3), 4, true),
                    SomeOtherClass(intArrayOf(4, 3, 2), 1, true),
                )

                // walking over all column creation methods
                val b: Dataset<Tuple3<Int, IntArray, Boolean>> = dataset.select(
                    dataset.col(SomeOtherClass::b),
                    dataset(SomeOtherClass::a),
                    col(SomeOtherClass::c),
                )
                b.collectAsList()
            }

            should("Handle some where queries using column operator functions") {
                val dataset = dsOf(
                    SomeOtherClass(intArrayOf(1, 2, 3), 4, true),
                    SomeOtherClass(intArrayOf(4, 3, 2), 1, true),
                )
                dataset.collectAsList()

                val column = functions.col("b").`as`<IntArray>()

                val b = dataset.where(column gt 3 and col(SomeOtherClass::c))

                b.count() shouldBe 1
            }


        }
    }
})

data class SomeOtherClass(val a: IntArray, val b: Int, val c: Boolean) : Serializable
