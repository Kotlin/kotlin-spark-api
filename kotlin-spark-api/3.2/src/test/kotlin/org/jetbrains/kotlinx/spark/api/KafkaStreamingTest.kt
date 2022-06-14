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

import io.kotest.core.Tag
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerExtension
import io.kotest.extensions.testcontainers.kafka.createStringStringConsumer
import io.kotest.extensions.testcontainers.kafka.createStringStringProducer
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
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
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import scala.Tuple3
import java.io.Serializable
import java.time.Duration

object Kafka : Tag()

class KafkaStreamingTest : FunSpec() {
    init {

        tags(Kafka)

        val kafka = install(
            TestContainerExtension(KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.1")))
        ) {
            withEmbeddedZookeeper()
            withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
        }
        println(kafka.bootstrapServers)
        test("Streaming should support kafka") {
            val topic1 = "test1"
            val topic2 = "test2"

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
            val data = arrayListOf<List<Tuple3<String, String, Int>>>()

            withSparkStreaming(
                batchDuration = Durations.milliseconds(1000),
                appName = "KotlinDirectKafkaWordCount",
                timeout = 10_000L,
                master = "local"
            ) {

                setRunAfterStart {
                    val producer = autoClose(kafka.createStringStringProducer())
                    producer.send(ProducerRecord(topic1, "Hello this is a test test test"))
                    producer.send(ProducerRecord(topic2, "This is also also a test test something"))
                }

                val kafkaParams: Map<String, Serializable> = mapOf(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "${kafka.host}:${kafka.getMappedPort(KafkaContainer.KAFKA_PORT)}",
                    ConsumerConfig.GROUP_ID_CONFIG to "consumer-group",
                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                )
                // Create direct kafka stream with brokers and topics
                val messages: JavaInputDStream<ConsumerRecord<String, String>> = KafkaUtils.createDirectStream(
                    ssc,
                    LocationStrategies.PreferBrokers(),
                    ConsumerStrategies.Subscribe(setOf(topic1, topic2), kafkaParams),
                )

                // Get the lines, split them into words, count the words and print

                val wordCounts = messages
                    .map { it.topic() X it.value() }
                    .flatMapValues { it.split(" ").iterator() }
                    .map { t(it, 1) }
                    .reduceByKey { a: Int, b: Int -> a + b }
                    .map { (tup, counter) -> tup + counter }


                wordCounts.foreachRDD { rdd, _ ->
                    data.add(rdd.collect())
                }
            }

            val resultList = resultLists.flatMap { (topic, tuples) ->
                tuples.map { it.prependedBy(topic) }
            }
            data.flatten() shouldContainAll resultList
        }
    }
}