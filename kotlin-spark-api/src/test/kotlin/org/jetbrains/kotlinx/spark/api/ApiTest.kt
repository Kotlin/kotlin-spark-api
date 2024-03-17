package org.jetbrains.kotlinx.spark.api/*-
 * =LICENSE=
 * Kotlin Spark API
 * ----------
 * Copyright (C) 2019 - 2020 JetBrains
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
import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import scala.collection.Seq
import java.io.Serializable
import kotlin.collections.Iterator
import scala.collection.Iterator as ScalaIterator
import scala.collection.Map as ScalaMap
import scala.collection.mutable.Map as ScalaMutableMap

class ApiTest : ShouldSpec({

    context("miscellaneous integration tests") {
        withSpark(props = mapOf("spark.sql.codegen.comments" to true)) {

            should("Create Seqs") {
                spark.createDataset(seqOf(1, 2, 3), kotlinEncoderFor())
                    .collectAsList() shouldBe listOf(1, 2, 3)


                seqOf(1, 2, 3) shouldBe seqOf(1, 2, 3)
                mutableSeqOf(1, 2, 3) shouldBe mutableSeqOf(1, 2, 3)

                seqOf<Int>() shouldBe emptySeq<Int>()
                mutableSeqOf<Int>() shouldBe emptyMutableSeq<Int>()
            }

            @OptIn(ExperimentalStdlibApi::class)
            should("broadcast variables") {
                val largeList = (1..15).map { SomeClass(a = (it..15).toList().toIntArray(), b = it) }
                val broadcast = spark.broadcast(largeList)
                val broadcast2 = spark.broadcast(arrayOf(doubleArrayOf(1.0, 2.0, 3.0, 4.0)))

                val result: List<Double> = listOf(1, 2, 3, 4, 5)
                    .toDS()
                    .mapPartitions { iterator ->
                        val receivedBroadcast = broadcast.value
                        val receivedBroadcast2 = broadcast2.value

                        buildList {
                            iterator.forEach {
                                this.add(it + receivedBroadcast[it].b * receivedBroadcast2[0][0])
                            }
                        }.iterator()
                    }
                    .collectAsList()

                expect(result).toContain.inOrder.only.values(3.0, 5.0, 7.0, 9.0, 11.0)
            }

            should("Handle JavaConversions in Kotlin") {
                // Test the iterator conversion
                val scalaIterator: ScalaIterator<String> = listOf("test1", "test2").iterator().asScalaIterator()
                scalaIterator.next() shouldBe "test1"

                val kotlinIterator: Iterator<String> = scalaIterator.asKotlinIterator()
                kotlinIterator.next() shouldBe "test2"


                val scalaMap: ScalaMap<Int, String> = mapOf(1 to "a", 2 to "b").asScalaMap()
                scalaMap.get(1).get() shouldBe "a"
                scalaMap.get(2).get() shouldBe "b"

                val kotlinMap: Map<Int, String> = scalaMap.asKotlinMap()
                kotlinMap[1] shouldBe "a"
                kotlinMap[2] shouldBe "b"


                val scalaMutableMap: ScalaMutableMap<Int, String> = mutableMapOf(1 to "a").asScalaMutableMap()
                scalaMutableMap.get(1).get() shouldBe "a"

                scalaMutableMap.put(2, "b")

                val kotlinMutableMap: MutableMap<Int, String> = scalaMutableMap.asKotlinMutableMap()
                kotlinMutableMap[1] shouldBe "a"
                kotlinMutableMap[2] shouldBe "b"

                val scalaSeq: Seq<String> = listOf("a", "b").iterator().asScalaIterator().toSeq()
                scalaSeq.take(1).toList().last() shouldBe "a"
                scalaSeq.take(2).toList().last() shouldBe "b"

                val kotlinList: List<String> = scalaSeq.asKotlinList()
                kotlinList.first() shouldBe "a"
                kotlinList.last() shouldBe "b"
            }

            should("Map iterators") {
                val data = (1..50).toList()
                val iterator = iterator { yieldAll(data) }
                    .map { it.toString() }

                iterator.asSequence().toList() shouldBe data.map { it.toString() }
            }

            should("Filter iterators") {
                val data = (1..50).toList()
                val iterator = iterator { yieldAll(data) }
                    .filter { it % 2 == 0 }

                iterator.asSequence().toList() shouldBe data.filter { it % 2 == 0 }
            }

            should("Partition iterators") {
                val data = (1..50).toList()

                val iterator1 = iterator { yieldAll(data) }
                    .partition(8, cutIncomplete = false)
                val result1 = iterator1.asSequence().toList()
                result1.size shouldBe (50 / 8 + 1)
                result1.map { it.size }.distinct().size shouldBe 2 // two difference sizes should exist, 8 and the rest

                val iterator2 = iterator { yieldAll(data) }
                    .partition(8, cutIncomplete = true)

                val result2 = iterator2.asSequence().toList()
                result2.size shouldBe (50 / 8)
                result2.forEach { it.size shouldBe 8 }
            }

            should("Flatten iterators") {
                val data = (1..50).toList()
                val (data1, data2) = data.partition { it <= 25 }
                val iterator = iterator {
                    yield(data1.iterator())
                    yield(data2.iterator())
                }.flatten()

                iterator.asSequence().toList() shouldBe data
            }

            should("Flatmap iterators using transformAsSequence") {
                val data = (1..50).toList()
                val iterator = data.iterator()
                    .transformAsSequence {
                        flatMap {
                            listOf(it.toDouble(), it + 0.5)
                        }
                    }

                iterator.asSequence().toList() shouldBe data.flatMap { listOf(it.toDouble(), it + 0.5) }
            }
        }
    }
})


// (data) class must be Serializable to be broadcast
data class SomeClass(val a: IntArray, val b: Int) : Serializable
