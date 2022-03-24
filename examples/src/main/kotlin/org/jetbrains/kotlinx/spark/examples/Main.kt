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
package org.jetbrains.kotlinx.spark.examples

import org.apache.spark.api.java.function.ReduceFunction
import org.apache.spark.sql.Dataset
import org.jetbrains.kotlinx.spark.api.*
import org.jetbrains.kotlinx.spark.api.tuples.*
import scala.*

data class Q<T>(val id: Int, val text: T)
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val spark = SparkSession
            .builder()
            .master("local[2]")
            .appName("Simple Application")
            .getOrCreate()

        val triples: Dataset<Tuple3<Int, Int?, Int>> = spark
            .toDS(
                listOf(
                    Q(1, t(1,  null)),
                    Q(2, t(2,  "22")),
                    Q(3, t(3,  "333")),
                )
            )
            .map { (a, b) -> t(a + b._1, b._2?.length) }
            .map { it: Tuple2<Int, Int?> -> it + 1 } // add counter

        val pairs = spark
            .toDS(listOf(
                t + 2 + "hell",
                t + 4 + "moon",
                t + 6 + "berry",
            ))

        triples
            .leftJoin(pairs, triples.col("first").multiply(2) eq pairs.col("first"))
//                .also { it.printSchema() }
            .map { (triple, pair) -> Five(triple._1, triple._2, triple._3, pair?._1, pair?._2) }
            .groupByKey { it.a }
            .reduceGroupsK { v1, v2 -> v1.copy(a = v1.a + v2.a, b = v1.a + v2.a) }
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

