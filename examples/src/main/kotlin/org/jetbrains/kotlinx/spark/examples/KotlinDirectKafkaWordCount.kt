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
package org.jetbrains.kotlinx.spark.examples

import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaDStream
import org.apache.spark.streaming.api.java.JavaInputDStream
import org.apache.spark.streaming.api.java.JavaPairDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies
import org.jetbrains.kotlinx.spark.api.c
import org.jetbrains.kotlinx.spark.api.reduceByKey
import org.jetbrains.kotlinx.spark.api.toTuple
import org.jetbrains.kotlinx.spark.api.tuples.*
import org.jetbrains.kotlinx.spark.api.withSparkStreaming
import scala.Tuple2
import java.io.Serializable
import java.util.regex.Pattern
import kotlin.system.exitProcess


/**
 * Src: https://github.com/apache/spark/blob/master/examples/src/main/java/org/apache/spark/examples/streaming/JavaDirectKafkaWordCount.java
 *
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: JavaDirectKafkaWordCount <brokers> <groupId> <topics>
 * <brokers> is a list of one or more Kafka brokers
 * <groupId> is a consumer group name to consume from topics
 * <topics> is a list of one or more kafka topics to consume from
 *
 * Example:
 *
 * First make sure you have a Kafka producer running. For instance, when running locally:
 * $ kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092
 *
 * Then start the program normally or like this:
 * $ bin/run-example streaming.JavaDirectKafkaWordCount broker1-host:port,broker2-host:port \
 * consumer-group topic1,topic2
 */
object KotlinDirectKafkaWordCount {

    private val SPACE = Pattern.compile(" ")

    private const val DEFAULT_BROKER = "localhost:9092"
    private const val DEFAULT_GROUP_ID = "consumer-group"
    private const val DEFAULT_TOPIC = "quickstart-events"

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size < 3 && args.isNotEmpty()) {
            System.err.println(
                """Usage: JavaDirectKafkaWordCount <brokers> <groupId> <topics>
                  <brokers> is a list of one or more Kafka brokers
                  <groupId> is a consumer group name to consume from topics
                  <topics> is a list of one or more kafka topics to consume from
                """.trimIndent()
            )
            exitProcess(1)
        }

        val brokers: String = args.getOrElse(0) { DEFAULT_BROKER }
        val groupId: String = args.getOrElse(1) { DEFAULT_GROUP_ID }
        val topics: String = args.getOrElse(2) { DEFAULT_TOPIC }

        // Create context with a 2 seconds batch interval
        withSparkStreaming(batchDuration = Durations.seconds(2), appName = "JavaDirectKafkaWordCount") {

            val topicsSet: Set<String> = topics.split(',').toSet()

            val kafkaParams: Map<String, Serializable> = mapOf(
                BOOTSTRAP_SERVERS_CONFIG to brokers,
                GROUP_ID_CONFIG to groupId,
                KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            )

            // Create direct kafka stream with brokers and topics
            val messages: JavaInputDStream<ConsumerRecord<String, String>> = KafkaUtils.createDirectStream(
                ssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topicsSet, kafkaParams),
            )

            // Get the lines, split them into words, count the words and print
            val lines: JavaDStream<String> = messages.map { it.value() }
            val words: JavaDStream<String> = lines.flatMap { it.split(SPACE).iterator() }

            val wordCounts: JavaDStream<Tuple2<String, Int>> = words
                .map { it X 1 }
                .reduceByKey { a: Int, b: Int -> a + b }

            wordCounts.print()

        }
    }
}
