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
import java.time.LocalDate

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
        }
    }
})

data class LonLat(val lon: Double, val lat: Double)
