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

                expect(result).contains.inOrder.only.values(3.0, 5.0, 7.0, 9.0, 11.0)
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
        }
    }
})


// (data) class must be Serializable to be broadcast
data class SomeClass(val a: IntArray, val b: Int) : Serializable
