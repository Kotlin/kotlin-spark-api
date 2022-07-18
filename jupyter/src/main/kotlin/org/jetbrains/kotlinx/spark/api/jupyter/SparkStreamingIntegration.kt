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
package org.jetbrains.kotlinx.spark.api.jupyter


import org.apache.spark.streaming.StreamingContextState
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost
import org.jetbrains.kotlinx.jupyter.api.VariableDeclaration
import org.jetbrains.kotlinx.jupyter.api.declare
import kotlin.reflect.typeOf

/**
 * %use spark-streaming
 */
@Suppress("UNUSED_VARIABLE", "LocalVariableName")
internal class SparkStreamingIntegration : Integration() {

    override val imports: Array<String> = super.imports + arrayOf(
        "org.apache.spark.deploy.SparkHadoopUtil",
        "org.apache.hadoop.conf.Configuration",
    )

    private val sscCollection = mutableSetOf<JavaStreamingContext>()

    override fun KotlinKernelHost.onLoaded() {

        declare(
            VariableDeclaration(
                name = ::sscCollection.name,
                value = sscCollection,
                isMutable = false,
                type = typeOf<MutableSet<JavaStreamingContext>>(),
            )
        )

        val _0 = execute("""%dumpClassesForSpark""")

        @Language("kts")
        val _1 = listOf(
            """
                @JvmOverloads
                fun withSparkStreaming(
                    batchDuration: Duration = Durations.seconds(1L),
                    checkpointPath: String? = null,
                    hadoopConf: Configuration = SparkHadoopUtil.get().conf(),
                    createOnError: Boolean = false,
                    props: Map<String, Any> = emptyMap(),
                    master: String = SparkConf().get("spark.master", "local[*]"),
                    appName: String = "Kotlin Spark Sample",
                    timeout: Long = -1L,
                    startStreamingContext: Boolean = true,
                    func: KSparkStreamingSession.() -> Unit,
                ) {

                    // will only be set when a new context is created
                    var kSparkStreamingSession: KSparkStreamingSession? = null

                    val creatingFunc = {
                        val sc = SparkConf()
                            .setAppName(appName)
                            .setMaster(master)
                            .setAll(
                                props
                                    .map { (key, value) -> key X value.toString() }
                                    .asScalaIterable()
                            )

                        val ssc = JavaStreamingContext(sc, batchDuration)
                        ssc.checkpoint(checkpointPath)

                        kSparkStreamingSession = KSparkStreamingSession(ssc)
                        func(kSparkStreamingSession!!)

                        ssc
                    }

                    val ssc = when {
                        checkpointPath != null ->
                            JavaStreamingContext.getOrCreate(checkpointPath, creatingFunc, hadoopConf, createOnError)

                        else -> creatingFunc()
                    }
                    sscCollection += ssc

                    if (startStreamingContext) {
                        ssc.start()
                        kSparkStreamingSession?.invokeRunAfterStart()
                    }
                    ssc.awaitTerminationOrTimeout(timeout)
                    ssc.stop()
                }
            """.trimIndent(),
            """
                println("To start a Spark (Spark: $sparkVersion, Scala: $scalaCompatVersion, v: $version) Streaming session, simply use `withSparkStreaming { }` inside a cell. To use Spark normally, use `withSpark { }` in a cell, or use `%use spark` to start a Spark session for the whole notebook.")""".trimIndent(),
        ).map(::execute)
    }

    private fun cleanUp(e: Throwable): String {
        while (sscCollection.isNotEmpty())
            sscCollection.first().let {
                while (it.state != StreamingContextState.STOPPED) {
                    try {
                        it.stop(true, true)
                    } catch (_: Exception) {
                    }
                }
                sscCollection.remove(it)
            }

        return "Spark streams cleaned up. Cause: $e"
    }

    override fun Builder.onLoadedAlsoDo() {
        renderThrowable<IllegalMonitorStateException> {
            cleanUp(it)
        }
    }

    override fun KotlinKernelHost.onInterrupt() {
        println(
            cleanUp(InterruptedException("Kernel was interrupted."))
        )
    }
}
