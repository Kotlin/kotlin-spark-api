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
@file:Suppress("MemberVisibilityCanBePrivate", "BlockingMethodInNonBlockingContext")

package org.jetbrains.kotlinx.spark.api

/**
 * Source: https://github.com/kotest/kotest-extensions-embedded-kafka
 *
 */

import io.github.embeddedkafka.EmbeddedKafka
import io.github.embeddedkafka.EmbeddedKafkaConfig
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.BytesDeserializer
import org.apache.kafka.common.serialization.BytesSerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.utils.Bytes
import scala.Predef
import java.util.Properties

val embeddedKafkaListener: EmbeddedKafkaListener = EmbeddedKafkaListener(EmbeddedKafkaConfig.defaultConfig())

class EmbeddedKafkaListener(
    private val config: EmbeddedKafkaConfig,
) : TestListener {

    constructor(port: Int) : this(
        EmbeddedKafkaConfig.apply(
            port,
            EmbeddedKafkaConfig.defaultZookeeperPort(),
            Predef.Map().empty(),
            Predef.Map().empty(),
            Predef.Map().empty(),
        )
    )

    constructor(kafkaPort: Int, zookeeperPort: Int) : this(
        EmbeddedKafkaConfig.apply(
            kafkaPort,
            zookeeperPort,
            Predef.Map().empty(),
            Predef.Map().empty(),
            Predef.Map().empty(),
        )
    )

    val port: Int = config.kafkaPort()

//    val host: String = "127.0.0.1"
    val host: String = "0.0.0.0"

    val bootstrapServer = "$host:$port"

    override suspend fun beforeSpec(spec: Spec) {
        EmbeddedKafka.start(config)
        while (!EmbeddedKafka.isRunning()) {
            Thread.sleep(100)
        }
    }

    override suspend fun afterSpec(spec: Spec) {
        EmbeddedKafka.stop()
        while (EmbeddedKafka.isRunning()) {
            Thread.sleep(100)
        }
    }

    /**
     * Returns a kafka consumer configured with the details of the embedded broker.
     */
    fun stringStringConsumer(configure: Properties.() -> Unit = {}): KafkaConsumer<String, String> {
        val props = Properties()
        props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
        props[ConsumerConfig.GROUP_ID_CONFIG] = "test_consumer_group_" + System.currentTimeMillis()
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        props.configure()
        return KafkaConsumer(props, StringDeserializer(), StringDeserializer())
    }

    /**
     * Returns a kafka consumer subscribed to the given topic on the embedded broker.
     */
    fun stringStringConsumer(topic: String, configure: Properties.() -> Unit = {}): KafkaConsumer<String, String> {
        val consumer = stringStringConsumer(configure)
        consumer.subscribe(listOf(topic))
        return consumer
    }

    /**
     * Returns a kafka consumer configured with the details of the embedded broker.
     */
    fun bytesBytesConsumer(configure: Properties.() -> Unit = {}): KafkaConsumer<Bytes, Bytes> {
        val props = Properties()
        props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
        props[ConsumerConfig.GROUP_ID_CONFIG] = "test_consumer_group_" + System.currentTimeMillis()
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        props.configure()
        return KafkaConsumer(props, BytesDeserializer(), BytesDeserializer())
    }

    /**
     * Returns a kafka consumer subscribed to the given topic on the embedded broker.
     */
    fun bytesBytesConsumer(topic: String, configure: Properties.() -> Unit = {}): KafkaConsumer<Bytes, Bytes> {
        val consumer = bytesBytesConsumer(configure)
        consumer.subscribe(listOf(topic))
        return consumer
    }

    fun bytesBytesProducer(configure: Properties.() -> Unit = {}): KafkaProducer<Bytes, Bytes> {
        val props = Properties()
        props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
        props.configure()
        return KafkaProducer(props, BytesSerializer(), BytesSerializer())
    }

    fun stringStringProducer(configure: Properties.() -> Unit = {}): KafkaProducer<String, String> {
        val props = Properties()
        props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
        props.configure()
        return KafkaProducer(props, StringSerializer(), StringSerializer())
    }
}

