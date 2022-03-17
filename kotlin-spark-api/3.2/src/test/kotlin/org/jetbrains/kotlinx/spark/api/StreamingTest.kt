/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 3.2+ (Scala 2.12)
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
package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import org.apache.spark.streaming.Duration
import java.io.Serializable
import org.jetbrains.kotlinx.spark.api.*
import java.util.LinkedList


class StreamingTest : ShouldSpec({
    context("streaming") {
        should("stream") {

            val input = listOf("aaa", "bbb", "aaa", "ccc")

            val results = object : Serializable {
                @Volatile
                var counter = 0
            }

            withSparkStreaming(Duration(10), timeout = 1000) {
                val resultsBroadcast = spark.broadcast(results)

                val rdd = sc.parallelize(input)
                val queue = LinkedList(listOf(rdd))

                val inputStream = ssc.queueStream(queue)

                inputStream.foreachRDD { rdd, _ ->
                    rdd.toDS().forEach {
                        it shouldBeIn input
                        resultsBroadcast.value.counter++
                    }
                }
            }
            results.counter shouldBe input.size

        }
    }
})
