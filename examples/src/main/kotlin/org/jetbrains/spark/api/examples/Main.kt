/*-
 * =LICENSE=
 * Kotlin Spark API: Examples
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
package org.jetbrains.spark.api.examples

import org.apache.spark.api.java.function.ReduceFunction
import org.jetbrains.spark.api.*

data class Q<T>(val id: Int, val text: T)
object Main {

    @JvmStatic
    fun main(args: Array<String>) {

        val spark = SparkSession
                .builder()
                .master("local[2]")
                .appName("Simple Application").orCreate

        val triples = spark
                .toDS(listOf(Q(1, 1 to null), Q(2, 2 to "22"), Q(3, 3 to "333")))
                .map { (a, b) -> a + b.first to b.second?.length }
                .map { it to 1 }
                .map { (a, b) -> Triple(a.first, a.second, b) }


        val pairs = spark
                .toDS(listOf(2 to "hell", 4 to "moon", 6 to "berry"))

        triples
                .leftJoin(pairs, triples.col("first").multiply(2).eq(pairs.col("first")))
//                .also { it.printSchema() }
                .map { (triple, pair) -> Five(triple.first, triple.second, triple.third, pair?.first, pair?.second) }
                .groupByKey { it.a }
                .reduceGroups(ReduceFunction { v1, v2 -> v1.copy(a = v1.a + v2.a, b = v1.a + v2.a) })
                .map { it._2 }
                .repartition(1)
                .withCached {
                    write()
                            .also { it.csv("csvpath") }
                            .also { it.orc("orcpath") }
                    showDS()
                }



        spark.stop()
    }

    data class Five<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
}

