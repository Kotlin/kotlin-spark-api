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
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaInputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies
import org.jetbrains.kotlinx.spark.api.tuples.*
import java.io.Serializable

class KafkaStreamingTest : ShouldSpec({
    context("kafka") {

//            val kafka = install(
//                TestContainerExtension(KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1")))
//            ) {
//                withEmbeddedZookeeper()
//            }
//
//            should("support kafka streams") {
//                val topic1 = "test1"
//                val topic2 = "test2"
//
//                val producer = kafka.createStringStringProducer()
//                producer.send(ProducerRecord(topic1, "Hello this is a test test test"))
//                producer.send(ProducerRecord(topic1, "Hello this is a test test test"))
//                producer.send(ProducerRecord(topic1, "Hello this is a test test test"))
//                producer.send(ProducerRecord(topic1, "Hello this is a test test test"))
//                producer.send(ProducerRecord(topic2, "This is also also a test test something"))
//                producer.send(ProducerRecord(topic2, "This is also also a test test something"))
//                producer.send(ProducerRecord(topic2, "This is also also a test test something"))
//                producer.send(ProducerRecord(topic2, "This is also also a test test something"))
//                producer.close()
//
//                val consumer = kafka.createStringStringConsumer {
//                    this[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 1
//                }
//
//                consumer.subscribe(listOf(topic1))
//                val records = consumer.poll(Duration.ofSeconds(100))
//                records.shouldHaveSize(4)
//                records.print()

        val port = 9092
        val broker = "localhost:$port"
        val topic1 = "test1"
        val topic2 = "test2"
        val kafkaListener = EmbeddedKafkaListener(port)
        listener(kafkaListener)

        should("support kafka streams") {
            val producer = kafkaListener.stringStringProducer()
            producer.send(ProducerRecord(topic1, "Hello this is a test test test"))
            producer.send(ProducerRecord(topic2, "This is also also a test test something"))
            producer.close()

            withSparkStreaming(
                batchDuration = Durations.seconds(2),
                appName = "KotlinDirectKafkaWordCount",
                timeout = 1000L,
            ) {

                val kafkaParams: Map<String, Serializable> = mapOf(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to broker,
                    ConsumerConfig.GROUP_ID_CONFIG to "consumer-group",
                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                )

                // Create direct kafka stream with brokers and topics
                val messages: JavaInputDStream<ConsumerRecord<String, String>> = KafkaUtils.createDirectStream(
                    ssc,
                    LocationStrategies.PreferConsistent(),
                    ConsumerStrategies.Subscribe(setOf(topic1, topic2), kafkaParams),
                )

                // Get the lines, split them into words, count the words and print
                val lines = messages.map { it.topic() X it.value() }
                val words = lines.flatMapValues { it.split(" ").iterator() }

                val wordCounts = words
                    .map { t(it, 1) }
                    .reduceByKey { a: Int, b: Int -> a + b }
                    .map { (tup, counter) -> tup + counter }

                val resultLists = mapOf(
                    topic1 to listOf(
                        "Hello" X 1,
                        "this" X 1,
                        "is" X 1,
                        "a" X 1,
                        "test" X 3,
                    ),
                    topic2 to listOf(
                        "This" X 1,
                        "is" X 1,
                        "also" X 2,
                        "a" X 1,
                        "test" X 2,
                        "something" X 1,
                    )
                )

                wordCounts.foreachRDD { rdd, _ ->
                    rdd.foreach { (topic, word, count) ->
                        t(word, count).shouldBeIn(collection = resultLists[topic]!!)
                    }
                }

                wordCounts.print()
            }
        }

    }
})
