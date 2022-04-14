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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import org.apache.commons.io.FileUtils
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.SparkException
import org.apache.spark.streaming.*
import org.apache.spark.util.Utils
import org.jetbrains.kotlinx.spark.api.tuples.X
import org.jetbrains.kotlinx.spark.api.tuples.component1
import org.jetbrains.kotlinx.spark.api.tuples.component2
import java.io.File
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


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

