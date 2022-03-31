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
@file:OptIn(ExperimentalTime::class)

package org.jetbrains.kotlinx.spark.examples

import com.google.common.io.Files
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.Time
import org.apache.spark.util.LongAccumulator
import org.jetbrains.kotlinx.spark.api.*
import scala.Tuple2
import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern
import kotlin.experimental.ExperimentalTypeInference
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


/**
 * Use this singleton to get or register a Broadcast variable.
 */
internal object KotlinWordExcludeList {

    @Volatile
    private var instance: Broadcast<List<String>>? = null

    fun getInstance(sc: JavaSparkContext): Broadcast<List<String>> {
        if (instance == null) synchronized(KotlinWordExcludeList::class) {
            if (instance == null) {
                val wordExcludeList = listOf("a", "b", "c")
                instance = sc.broadcast(wordExcludeList)
            }
        }
        return instance!!
    }
}

/**
 * Use this singleton to get or register an Accumulator.
 */
internal object KotlinDroppedWordsCounter {

    @Volatile
    private var instance: LongAccumulator? = null

    fun getInstance(sc: JavaSparkContext): LongAccumulator {
        if (instance == null) synchronized(KotlinDroppedWordsCounter::class) {
            if (instance == null)
                instance = sc.sc().longAccumulator("DroppedWordsCounter")
        }
        return instance!!
    }
}

/**
 * Counts words in text encoded with UTF8 received from the network every second. This example also
 * shows how to use lazily instantiated singleton instances for Accumulator and Broadcast so that
 * they can be registered on driver failures.
 *
 * Usage: KotlinRecoverableNetworkWordCount <hostname> <port> <checkpoint-directory> <output-file>
 * <hostname> and <port> describe the TCP server that Spark Streaming would connect to receive
 * data. <checkpoint-directory> directory to HDFS-compatible file system which checkpoint data
 * <output-file> file to which the word counts will be appended
 *
 * <checkpoint-directory> and <output-file> must be absolute paths
 *
 * To run this on your local machine, you need to first run a Netcat server
 *
 * `$ nc -lk 9999`
 *
 * and run the example as
 *
 * `$ ./bin/run-example org.apache.spark.examples.streaming.KotlinRecoverableNetworkWordCount \
 * localhost 9999 ~/checkpoint/ ~/out`
 *
 * If the directory ~/checkpoint/ does not exist (e.g. running for the first time), it will create
 * a new StreamingContext (will print "Creating new context" to the console). Otherwise, if
 * checkpoint data exists in ~/checkpoint/, then it will create StreamingContext from
 * the checkpoint data.
 *
 * Refer to the online documentation for more details.
 */
object KotlinRecoverableNetworkWordCount {

    private val SPACE = Pattern.compile(" ")

    private const val DEFAULT_IP = "localhost"
    private const val DEFAULT_PORT = "9999"
    private const val DEFAULT_CHECKPOINT_DIRECTORY = "~/checkpoint/"
    private const val DEFAULT_OUTPUT_PATH = "~/out"

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 4 && args.isNotEmpty()) {
            System.err.println("You arguments were " + listOf(*args))
            System.err.println(
                """Usage: KotlinRecoverableNetworkWordCount <hostname> <port> <checkpoint-directory>
                     <output-file>. <hostname> and <port> describe the TCP server that Spark
                     Streaming would connect to receive data. <checkpoint-directory> directory to
                     HDFS-compatible file system which checkpoint data <output-file> file to which
                     the word counts will be appended
                
                In local mode, <master> should be 'local[n]' with n > 1
                Both <checkpoint-directory> and <output-file> must be absolute paths""".trimIndent()
            )
            exitProcess(1)
        }
        val ip = args.getOrElse(0) { DEFAULT_IP }
        val port = args.getOrElse(1) { DEFAULT_PORT }.toInt()
        val checkpointDirectory = args.getOrElse(2) { DEFAULT_CHECKPOINT_DIRECTORY }
        val outputPath = args.getOrElse(3) { DEFAULT_OUTPUT_PATH }


        // (used to detect the new context)
        // Create the context with a 1 second batch size or load from checkpointDirectory
        withSparkStreaming(
            checkpointPath = checkpointDirectory,
            batchDuration = Durations.seconds(1),
            appName = "KotlinRecoverableNetworkWordCount",
        ) {
            createContext(
                ip = ip,
                port = port,
                outputPath = outputPath,
            )
        }
    }


    @OptIn(ExperimentalTypeInference::class)
    @Suppress("UnstableApiUsage")
    private fun KSparkStreamingSession.createContext(
        ip: String,
        port: Int,
        outputPath: String,
    ) {
        // If you do not see this printed, that means the StreamingContext has been loaded
        // from the new checkpoint
        println("Creating new context")
        val outputFile = File(outputPath).apply {
            if (exists()) delete()
            parentFile.mkdirs()
            createNewFile()
        }


        // Create a socket stream on target ip:port and count the
        // words in input stream of \n delimited text (e.g. generated by 'nc')
        val lines = ssc.socketTextStream(ip, port)

        val words = lines.flatMap { it.split(SPACE).iterator() }

        val wordCounts3 = words
            .map { Tuple2(it, 1) }
            .reduceByKey { a, b -> a + b }

        // in normal streaming context we can create a SparkSession from ssc: JavaStreamingContext
        // normally `ssc.sparkContext().conf`
        withSpark(ssc) {
            listOf(1, 2, 3).toDS().show()
        }

        wordCounts3.foreachRDD { rdd, time: Time ->
            // but in foreachRDD we must obtain this conf from the RDD
            // like `rdd.context().conf`
            withSpark(rdd) {

                rdd.toDS().show()

                // Get or register the excludeList Broadcast
                val excludeList = KotlinWordExcludeList.getInstance(sc)

                // Get or register the droppedWordsCounter Accumulator
                val droppedWordsCounter = KotlinDroppedWordsCounter.getInstance(sc)

                // Use excludeList to drop words and use droppedWordsCounter to count them
                val (counts, duration) = measureTimedValue {
                    rdd.filter { wordCount ->
                        if (excludeList.value().contains(wordCount._1)) {
                            droppedWordsCounter.add(wordCount._2.toLong())
                            false
                        } else {
                            true
                        }
                    }.collect()
                }


                val output = "Counts at time $time $counts\n$duration"
                println(output)
                println("Dropped ${droppedWordsCounter.value()} word(s) totally")
                println("Appending to " + outputFile.absolutePath)
                Files.append(
                    """
                    $output
                    
                    """.trimIndent(),
                    outputFile,
                    Charset.defaultCharset(),
                )
            }
        }
    }


}
