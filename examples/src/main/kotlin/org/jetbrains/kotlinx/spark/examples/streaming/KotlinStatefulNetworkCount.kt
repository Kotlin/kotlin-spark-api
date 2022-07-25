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

import org.apache.spark.api.java.Optional
import org.apache.spark.api.java.StorageLevels
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.State
import org.apache.spark.streaming.StateSpec
import org.jetbrains.kotlinx.spark.api.*
import org.jetbrains.kotlinx.spark.api.tuples.X
import java.util.regex.Pattern
import kotlin.system.exitProcess


/**
 * Src: https://github.com/apache/spark/blob/master/examples/src/main/java/org/apache/spark/examples/streaming/JavaStatefulNetworkWordCount.java
 *
 * Counts words cumulatively in UTF8 encoded, '\n' delimited text received from the network every
 * second starting with initial value of word count.
 * Usage: JavaStatefulNetworkWordCount <hostname> <port>
 * <hostname> and <port> describe the TCP server that Spark Streaming would connect to receive
 * data.
 *
 *
 * To run this on your local machine, you need to first run a Netcat server
 * `$ nc -lk 9999`
 * and then run the example
 * `$ bin/run-example
 * org.apache.spark.examples.streaming.JavaStatefulNetworkWordCount localhost 9999` */
object KotlinStatefulNetworkCount {

    private val SPACE = Pattern.compile(" ")

    private const val DEFAULT_HOSTNAME = "localhost"
    private const val DEFAULT_PORT = "9999"

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size < 2 && args.isNotEmpty()) {
            System.err.println("Usage: JavaStatefulNetworkWordCount <hostname> <port>")
            exitProcess(1)
        }

        // Create the context with a 1 second batch size
        withSparkStreaming(
            batchDuration = Durations.seconds(1),
            checkpointPath = ".",
            appName = "JavaStatefulNetworkWordCount",
        ) {

            // Initial state RDD input to mapWithState
            val tuples = arrayOf("hello" X 1, "world" X 1)
            val initialRDD = ssc.sparkContext().rddOf(*tuples)

            val lines = ssc.socketTextStream(
                args.getOrElse(0) { DEFAULT_HOSTNAME },
                args.getOrElse(1) { DEFAULT_PORT }.toInt(),
                StorageLevels.MEMORY_AND_DISK_SER_2,
            )
            val words = lines.flatMap { it.split(SPACE).iterator() }

            val wordsDstream = words.map { it X 1 }

            // Update the cumulative count function
            val mappingFunc = { word: String, one: Optional<Int>, state: State<Int> ->
                val sum = one.getOrElse(0) + state.getOrElse(0)
                val output = word X sum
                state.update(sum)
                output
            }

            // DStream made of get cumulative counts that get updated in every batch
            val stateDstream = wordsDstream.mapWithState(
                StateSpec
                    .function(mappingFunc)
                    .initialState(initialRDD.toJavaPairRDD())
            )

            stateDstream.print()
        }
    }
}
