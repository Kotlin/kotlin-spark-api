/*-
 * =LICENSE=
 * Kotlin Spark API: Scala Tuples in Kotlin
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
package org.jetbrains.kotlinx.spark.api.tuples

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlinx.spark.api.tuples.*
import org.jetbrains.kotlinx.spark.api.*
import scala.Tuple3
import io.kotest.matchers.types.shouldBeInstanceOf
import scala.Tuple1
import scala.Tuple2

@Suppress("ShouldBeInstanceOfInspection", "RedundantLambdaArrow", "USELESS_IS_CHECK")
class TuplesTest : ShouldSpec({
    context("Test tuple extensions") {

        should("Support different ways to create tuples") {
            listOf(
                1 X 2L X "3",
                (1 X 2L) + "3",
                1 + (2L X "3"),
                1 + t() + 2L + "3",
                t() + 1 + 2L + "3",
                tupleOf() + 1 + 2L + "3",
                EmptyTuple + 1 + 2L + "3",
                emptyTuple() + 1 + 2L + "3",
                t(1, 2L, "3"),
                tupleOf(1, 2L, "3"),
                t(1, 2L) + "3",
                t(1, 2L) + t("3"),
                t(1) + t(2L, "3"),
                t(1) + t(2L) + t("3"),
                t() + t(1, 2L, "3"),
                t(1) + t() + t(2L, "3"),
                t(1, 2L) + t() + t("3"),
                t(1, 2L, "3") + t(),
                1 + t(2L) + "3",
                1 + t(2L, "3"),
                Triple(1, 2L, "3").toTuple(),
                (1 to 2L).toTuple().appendedBy("3"),
                (2L to "3").toTuple().prependedBy(1),
            ).forEach {
                it shouldBe Tuple3(1, 2L, "3")
            }
        }

        should("Merge tuples with +, append/prepend other values") {
            t() + 1 shouldBe t(1)
            t(1) + 2L shouldBe t(1, 2L)
            t(1, 2L) + "3" shouldBe t(1, 2L, "3")
            1 + t() shouldBe t(1)
            2L + t(1) shouldBe t(2L, 1)
            2L + t(1, "3") shouldBe t(2L, 1, "3")

            // NOTE! String.plus is a thing
            "3" + t(1, 2L) shouldNotBe t("3", 1, 2L)

            t() + t(1) shouldBe t(1)
            t(1) + t(2L) shouldBe t(1, 2L)
            t(1, 2L) + t("3") shouldBe t(1, 2L, "3")
            t(1) + t(2L, "3") shouldBe t(1, 2L, "3")

            t() concat t(1) shouldBe t(1)
            t(1) concat t(2L) shouldBe t(1, 2L)
            t(1, 2L) concat t("3") shouldBe t(1, 2L, "3")
            t(1) concat t(2L, "3") shouldBe t(1, 2L, "3")

            // tuple inside other tuple
            t().appendedBy(t(1)) shouldBe t(t(1))
            t() + t(t(1)) shouldBe t(t(1))
            t().prependedBy(t(1)) shouldBe t(t(1))
            t(t(1)) + t() shouldBe t(t(1))

            t(1).appendedBy(t(2L)) shouldBe t(1, t(2L))
            t(1) + t(t(2L)) shouldBe t(1, t(2L))
            t(1).prependedBy(t(2L)) shouldBe t(t(2L), 1)
            t(t(2L)) + t(1) shouldBe t(t(2L), 1)
        }

        should("Have drop functions") {
            t(1, 2L).dropLast() shouldBe t(1)
            t(1, 2L).dropFirst() shouldBe t(2L)
            t(1, 2L, "3").dropLast() shouldBe t(1, 2L)
            t(1, 2L, "3").dropFirst() shouldBe t(2L, "3")

            t(1).dropLast() shouldBe emptyTuple()
            t(1).dropFirst() shouldBe emptyTuple()
        }

        should("Have Tuple destructuring") {
            val (a: Int, b: Double, c: Long, d: String, e: Char, f: Float, g: Short, h: Byte, i: UInt, j: UByte, k: UShort, l: ULong) =
                1 X 2.0 X 3L X "4" X '5' X 6F X 7.toShort() X 8.toByte() X 9.toUInt() X 10.toUByte() X 11.toUShort() X 12.toULong() // etc...
            a shouldBe 1
            b shouldBe 2.0
            c shouldBe 3L
            d shouldBe "4"
            e shouldBe '5'
            f shouldBe 6F
            g shouldBe 7.toShort()
            h shouldBe 8.toByte()
            i shouldBe 9.toUInt()
            j shouldBe 10.toUByte()
            k shouldBe 11.toUShort()
            l shouldBe 12.toULong()
        }

        should("Have other helpful extensions") {
            (0 !in tupleOf()) shouldBe true
            (1 in tupleOf(1, 2, 3)) shouldBe true
            (0 !in tupleOf(1, 2, 3)) shouldBe true
            tupleOf(1, 2, 3).iterator().asSequence().toSet() shouldBe setOf(1, 2, 3)
            for (it in tupleOf(1, 1, 1)) {
                it shouldBe 1
            }
            tupleOf(1, 2, 3).toList().isNotEmpty() shouldBe true
            tupleOf(1, 2, 3).asIterable().none { it > 4 } shouldBe true
            tupleOf(1, 2, 3, 4, 5).size shouldBe 5
            tupleOf(1, 2, 3, 4)[0] shouldBe 1
            shouldThrow<IndexOutOfBoundsException> { tupleOf(1, 2L)[5] }

            tupleOf(1 to 3, arrayOf(1), A()).getOrNull(5).let {
                (it is Any?) shouldBe true
                it shouldBe null
            }

            tupleOf(1, 2, 3).getOrNull(5).let {
                (it is Int?) shouldBe true
                it shouldBe null
            }

            shouldThrow<IndexOutOfBoundsException> { tupleOf(1).getAs<String>(5) }
            shouldThrow<ClassCastException> { tupleOf(1).getAs<String>(0) }

            tupleOf(1).getAsOrNull<String>(5) shouldBe null
            tupleOf(1).getAsOrNull<String>(0) shouldBe null


            tupleOf(1, 2, 3).toTriple() shouldBe Triple(1, 2, 3)

            tupleOf(1, 2, 3, 4, 5, 6, 7)[1..3].let {
                it.shouldBeInstanceOf<List<Int>>()
                it.containsAll(listOf(2, 3, 4)) shouldBe true
            }
            tupleOf(1, 1, 2)[1..2] shouldBe tupleOf(1, 2, 2)[0..1]

            tupleOf(1, 2, 3, 4, 5)[2] shouldBe 3

            shouldThrow<IndexOutOfBoundsException> { tupleOf(1, 1, 2)[1..5] }
            (null in tupleOf(1, 1, 2).getOrNull(1..5)) shouldBe true

            tupleOf(1, 2) shouldBe tupleOf(2, 1).swap()
            tupleOf(1 to "Test") shouldBe tupleOf(1 to "Test")
            val a: List<Super> = tupleOf(A(), B()).toList()
        }

        should("Have copy methods for tuples, Kotlin data class style") {
            t().copy() shouldBe t()

            t(1, 2).copy(_1 = 0) shouldBe t(0, 2)
            t(1, 2).copy(_2 = 0) shouldBe t(1, 0)

            t(1, 2).copy() shouldBe t(1, 2)
            t(1, 2, 3, 4, 5).copy() shouldBe t(1, 2, 3, 4, 5)

            // when specifying all parameters, the Scala version will be used
            t(1, 2).copy(3, 4) shouldBe t(3, 4)
            // unless explicitly giving parameters
            t(1, 2).copy(_1 = 3, _2 = 4) shouldBe t(3, 4)
        }

        should("Zip tuples") {

            (t(1, 2) zip t(3, 4)) shouldBe t(t(1, 3), t(2, 4))
            (t(1, 2, 3, 4, 5, 6) zip t("a", "b")) shouldBe t(t(1, "a"), t(2, "b"))

            (t(1, 2, 3, 4) zip t()) shouldBe t()
            (t() zip t(1, 2, 3, 4)) shouldBe t()

            val a: Tuple2<Tuple2<String, Int>, Tuple2<Double, Long>> =  t("1", 2.0) zip t(3, 4L, "", "")
            val b: Tuple3<Tuple2<String, Int>, Tuple2<Double, Long>, Tuple2<Float, String>> = t("1", 2.0, 5f) zip t(3, 4L, "", "")

            val c: Tuple2<Tuple2<Tuple2<Int, String>, List<Int?>>, Tuple2<Tuple2<Map<Int, String>, Long>, Int>> =
                t(1, mapOf(1 to "a")) zip t("13", 1L) zip t(listOf(null, 1), 1, 'c')
        }

        should("Map tuples") {
            t(1, 2.toShort(), 3L, 4.0, 5)
                .map { it.toString() }
                .shouldBe(
                    t("1", "2", "3", "4.0", "5")
                )

            shouldThrow<ClassCastException> {
                t(1, "2", 3L).cast<String, Int, Double>()
            }
        }

        should("Take n from tuples") {
            t(1, 2, 3).take2() shouldBe t(1, 2)
            t(1, 2, 3).takeLast2() shouldBe t(2, 3)

            t(1, 2, 3).take0() shouldBe t()
            t(1, 2, 3).takeLast0() shouldBe t()
        }

        should("Drop n from tuples") {
            t(1, 2, 3).drop2() shouldBe t(3)
            t(1, 2, 3).dropLast2() shouldBe t(1)

            t(1, 2, 3).drop0() shouldBe t(1, 2, 3)
            t(1, 2, 3).dropLast0() shouldBe t(1, 2, 3)
        }

        should("Split tuples") {
            t(1, 2, 3, 4, 5).splitAt2() shouldBe t(t(1, 2), t(3, 4, 5))
            t(1, 2, 3, 4, 5).splitAt0() shouldBe t(t(), t(1, 2, 3, 4, 5))
            t(1, 2, 3, 4, 5).splitAt5() shouldBe t(t(1, 2, 3, 4, 5), t())
        }

    }
})

interface Super

class A : Super
class B : Super
