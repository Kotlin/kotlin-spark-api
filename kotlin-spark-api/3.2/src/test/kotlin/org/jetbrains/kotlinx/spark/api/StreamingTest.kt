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

import io.kotest.assertions.print.print
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.timing.eventually
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.testcontainers.TestContainerExtension
import io.kotest.extensions.testcontainers.kafka.createStringStringConsumer
import io.kotest.extensions.testcontainers.kafka.createStringStringProducer
import io.kotest.extensions.testcontainers.perTest
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.apache.commons.io.FileUtils
import org.apache.hadoop.fs.FileSystem
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkException
import org.apache.spark.streaming.*
import org.apache.spark.streaming.api.java.JavaDStream
import org.apache.spark.streaming.api.java.JavaInputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies
import org.apache.spark.util.Utils
import org.jetbrains.kotlinx.spark.api.tuples.*
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import scala.Tuple2
import java.io.File
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds
import java.time.Duration


class StreamingTest : ShouldSpec({

    context("streaming") {

        should("stream") {

            val input = listOf("aaa", "bbb", "aaa", "ccc")
            val counter = Counter(0)

            withSparkStreaming(Duration(10), timeout = 1000) {

                val (counterBroadcast, queue) = withSpark(ssc) {
                    spark.broadcast(counter) X LinkedList(listOf(sc.parallelize(input)))
                }

                val inputStream = ssc.queueStream(queue)

                inputStream.foreachRDD { rdd, _ ->
                    withSpark(rdd) {
                        rdd.toDS().forEach {
                            it shouldBeIn input
                            counterBroadcast.value.value++
                        }
                    }
                }
            }

            counter.value shouldBe input.size

        }

        should("Work with checkpointpath") {
            val emptyDir = createTempDir()
            val testDirectory = createTempDir()
            val corruptedCheckpointDir = createCorruptedCheckpoint()

            val batchDuration = Durations.seconds(1)
            val timeout = Durations.seconds(1).milliseconds()


            val newContextCreated = AtomicBoolean(false)

            val creatingFun: KSparkStreamingSession.() -> Unit = {
                println("created new context")
                newContextCreated.set(true)

                // closing statement
                ssc.textFileStream(testDirectory.absolutePath).foreachRDD { rdd, _ -> rdd.count() }
            }

            // fill emptyDir with checkpoint
            newContextCreated.set(false)
            withSparkStreaming(
                batchDuration = batchDuration,
                checkpointPath = emptyDir.absolutePath,
                props = mapOf("newContext" to true),
                timeout = timeout,
                func = creatingFun,
            )
            newContextCreated.get() shouldBe true

            // check that creatingFun isn't executed when checkpoint is present
            newContextCreated.set(false)
            withSparkStreaming(
                batchDuration = batchDuration,
                checkpointPath = emptyDir.absolutePath,
                props = mapOf("newContext" to true),
                timeout = timeout,
                func = creatingFun,
            )
            newContextCreated.get() shouldBe false

            // check that creatingFun is not executed when createOnError = false using corrupted checkpoint
            newContextCreated.set(false)
            shouldThrow<SparkException> {
                withSparkStreaming(
                    batchDuration = batchDuration,
                    checkpointPath = corruptedCheckpointDir,
                    props = mapOf("newContext" to true),
                    timeout = timeout,
                    func = creatingFun,
                    createOnError = false,
                )
            }
            newContextCreated.get() shouldBe false

            // check that creatingFun is executed when createOnError = true using corrupted checkpoint
            newContextCreated.set(false)
            withSparkStreaming(
                batchDuration = batchDuration,
                checkpointPath = corruptedCheckpointDir,
                props = mapOf("newContext" to true),
                timeout = timeout,
                func = creatingFun,
                createOnError = true,
            )
            newContextCreated.get() shouldBe true
        }

        should("Have handy tuple2 functions") {
            val input = listOf("aaa", "bbb", "aaa", "ccc")
            val result = Result()

            withSparkStreaming(Duration(10), timeout = 1000, checkpointPath = createTempDir().absolutePath) {

                val (resultBroadcast, queue) = withSpark(ssc) {
                    spark.broadcast(result) X LinkedList(listOf(sc.parallelize(input)))
                }

                val inputStream = ssc

                    .queueStream(queue) // "aaa", "bbb", "aaa", "ccc"

                    .map { it X 1 } // ("aaa", 1), ("bbb", 1), ("aaa", 1), ("ccc", 1)

                    .reduceByKey(reduceFunc = Int::plus) // ("aaa", 2), ("bbb", 1), ("ccc", 1)

                    .flatMapValues { iterator { yield(it); yield(it) } } // ("aaa", 2), ("aaa", 2), ("bbb", 1), ("bbb", 1), ("ccc", 1), ("ccc", 1)

                    .groupByKey() // ("aaa", [2, 2]), ("bbb", [1, 1]), ("ccc", [1, 1])

                    .flatMap { (key, values) ->
                        values.mapIndexed { i, it -> key X it + i }.iterator()
                    } // ("aaa", 2), ("aaa", 3), ("bbb", 1), ("bbb", 2), ("ccc", 1), ("ccc", 2)

                    .combineByKey(
                        createCombiner = { listOf(it) },
                        mergeValue = { list, int ->
                            list + int
                        },
                        mergeCombiner = { list1, list2 ->
                            list1 + list2
                        },
                    ) // ("aaa", [2, 3]), ("bbb", [1, 2]), ("ccc", [1, 2])


                    // Note: this will update state inside the checkpoint, which we won't test here for now
                    .updateStateByKey(numPartitions = 3) { lists, s: Int? ->
                        (s ?: 0) + lists.sumOf { it.sum() }
                    } // ("aaa", 5), ("bbb", 3), ("ccc", 3)

                inputStream.foreachRDD { rdd, _ ->
                    withSpark(rdd) {
                        rdd.toDS().forEach {
                            it._1 shouldBeIn input

                            resultBroadcast.value.list = resultBroadcast.value.list.plusElement(it)
                        }
                    }
                }
            }

            result.list.shouldContainAll(t("aaa", 5), t("bbb", 3), t("ccc", 3))
        }
    }
})

private fun createTempDir() = Utils.createTempDir(System.getProperty("java.io.tmpdir"), "spark")
    .apply { deleteOnExit() }

private fun createCorruptedCheckpoint(): String {
    val checkpointDirectory = createTempDir().absolutePath
    val fakeCheckpointFile = Checkpoint.checkpointFile(checkpointDirectory, Time(1000))
    FileUtils.write(File(fakeCheckpointFile.toString()), "blablabla", StandardCharsets.UTF_8)
    assert(Checkpoint.getCheckpointFiles(checkpointDirectory, (null as FileSystem?).toOption()).nonEmpty())
    return checkpointDirectory
}


class Counter(@Volatile var value: Int) : Serializable

class Result(@Volatile var list: List<Tuple2<String, Int>> = listOf()) : Serializable