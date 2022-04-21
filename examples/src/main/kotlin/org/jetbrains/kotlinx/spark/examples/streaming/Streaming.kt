/*-
 * =LICENSE=
 * Kotlin Spark API: Examples for Spark 3.2+ (Scala 2.12)
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
package org.jetbrains.kotlinx.spark.examples.streaming

import org.apache.spark.api.java.JavaRDD
import org.apache.spark.sql.Dataset
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.Time
import org.apache.spark.streaming.api.java.JavaDStream
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream
import org.jetbrains.kotlinx.spark.api.*

data class TestRow(
    val word: String,
)

/**
 * To run this on your local machine, you need to first run a Netcat server
 *
 * `$ nc -lk 9999`
 */
fun main() = withSparkStreaming(batchDuration = Durations.seconds(1), timeout = 10_000) { // this: KSparkStreamingSession

    val lines: JavaReceiverInputDStream<String> = ssc.socketTextStream("localhost", 9999)
    val words: JavaDStream<String> = lines.flatMap { it.split(" ").iterator() }

    words.foreachRDD { rdd: JavaRDD<String>, _: Time ->
        withSpark(rdd) { // this: KSparkSession
            val dataframe: Dataset<TestRow> = rdd.map { TestRow(it) }.toDS()
            dataframe
                .groupByKey { it.word }
                .count()
                .show()
        }
    }
}