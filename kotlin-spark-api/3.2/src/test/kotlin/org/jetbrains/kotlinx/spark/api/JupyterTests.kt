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

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import jupyter.kotlin.DependsOn
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.streaming.Duration
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.EvalRequestData
import org.jetbrains.kotlinx.jupyter.ReplForJupyter
import org.jetbrains.kotlinx.jupyter.ReplForJupyterImpl
import org.jetbrains.kotlinx.jupyter.api.Code
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import org.jetbrains.kotlinx.jupyter.libraries.EmptyResolutionInfoProvider
import org.jetbrains.kotlinx.jupyter.repl.EvalResultEx
import org.jetbrains.kotlinx.jupyter.testkit.ReplProvider
import org.jetbrains.kotlinx.jupyter.util.PatternNameAcceptanceRule
import org.jetbrains.kotlinx.spark.api.tuples.X
import org.jetbrains.kotlinx.spark.api.tuples.component1
import org.jetbrains.kotlinx.spark.api.tuples.component2
import java.util.*
import kotlin.script.experimental.jvm.util.classpathFromClassloader

class JupyterTests : ShouldSpec({
    val replProvider = ReplProvider { classpath ->
        ReplForJupyterImpl(
            resolutionInfoProvider = EmptyResolutionInfoProvider,
            scriptClasspath = classpath,
            isEmbedded = true,
        ).apply {
            eval {
                librariesScanner.addLibrariesFromClassLoader(
                    classLoader = currentClassLoader,
                    host = this,
                    integrationTypeNameRules = listOf(
                        PatternNameAcceptanceRule(false, "org.jetbrains.kotlinx.spark.api.jupyter.**"),
                        PatternNameAcceptanceRule(true, "org.jetbrains.kotlinx.spark.api.jupyter.SparkIntegration"),
                    ),
                )
            }
        }
    }

    val currentClassLoader = DependsOn::class.java.classLoader
    val scriptClasspath = classpathFromClassloader(currentClassLoader).orEmpty()

    fun createRepl(): ReplForJupyter = replProvider(scriptClasspath)
    suspend fun withRepl(action: suspend ReplForJupyter.() -> Unit): Unit = createRepl().action()

    context("Jupyter") {
        withRepl {

            should("Allow functions on local data classes") {
                @Language("kts")
                val klass = exec("""data class Test(val a: Int, val b: String)""")

                @Language("kts")
                val ds = exec("""val ds = dsOf(Test(1, "hi"), Test(2, "something"))""")

                @Language("kts")
                val filtered = exec("""val filtered = ds.filter { it.a > 1 }""")

                @Language("kts")
                val filteredShow = exec("""filtered.show()""")
            }

            should("Have spark instance") {
                @Language("kts")
                val spark = exec("""spark""")
                spark as? SparkSession shouldNotBe null
            }

            should("Have JavaSparkContext instance") {
                @Language("kts")
                val sc = exec("""sc""")
                sc as? JavaSparkContext shouldNotBe null
            }

            should("render Datasets") {
                @Language("kts")
                val html = execHtml(
                    """
                    val ds = listOf(1, 2, 3).toDS()
                    ds
                    """.trimIndent()
                )
                println(html)

                html shouldContain "value"
                html shouldContain "1"
                html shouldContain "2"
                html shouldContain "3"
            }

            should("render JavaRDDs") {
                @Language("kts")
                val html = execHtml(
                    """
                    val rdd: JavaRDD<List<Int>> = sc.parallelize(listOf(
                        listOf(1, 2, 3), 
                        listOf(4, 5, 6),
                    ))
                    rdd
                    """.trimIndent()
                )
                println(html)

                html shouldContain "1, 2, 3"
                html shouldContain "4, 5, 6"
            }

            should("render JavaRDDs with Arrays") {
                @Language("kts")
                val html = execHtml(
                    """
                    val rdd: JavaRDD<IntArray> = sc.parallelize(listOf(
                        intArrayOf(1, 2, 3), 
                        intArrayOf(4, 5, 6),
                    ))
                    rdd
                    """.trimIndent()
                )
                println(html)

                html shouldContain "1, 2, 3"
                html shouldContain "4, 5, 6"
            }

            xshould("not render JavaRDDs with custom class") {

                @Language("kts")
                val klass = exec("""
                    data class Test(
                        val longFirstName: String,
                        val second: LongArray,
                        val somethingSpecial: Map<Int, String>,
                    ): Serializable
                """.trimIndent())

                @Language("kts")
                val html = execHtml("""
                    val rdd = sc.parallelize(
                        listOf(
                            Test("aaaaaaaaa", longArrayOf(1L, 100000L, 24L), mapOf(1 to "one", 2 to "two")),
                            Test("aaaaaaaaa", longArrayOf(1L, 100000L, 24L), mapOf(1 to "one", 2 to "two")),
                        )
                    )
                    rdd
                    """.trimIndent()
                )
                html shouldContain "Cannot render this RDD of this class."
            }

            should("render JavaPairRDDs") {
                @Language("kts")
                val html = execHtml(
                    """
                    val rdd: JavaPairRDD<Int, Int> = sc.parallelizePairs(listOf(
                        c(1, 2).toTuple(),
                        c(3, 4).toTuple(),
                    ))
                    rdd
                    """.trimIndent()
                )
                println(html)

                html shouldContain "1, 2"
                html shouldContain "3, 4"
            }

            should("render JavaDoubleRDD") {
                @Language("kts")
                val html = execHtml(
                    """
                    val rdd: JavaDoubleRDD = sc.parallelizeDoubles(listOf(1.0, 2.0, 3.0, 4.0,))
                    rdd
                    """.trimIndent()
                )
                println(html)

                html shouldContain "1.0"
                html shouldContain "2.0"
                html shouldContain "3.0"
                html shouldContain "4.0"
            }

            should("render Scala RDD") {
                @Language("kts")
                val html = execHtml(
                    """
                    val rdd: RDD<List<Int>> = sc.parallelize(listOf(
                        listOf(1, 2, 3), 
                        listOf(4, 5, 6),
                    )).rdd()
                    rdd
                    """.trimIndent()
                )
                println(html)

                html shouldContain "1, 2, 3"
                html shouldContain "4, 5, 6"
            }

            @Language("kts")
            val _stop = exec("""spark.stop()""")
        }
    }
})

class JupyterStreamingTests : ShouldSpec({
    val replProvider = ReplProvider { classpath ->
        ReplForJupyterImpl(
            resolutionInfoProvider = EmptyResolutionInfoProvider,
            scriptClasspath = classpath,
            isEmbedded = true,
        ).apply {
            eval {
                librariesScanner.addLibrariesFromClassLoader(
                    classLoader = currentClassLoader,
                    host = this,
                    integrationTypeNameRules = listOf(
                        PatternNameAcceptanceRule(false, "org.jetbrains.kotlinx.spark.api.jupyter.**"),
                        PatternNameAcceptanceRule(true,
                            "org.jetbrains.kotlinx.spark.api.jupyter.SparkStreamingIntegration"),
                    ),
                )
            }
        }
    }

    val currentClassLoader = DependsOn::class.java.classLoader
    val scriptClasspath = classpathFromClassloader(currentClassLoader).orEmpty()

    fun createRepl(): ReplForJupyter = replProvider(scriptClasspath)
    suspend fun withRepl(action: suspend ReplForJupyter.() -> Unit): Unit = createRepl().action()

    context("Jupyter") {
        withRepl {

            should("Not have spark instance") {
                shouldThrowAny {
                    @Language("kts")
                    val spark = exec("""spark""")
                    Unit
                }
            }

            should("Not have sc instance") {
                shouldThrowAny {
                    @Language("kts")
                    val sc = exec("""sc""")
                    Unit
                }
            }

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

        }
    }
})


private fun ReplForJupyter.execEx(code: Code): EvalResultEx = evalEx(EvalRequestData(code))

private fun ReplForJupyter.exec(code: Code): Any? = execEx(code).renderedValue

private fun ReplForJupyter.execRaw(code: Code): Any? = execEx(code).rawValue

@JvmName("execTyped")
private inline fun <reified T : Any> ReplForJupyter.exec(code: Code): T {
    val res = exec(code)
    res.shouldBeInstanceOf<T>()
    return res
}

private fun ReplForJupyter.execHtml(code: Code): String {
    val res = exec<MimeTypedResult>(code)
    val html = res["text/html"]
    html.shouldNotBeNull()
    return html
}