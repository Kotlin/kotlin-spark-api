package org.jetbrains.spark.api/*-
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
import org.jetbrains.spark.api.*
import java.time.LocalDate

class ApiTest : ShouldSpec({
    context("integration tests") {
        withSpark {
            should("collect data classes with doubles correctly") {

                val ll1 = LonLat(1.0, 2.0)
                val ll2 = LonLat(3.0, 4.0)
                val lonlats = dsOf(ll1, ll2).collectAsList()
                expect(lonlats).asExpect().contains.inAnyOrder.only.values(ll1, ll2)

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
                            showDS()

                            filter { it.first % 2 == 0 }.showDS()
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
        }
    }
})

data class LonLat(val lon: Double, val lat: Double)
