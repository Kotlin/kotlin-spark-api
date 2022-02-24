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
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.apache.spark.api.java.JavaDoubleRDD
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions.*
import org.apache.spark.sql.streaming.GroupState
import org.apache.spark.sql.streaming.GroupStateTimeout
import org.apache.spark.sql.types.Decimal
import org.apache.spark.unsafe.types.CalendarInterval
import scala.Product
import scala.Tuple1
import scala.Tuple2
import scala.Tuple3
import scala.collection.Seq
import java.io.Serializable
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import kotlin.collections.Iterator
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
                expect(lonlats).contains.inAnyOrder.only.values(ll1.copy(), ll2.copy())
            }
            should("contain all generic primitives with complex schema") {
                val primitives = c(1, 1.0, 1.toFloat(), 1.toByte(), LocalDate.now(), true)
                val primitives2 = c(2, 2.0, 2.toFloat(), 2.toByte(), LocalDate.now().plusDays(1), false)
                val tuples = dsOf(primitives, primitives2).collectAsList()
                expect(tuples).contains.inAnyOrder.only.values(primitives, primitives2)
            }
            should("contain all generic primitives with complex nullable schema") {
                val primitives = c(1, 1.0, 1.toFloat(), 1.toByte(), LocalDate.now(), true)
                val nulls = c(null, null, null, null, null, null)
                val tuples = dsOf(primitives, nulls).collectAsList()
                expect(tuples).contains.inAnyOrder.only.values(primitives, nulls)
            }
            should("handle cached operations") {
                val result = dsOf(1, 2, 3, 4, 5)
                    .map { it to (it + 2) }
                    .withCached {
                        expect(collectAsList()).contains.inAnyOrder.only.values(
                            1 to 3,
                            2 to 4,
                            3 to 5,
                            4 to 6,
                            5 to 7
                        )

                        val next = filter { it.first % 2 == 0 }
                        expect(next.collectAsList()).contains.inAnyOrder.only.values(2 to 4, 4 to 6)
                        next
                    }
                    .map { c(it.first, it.second, (it.first + it.second) * 2) }
                    .collectAsList()
                expect(result).contains.inOrder.only.values(c(2, 4, 12), c(4, 6, 20))
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
                expect(result).contains.inOrder.only.values(c(1, "a", 100), c(2, "b", null))
            }
            should("handle map operations") {
                val result = dsOf(listOf(1, 2, 3, 4), listOf(3, 4, 5, 6))
                    .flatMap { it.iterator() }
                    .map { it + 4 }
                    .filter { it < 10 }
                    .collectAsList()
                expect(result).contains.inAnyOrder.only.values(5, 6, 7, 8, 7, 8, 9)
            }
            should("handle strings converted to lists") {
                data class Movie(val id: Long, val genres: String)
                data class MovieExpanded(val id: Long, val genres: List<String>)

                val comedies = listOf(Movie(1, "Comedy|Romance"), Movie(2, "Horror|Action")).toDS()
                    .map { MovieExpanded(it.id, it.genres.split("|").toList()) }
                    .filter { it.genres.contains("Comedy") }
                    .collectAsList()
                expect(comedies).contains.inAnyOrder.only.values(
                    MovieExpanded(
                        1,
                        listOf("Comedy", "Romance")
                    )
                )
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
                expect(comedies).contains.inAnyOrder.only.values(
                    MovieExpanded(
                        1,
                        arrayOf("Comedy", "Romance")
                    )
                )
            }
            should("handle arrays of generics") {
                data class Test<Z>(val id: Long, val data: Array<Pair<Z, Int>>)

                val result = listOf(Test(1, arrayOf(5.1 to 6, 6.1 to 7)))
                    .toDS()
                    .map { it.id to it.data.firstOrNull { liEl -> liEl.first < 6 } }
                    .map { it.second }
                    .collectAsList()
                expect(result).contains.inOrder.only.values(5.1 to 6)
            }
            should("handle lists of generics") {
                data class Test<Z>(val id: Long, val data: List<Pair<Z, Int>>)

                val result = listOf(Test(1, listOf(5.1 to 6, 6.1 to 7)))
                    .toDS()
                    .map { it.id to it.data.firstOrNull { liEl -> liEl.first < 6 } }
                    .map { it.second }
                    .collectAsList()
                expect(result).contains.inOrder.only.values(5.1 to 6)
            }
            should("!handle primitive arrays") {
                val result = listOf(arrayOf(1, 2, 3, 4))
                    .toDS()
                    .map { it.map { ai -> ai + 1 } }
                    .collectAsList()
                    .flatten()
                expect(result).contains.inOrder.only.values(2, 3, 4, 5)

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
                val dates = listOf(LocalDate.now(), LocalDate.now())
                val dataset: Dataset<LocalDate> = dates.toDS()
                dataset.collectAsList() shouldBe dates
            }
            should("handle Instant Datasets") { // uses encoder
                val instants = listOf(Instant.now(), Instant.now())
                val dataset: Dataset<Instant> = instants.toDS()
                dataset.collectAsList() shouldBe instants
            }
            should("Be able to serialize Instant") { // uses knownDataTypes
                val instantPair = Instant.now() to Instant.now()
                val dataset = dsOf(instantPair)
                dataset.collectAsList() shouldBe listOf(instantPair)
            }
            should("be able to serialize Date") { // uses knownDataTypes
                val datePair = Date.valueOf("2020-02-10") to 5
                val dataset: Dataset<Pair<Date, Int>> = dsOf(datePair)
                dataset.collectAsList() shouldBe listOf(datePair)
            }
            should("handle Timestamp Datasets") { // uses encoder
                val timeStamps = listOf(Timestamp(0L), Timestamp(1L))
                val dataset = timeStamps.toDS()
                dataset.collectAsList() shouldBe timeStamps
            }
            should("be able to serialize Timestamp") { // uses knownDataTypes
                val timestampPair = Timestamp(0L) to 2
                val dataset = dsOf(timestampPair)
                dataset.collectAsList() shouldBe listOf(timestampPair)
            }
            should("handle Duration Datasets") { // uses encoder
                val dataset = dsOf(Duration.ZERO)
                dataset.collectAsList() shouldBe listOf(Duration.ZERO)
            }
            should("handle Period Datasets") { // uses encoder
                val periods = listOf(Period.ZERO, Period.ofDays(2))
                val dataset = periods.toDS()

                dataset.show(false)

                dataset.collectAsList().let {
                    it[0] shouldBe Period.ZERO

                    // TODO this is also broken in Scala. It reports a Period of 0 instead of 2 days
                    //  https://issues.apache.org/jira/browse/SPARK-38317
//                    it[1] shouldBe Period.ofDays(2)
                    it[1] shouldBe Period.ofDays(0)
                }

            }
            should("handle binary datasets") { // uses encoder
                val byteArray = "Hello there".encodeToByteArray()
                val dataset = dsOf(byteArray)
                dataset.collectAsList() shouldBe listOf(byteArray)
            }
            should("be able to serialize binary") { // uses knownDataTypes
                val byteArrayTriple = c("Hello there".encodeToByteArray(), 1, intArrayOf(1, 2, 3))
                val dataset = dsOf(byteArrayTriple)

                val (a, b, c) = dataset.collectAsList().single()
                a contentEquals "Hello there".encodeToByteArray() shouldBe true
                b shouldBe 1
                c contentEquals intArrayOf(1, 2, 3) shouldBe true
            }
            should("be able to serialize Decimal") { // uses knownDataTypes
                val decimalPair = c(Decimal().set(50), 12)
                val dataset = dsOf(decimalPair)
                dataset.collectAsList() shouldBe listOf(decimalPair)
            }
            should("handle BigDecimal datasets") { // uses encoder
                val decimals = listOf(BigDecimal.ONE, BigDecimal.TEN)
                val dataset = decimals.toDS()
                dataset.collectAsList().let { (one, ten) ->
                    one.compareTo(BigDecimal.ONE) shouldBe 0
                    ten.compareTo(BigDecimal.TEN) shouldBe 0
                }
            }
            should("be able to serialize BigDecimal") { // uses knownDataTypes
                val decimalPair = c(BigDecimal.TEN, 12)
                val dataset = dsOf(decimalPair)
                val (a, b) = dataset.collectAsList().single()
                a.compareTo(BigDecimal.TEN) shouldBe 0
                b shouldBe 12
            }
            should("be able to serialize CalendarInterval") { // uses knownDataTypes
                val calendarIntervalPair = CalendarInterval(1, 0, 0L) to 2
                val dataset = dsOf(calendarIntervalPair)
                dataset.collectAsList() shouldBe listOf(calendarIntervalPair)
            }
            should("handle nullable datasets") {
                val ints = listOf(1, 2, 3, null)
                val dataset = ints.toDS()
                dataset.collectAsList() shouldBe ints
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
            @Suppress("UNCHECKED_CAST")
            should("support dataset select") {
                val dataset = dsOf(
                    SomeClass(intArrayOf(1, 2, 3), 3),
                    SomeClass(intArrayOf(1, 2, 4), 5),
                )

                val newDS1WithAs: Dataset<IntArray> = dataset.selectTyped(
                    col("a").`as`<IntArray>(),
                )
                newDS1WithAs.collectAsList()

                val newDS2: Dataset<Pair<IntArray, Int>> = dataset.selectTyped(
                    col(SomeClass::a), // NOTE: this only works on 3.0, returning a data class with an array in it
                    col(SomeClass::b),
                )
                newDS2.collectAsList()

                val newDS3: Dataset<Triple<IntArray, Int, Int>> = dataset.selectTyped(
                    col(SomeClass::a),
                    col(SomeClass::b),
                    col(SomeClass::b),
                )
                newDS3.collectAsList()

                val newDS4: Dataset<Arity4<IntArray, Int, Int, Int>> = dataset.selectTyped(
                    col(SomeClass::a),
                    col(SomeClass::b),
                    col(SomeClass::b),
                    col(SomeClass::b),
                )
                newDS4.collectAsList()

                val newDS5: Dataset<Arity5<IntArray, Int, Int, Int, Int>> = dataset.selectTyped(
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
                b.collectAsList()
            }
            should("Handle some where queries using column operator functions") {
                val dataset = dsOf(
                    SomeOtherClass(intArrayOf(1, 2, 3), 4, true),
                    SomeOtherClass(intArrayOf(4, 3, 2), 1, true),
                )
                dataset.collectAsList()

                val column = col("b").`as`<IntArray>()

                val b = dataset.where(column gt 3 and col(SomeOtherClass::c))

                b.count() shouldBe 1
            }
            should("Be able to serialize lists of data classes") {
                val dataset = dsOf(
                    listOf(SomeClass(intArrayOf(1, 2, 3), 4)),
                    listOf(SomeClass(intArrayOf(3, 2, 1), 0)),
                )

                val (first, second) = dataset.collectAsList()

                first.single().let { (a, b) ->
                    a.contentEquals(intArrayOf(1, 2, 3)) shouldBe true
                    b shouldBe 4
                }
                second.single().let { (a, b) ->
                    a.contentEquals(intArrayOf(3, 2, 1)) shouldBe true
                    b shouldBe 0
                }
            }
            should("Be able to serialize arrays of data classes") {
                val dataset = dsOf(
                    arrayOf(SomeClass(intArrayOf(1, 2, 3), 4)),
                    arrayOf(SomeClass(intArrayOf(3, 2, 1), 0)),
                )

                val (first, second) = dataset.collectAsList()

                first.single().let { (a, b) ->
                    a.contentEquals(intArrayOf(1, 2, 3)) shouldBe true
                    b shouldBe 4
                }
                second.single().let { (a, b) ->
                    a.contentEquals(intArrayOf(3, 2, 1)) shouldBe true
                    b shouldBe 0
                }
            }
            should("Be able to serialize lists of tuples") {
                val dataset = dsOf(
                    listOf(Tuple2(intArrayOf(1, 2, 3), 4)),
                    listOf(Tuple2(intArrayOf(3, 2, 1), 0)),
                )

                val (first, second) = dataset.collectAsList()

                first.single().let {
                    it._1().contentEquals(intArrayOf(1, 2, 3)) shouldBe true
                    it._2() shouldBe 4
                }
                second.single().let {
                    it._1().contentEquals(intArrayOf(3, 2, 1)) shouldBe true
                    it._2() shouldBe 0
                }
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
                val dataset: Pair<Int, SomeClass> = dsOf(
                    SomeClass(intArrayOf(1, 2, 3), 1),
                    SomeClass(intArrayOf(4, 3, 2), 1),
                )
                    .groupByKey { it: SomeClass -> it.b }
                    .reduceGroupsK { v1: SomeClass, v2: SomeClass -> v1 }
                    .filter { it: Pair<Int, SomeClass> -> true } // not sure why this does work, but reduce doesn't
                    .reduceK { v1: Pair<Int, SomeClass>, v2: Pair<Int, SomeClass> -> v1 }

                dataset.second.a shouldBe intArrayOf(1, 2, 3)
            }
            should("Generate encoder correctly with complex enum data class") {
                val dataset: Dataset<ComplexEnumDataClass> =
                    dsOf(
                        ComplexEnumDataClass(
                            1,
                            "string",
                            listOf("1", "2"),
                            SomeEnum.A,
                            SomeOtherEnum.C,
                            listOf(SomeEnum.A, SomeEnum.B),
                            listOf(SomeOtherEnum.C, SomeOtherEnum.D),
                            arrayOf(SomeEnum.A, SomeEnum.B),
                            arrayOf(SomeOtherEnum.C, SomeOtherEnum.D),
                            mapOf(SomeEnum.A to SomeOtherEnum.C)
                        )
                    )

                dataset.show(false)
                val first = dataset.takeAsList(1).first()

                first.int shouldBe 1
                first.string shouldBe "string"
                first.strings shouldBe listOf("1", "2")
                first.someEnum shouldBe SomeEnum.A
                first.someOtherEnum shouldBe SomeOtherEnum.C
                first.someEnums shouldBe listOf(SomeEnum.A, SomeEnum.B)
                first.someOtherEnums shouldBe listOf(SomeOtherEnum.C, SomeOtherEnum.D)
                first.someEnumArray shouldBe arrayOf(SomeEnum.A, SomeEnum.B)
                first.someOtherArray shouldBe arrayOf(SomeOtherEnum.C, SomeOtherEnum.D)
                first.enumMap shouldBe mapOf(SomeEnum.A to SomeOtherEnum.C)
            }
            should("work with lists of maps") {
                val result = dsOf(
                    listOf(mapOf("a" to "b", "x" to "y")),
                    listOf(mapOf("a" to "b", "x" to "y")),
                    listOf(mapOf("a" to "b", "x" to "y"))
                )
                    .showDS()
                    .map { it.last() }
                    .map { it["x"] }
                    .filterNotNull()
                    .distinct()
                    .collectAsList()
                expect(result).contains.inOrder.only.value("y")
            }
            should("work with lists of lists") {
                val result = dsOf(
                    listOf(listOf(1, 2, 3)),
                    listOf(listOf(1, 2, 3)),
                    listOf(listOf(1, 2, 3))
                )
                    .map { it.last() }
                    .map { it.first() }
                    .reduceK { a, b -> a + b }
                expect(result).toBe(3)
            }
            should("Generate schema correctly with nullalble list and map") {
                val schema = encoder<NullFieldAbleDataClass>().schema()
                schema.fields().forEach {
                    it.nullable() shouldBe true
                }
            }
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
                    listOf(Tuple2(1, 1.0), Tuple2(2, 2.0), Tuple2(3, 3.0))
                )
                val dataset3: Dataset<Tuple2<Int, Double>> = rdd3.toDS()

                dataset3.toList<Tuple2<Int, Double>>() shouldBe listOf(Tuple2(1, 1.0), Tuple2(2, 2.0), Tuple2(3, 3.0))
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
            should("Convert Arity RDD to Dataset") {
                val rdd5 = sc.parallelize(
                    listOf(c(1.0, 4))
                )
                val dataset5 = rdd5.toDS()

                dataset5.toList<Arity2<Double, Int>>() shouldBe listOf(c(1.0, 4))
            }
            should("Convert List RDD to Dataset") {
                val rdd6 = sc.parallelize(
                    listOf(listOf(1, 2, 3), listOf(4, 5, 6))
                )
                val dataset6 = rdd6.toDS()

                dataset6.toList<List<Int>>() shouldBe listOf(listOf(1, 2, 3), listOf(4, 5, 6))
            }
        }
    }
})


data class DataClassWithTuple<T : Product>(val tuple: T)

data class LonLat(val lon: Double, val lat: Double)

// (data) class must be Serializable to be broadcast
data class SomeClass(val a: IntArray, val b: Int) : Serializable

data class SomeOtherClass(val a: IntArray, val b: Int, val c: Boolean) : Serializable


enum class SomeEnum { A, B }

enum class SomeOtherEnum(val value: Int) { C(1), D(2) }

data class ComplexEnumDataClass(
    val int: Int,
    val string: String,
    val strings: List<String>,
    val someEnum: SomeEnum,
    val someOtherEnum: SomeOtherEnum,
    val someEnums: List<SomeEnum>,
    val someOtherEnums: List<SomeOtherEnum>,
    val someEnumArray: Array<SomeEnum>,
    val someOtherArray: Array<SomeOtherEnum>,
    val enumMap: Map<SomeEnum, SomeOtherEnum>,
)

data class NullFieldAbleDataClass(
    val optionList: List<Int>?,
    val optionMap: Map<String, Int>?,
)