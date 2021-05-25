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
import ch.tutteli.atrium.domain.builders.migration.asExpect
import ch.tutteli.atrium.verbs.expect
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import scala.Tuple1
import scala.Tuple2
import scala.Tuple3
import org.apache.spark.sql.streaming.GroupState
import org.apache.spark.sql.streaming.GroupStateTimeout
import scala.collection.Seq
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.TypedColumn
import org.apache.spark.sql.functions.*
import scala.Product
import java.io.Serializable
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import kotlin.reflect.KProperty1
import scala.collection.Iterator as ScalaIterator
import scala.collection.Map as ScalaMap
import scala.collection.mutable.Map as ScalaMutableMap

class ApiTest : ShouldSpec({
    context("integration tests") {
        withSpark(props = mapOf("spark.sql.codegen.comments" to true)) {
            should("collect data classes with doubles correctly") {
                val ll1 = LonLat(1.0, 2.0)
                val ll2 = LonLat(3.0, 4.0)
                val lonlats = dsOf(ll1, ll2).collectAsList()
                expect(lonlats).asExpect().contains.inAnyOrder.only.values(ll1.copy(), ll2.copy())
            }
            should("contain all generic primitives with complex schema") {
                val primitives = c(1, 1.0, 1.toFloat(), 1.toByte(), LocalDate.now(), true)
                val primitives2 = c(2, 2.0, 2.toFloat(), 2.toByte(), LocalDate.now().plusDays(1), false)
                val tuples = dsOf(primitives, primitives2).collectAsList()
                expect(tuples).asExpect().contains.inAnyOrder.only.values(primitives, primitives2)
            }
            should("contain all generic primitives with complex nullable schema") {
                val primitives = c(1, 1.0, 1.toFloat(), 1.toByte(), LocalDate.now(), true)
                val nulls = c(null, null, null, null, null, null)
                val tuples = dsOf(primitives, nulls).collectAsList()
                expect(tuples).asExpect().contains.inAnyOrder.only.values(primitives, nulls)
            }
            should("handle cached operations") {
                val result = dsOf(1, 2, 3, 4, 5)
                        .map { it to (it + 2) }
                        .withCached {
                            expect(collectAsList()).asExpect().contains.inAnyOrder.only.values(1 to 3, 2 to 4, 3 to 5, 4 to 6, 5 to 7)

                            val next = filter { it.first % 2 == 0 }
                            expect(next.collectAsList()).asExpect().contains.inAnyOrder.only.values(2 to 4, 4 to 6)
                            next
                        }
                        .map { c(it.first, it.second, (it.first + it.second) * 2) }
                        .collectAsList()
                expect(result).asExpect().contains.inOrder.only.values(c(2, 4, 12), c(4, 6, 20))
            }
            should("handle join operations") {
                data class Left(val id: Int, val name: String)

                data class Right(val id: Int, val value: Int)

                val first = dsOf(Left(1, "a"), Left(2, "b"))
                val second = dsOf(Right(1, 100), Right(3, 300))
                val result = first
                        .leftJoin(second, first.col("id").eq(second.col("id")))
                        .map { c(it.first.id, it.first.name, it.second?.value) }
                        .collectAsList()
                expect(result).asExpect().contains.inOrder.only.values(c(1, "a", 100), c(2, "b", null))
            }
            should("handle map operations") {
                val result = dsOf(listOf(1, 2, 3, 4), listOf(3, 4, 5, 6))
                        .flatMap { it.iterator() }
                        .map { it + 4 }
                        .filter { it < 10 }
                        .collectAsList()
                expect(result).asExpect().contains.inAnyOrder.only.values(5, 6, 7, 8, 7, 8, 9)
            }
            should("handle strings converted to lists") {
                data class Movie(val id: Long, val genres: String)
                data class MovieExpanded(val id: Long, val genres: List<String>)

                val comedies = listOf(Movie(1, "Comedy|Romance"), Movie(2, "Horror|Action")).toDS()
                        .map { MovieExpanded(it.id, it.genres.split("|").toList()) }
                        .filter { it.genres.contains("Comedy") }
                        .collectAsList()
                expect(comedies).asExpect().contains.inAnyOrder.only.values(MovieExpanded(1, listOf("Comedy", "Romance")))
            }
            should("handle strings converted to arrays") {
                data class Movie(val id: Long, val genres: String)
                data class MovieExpanded(val id: Long, val genres: Array<String>) {
                    override fun equals(other: Any?): Boolean {
                        if (this === other) return true
                        if (javaClass != other?.javaClass) return false
                        other as MovieExpanded
                        return if (id != other.id) false else genres.contentEquals(other.genres)
                    }

                    override fun hashCode(): Int {
                        var result = id.hashCode()
                        result = 31 * result + genres.contentHashCode()
                        return result
                    }
                }

                val comedies = listOf(Movie(1, "Comedy|Romance"), Movie(2, "Horror|Action")).toDS()
                        .map { MovieExpanded(it.id, it.genres.split("|").toTypedArray()) }
                        .filter { it.genres.contains("Comedy") }
                        .collectAsList()
                expect(comedies).asExpect().contains.inAnyOrder.only.values(MovieExpanded(1, arrayOf("Comedy", "Romance")))
            }
            should("handle arrays of generics") {
                data class Test<Z>(val id: Long, val data: Array<Pair<Z, Int>>) {
                    override fun equals(other: Any?): Boolean {
                        if (this === other) return true
                        if (javaClass != other?.javaClass) return false

                        other as Test<*>

                        if (id != other.id) return false
                        if (!data.contentEquals(other.data)) return false

                        return true
                    }

                    override fun hashCode(): Int {
                        var result = id.hashCode()
                        result = 31 * result + data.contentHashCode()
                        return result
                    }
                }

                val result = listOf(Test(1, arrayOf(5.1 to 6, 6.1 to 7)))
                        .toDS()
                        .map { it.id to it.data.firstOrNull { liEl -> liEl.first < 6 } }
                        .map { it.second }
                        .collectAsList()
                expect(result).asExpect().contains.inOrder.only.values(5.1 to 6)
            }
            should("!handle primitive arrays") {
                val result = listOf(arrayOf(1, 2, 3, 4))
                        .toDS()
                        .map { it.map { ai -> ai + 1 } }
                        .collectAsList()
                        .flatten()
                expect(result).asExpect().contains.inOrder.only.values(2, 3, 4, 5)

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

                expect(result).asExpect().contains.inOrder.only.values(3.0, 5.0, 7.0, 9.0, 11.0)
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
            should("perform flat map on grouped datasets") {
                val groupedDataset = listOf(1 to "a", 1 to "b", 2 to "c")
                    .toDS()
                    .groupByKey { it.first }

                val flatMapped = groupedDataset.flatMapGroups { key, values ->
                    val collected = values.asSequence().toList()

                    if (collected.size > 1) collected.iterator()
                    else emptyList<Pair<Int, String>>().iterator()
                }

                flatMapped.count() shouldBe 2
            }
            should("perform map group with state and timeout conf on grouped datasets") {
                val groupedDataset = listOf(1 to "a", 1 to "b", 2 to "c")
                    .toDS()
                    .groupByKey { it.first }

                val mappedWithStateTimeoutConf =
                    groupedDataset.mapGroupsWithState(GroupStateTimeout.NoTimeout()) { key, values, state: GroupState<Int> ->
                        var s by state
                        val collected = values.asSequence().toList()

                        s = key
                        s shouldBe key

                        s!! to collected.map { it.second }
                    }

                mappedWithStateTimeoutConf.count() shouldBe 2
            }
            should("perform map group with state on grouped datasets") {
                val groupedDataset = listOf(1 to "a", 1 to "b", 2 to "c")
                    .toDS()
                    .groupByKey { it.first }

                val mappedWithState = groupedDataset.mapGroupsWithState { key, values, state: GroupState<Int> ->
                    var s by state
                    val collected = values.asSequence().toList()

                    s = key
                    s shouldBe key

                    s!! to collected.map { it.second }
                }

                mappedWithState.count() shouldBe 2
            }
            should("perform flat map group with state on grouped datasets") {
                val groupedDataset = listOf(1 to "a", 1 to "b", 2 to "c")
                    .toDS()
                    .groupByKey { it.first }

                val flatMappedWithState = groupedDataset.mapGroupsWithState { key, values, state: GroupState<Int> ->
                    var s by state
                    val collected = values.asSequence().toList()

                    s = key
                    s shouldBe key

                    if (collected.size > 1) collected.iterator()
                    else emptyList<Pair<Int, String>>().iterator()
                }

                flatMappedWithState.count() shouldBe 2
            }
            should("be able to cogroup grouped datasets") {
                val groupedDataset1 = listOf(1 to "a", 1 to "b", 2 to "c")
                    .toDS()
                    .groupByKey { it.first }

                val groupedDataset2 = listOf(1 to "d", 5 to "e", 3 to "f")
                    .toDS()
                    .groupByKey { it.first }

                val cogrouped = groupedDataset1.cogroup(groupedDataset2) { key, left, right ->
                    listOf(
                        key to (left.asSequence() + right.asSequence())
                            .map { it.second }
                            .toList()
                    ).iterator()
                }

                cogrouped.count() shouldBe 4
            }
            should("handle LocalDate Datasets") { // uses encoder
                val dataset: Dataset<LocalDate> = dsOf(LocalDate.now(), LocalDate.now())
                dataset.show()
            }
            should("handle Instant Datasets") { // uses encoder
                val dataset: Dataset<Instant> = dsOf(Instant.now(), Instant.now())
                dataset.show()
            }
            should("be able to serialize Date") { // uses knownDataTypes
                val dataset: Dataset<Pair<Date, Int>> = dsOf(Date.valueOf("2020-02-10") to 5)
                dataset.show()
            }
            should("handle Timestamp Datasets") { // uses encoder
                val dataset = dsOf(Timestamp(0L))
                dataset.show()
            }
            should("be able to serialize Timestamp") { // uses knownDataTypes
                val dataset = dsOf(Timestamp(0L) to 2)
                dataset.show()
            }
            should("Be able to serialize Scala Tuples including data classes") {
                val dataset = dsOf(
                    Tuple2("a", Tuple3("a", 1, LonLat(1.0, 1.0))),
                    Tuple2("b", Tuple3("b", 2, LonLat(1.0, 2.0))),
                )
                dataset.show()
                val asList = dataset.takeAsList(2)
                asList.first() shouldBe Tuple2("a", Tuple3("a", 1, LonLat(1.0, 1.0)))
            }
            should("Be able to serialize data classes with tuples") {
                val dataset = dsOf(
                    DataClassWithTuple(Tuple3(5L, "test", Tuple1(""))),
                    DataClassWithTuple(Tuple3(6L, "tessst", Tuple1(""))),
                )

                dataset.show()
                val asList = dataset.takeAsList(2)
                asList.first().tuple shouldBe Tuple3(5L, "test", Tuple1(""))
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
                -dataset("b") shouldBe negate(dataset("b"))
                !dataset("c") shouldBe not(dataset("c"))
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
                b.show()
            }
            should("Handle some where queries using column operator functions") {
                val dataset = dsOf(
                    SomeOtherClass(intArrayOf(1, 2, 3), 4, true),
                    SomeOtherClass(intArrayOf(4, 3, 2), 1, true),
                )
                dataset.show()

                val column = col("b").`as`<IntArray>()

                val b = dataset.where(column gt 3 and col(SomeOtherClass::c))
                b.show()

                b.count() shouldBe 1
            }
            should("Be able to serialize lists of data classes") {
                val dataset = dsOf(
                    listOf(SomeClass(intArrayOf(1, 2, 3), 4)),
                    listOf(SomeClass(intArrayOf(3, 2, 1), 0)),
                )
                dataset.show()
            }
            should("Be able to serialize arrays of data classes") {
                val dataset = dsOf(
                    arrayOf(SomeClass(intArrayOf(1, 2, 3), 4)),
                    arrayOf(SomeClass(intArrayOf(3, 2, 1), 0)),
                )
                dataset.show()
            }
            should("Be able to serialize lists of tuples") {
                val dataset = dsOf(
                    listOf(Tuple2(intArrayOf(1, 2, 3), 4)),
                    listOf(Tuple2(intArrayOf(3, 2, 1), 0)),
                )
                dataset.show()
            }
        }
    }
})

data class DataClassWithTuple<T : Product>(val tuple: T)

data class LonLat(val lon: Double, val lat: Double)

// (data) class must be Serializable to be broadcast
data class SomeClass(val a: IntArray, val b: Int) : Serializable

data class SomeOtherClass(val a: IntArray, val b: Int, val c: Boolean) : Serializable
