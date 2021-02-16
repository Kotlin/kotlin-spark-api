/*-
 * =LICENSE=
 * Kotlin Spark API: Examples for Spark 2.4+ (Scala 2.11)
 * ----------
 * Copyright (C) 2019 - 2021 JetBrains
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

import org.jetbrains.kotlinx.spark.api.broadcast
import org.jetbrains.kotlinx.spark.api.map
import org.jetbrains.kotlinx.spark.api.sparkContext
import org.jetbrains.kotlinx.spark.api.withSpark
import java.io.Serializable

// (data) class must be Serializable to be broadcast
data class SomeClass(val a: IntArray, val b: Int) : Serializable

fun main() = withSpark {
    val broadcastVariable = spark.sparkContext.broadcast(SomeClass(a = intArrayOf(5, 6), b = 3))
    val result = listOf(1, 2, 3, 4, 5)
            .toDS()
            .map {
                val receivedBroadcast = broadcastVariable.value
                it + receivedBroadcast.a.first()
            }
            .collectAsList()

    println(result)
}
