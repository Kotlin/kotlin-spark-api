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

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.apache.spark.api.java.JavaRDDLike
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.apache.spark.unsafe.array.ByteArrayMethods
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.api.HTML
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import org.jetbrains.kotlinx.spark.api.*
import java.io.InputStreamReader


import org.apache.spark.*
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.jetbrains.kotlinx.jupyter.api.FieldValue
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost
import scala.collection.*
import org.jetbrains.kotlinx.spark.api.SparkSession
import scala.Product
import java.io.Serializable
import scala.collection.Iterable as ScalaIterable
import scala.collection.Iterator as ScalaIterator

/**
 * %use spark-streaming
 */
@Suppress("UNUSED_VARIABLE", "LocalVariableName")
@OptIn(ExperimentalStdlibApi::class)
internal class SparkStreamingIntegration : Integration() {

    override val imports: Array<String> = super.imports + arrayOf(
        "org.apache.spark.deploy.SparkHadoopUtil",
        "org.apache.hadoop.conf.Configuration",
    )

    override fun KotlinKernelHost.onLoaded() {
        val _0 = execute("""%dumpClassesForSpark""")

        @Language("kts")
        val _1 = listOf(
            """
                 val sscCollection = mutableSetOf<JavaStreamingContext>()
            """.trimIndent(),
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
                    var ssc: JavaStreamingContext? = null
                    try {

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

                            val ssc1 = JavaStreamingContext(sc, batchDuration)
                            ssc1.checkpoint(checkpointPath)

                            kSparkStreamingSession = KSparkStreamingSession(ssc1)
                            func(kSparkStreamingSession!!)

                            ssc1
                        }

                        ssc = when {
                            checkpointPath != null ->
                                JavaStreamingContext.getOrCreate(checkpointPath, creatingFunc, hadoopConf, createOnError)

                            else -> creatingFunc()
                        }
    
                        sscCollection += ssc!!

                        if (startStreamingContext) {
                            ssc!!.start()
                            kSparkStreamingSession?.invokeRunAfterStart()
                        }
                        ssc!!.awaitTerminationOrTimeout(timeout)
                    } finally {
                        ssc?.stop()
                        println("stopping ssc")
                        ssc?.awaitTermination()
                        println("ssc stopped")
                        ssc?.let(sscCollection::remove)   
                    }
                }
            """.trimIndent(),
            """
                println("To start a spark streaming session, simply use `withSparkStreaming { }` inside a cell. To use Spark normally, use `withSpark { }` in a cell, or use `%use spark` to start a Spark session for the whole notebook.")""".trimIndent(),
        ).map(::execute)
    }

    override fun KotlinKernelHost.afterCellExecution(snippetInstance: Any, result: FieldValue) {

        @Language("kts")
        val _1 = listOf(
            """
                while (sscCollection.isNotEmpty())
                    sscCollection.first().let {
                        it.stop()
                        sscCollection.remove(it)
                    }
            """.trimIndent(),
            """
                println("afterCellExecution cleanup!")
            """.trimIndent()
        ).map(::execute)
    }
}
