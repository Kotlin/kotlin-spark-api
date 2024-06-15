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

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import jupyter.kotlin.DependsOn
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.api.Code
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import org.jetbrains.kotlinx.jupyter.api.MimeTypes
import org.jetbrains.kotlinx.jupyter.libraries.createLibraryHttpUtil
import org.jetbrains.kotlinx.jupyter.repl.EvalRequestData
import org.jetbrains.kotlinx.jupyter.repl.ReplForJupyter
import org.jetbrains.kotlinx.jupyter.repl.creating.createRepl
import org.jetbrains.kotlinx.jupyter.repl.result.EvalResultEx
import org.jetbrains.kotlinx.jupyter.testkit.ReplProvider
import org.jetbrains.kotlinx.jupyter.util.PatternNameAcceptanceRule
import org.jetbrains.kotlinx.spark.api.SparkSession
import java.io.Serializable
import kotlin.script.experimental.jvm.util.classpathFromClassloader


class JupyterTests : ShouldSpec({

    val replProvider = ReplProvider { classpath ->
        createRepl(
            httpUtil = createLibraryHttpUtil(),
            scriptClasspath = classpath,
            isEmbedded = true,
        ).apply {
            eval {
                librariesScanner.addLibrariesFromClassLoader(
                    classLoader = currentClassLoader,
                    host = this,
                    notebook = notebook,
                    integrationTypeNameRules = listOf(
                        PatternNameAcceptanceRule(
                            acceptsFlag = false,
                            pattern = "org.jetbrains.kotlinx.spark.api.jupyter.**",
                        ),
                        PatternNameAcceptanceRule(
                            acceptsFlag = true,
                            pattern = "org.jetbrains.kotlinx.spark.api.jupyter.SparkIntegration",
                        ),
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
            exec("%trackExecution")

            should("Allow functions on local data classes") {
                @Language("kts")
                val klass = exec("""@Sparkify data class Test(val a: Int, val b: String)""")

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

            xshould("render Datasets") {
                @Language("kts")
                val html = execForDisplayText(
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

            xshould("render JavaRDDs") {
                @Language("kts")
                val html = execForDisplayText(
                    """
                    val rdd: JavaRDD<List<Int>> = listOf(
                        listOf(1, 2, 3), 
                        listOf(4, 5, 6),
                    ).toRDD()
                    rdd
                    """.trimIndent()
                )
                println(html)

                html shouldContain "1, 2, 3"
                html shouldContain "4, 5, 6"
            }

            xshould("render JavaRDDs with Arrays") {
                @Language("kts")
                val html = execForDisplayText(
                    """
                    val rdd: JavaRDD<IntArray> = rddOf(
                        intArrayOf(1, 2, 3), 
                        intArrayOf(4, 5, 6),
                    )
                    rdd
                    """.trimIndent()
                )
                println(html)

                html shouldContain "1, 2, 3"
                html shouldContain "4, 5, 6"
            }

            xshould("render JavaRDDs with custom class") {

                @Language("kts")
                val klass = exec(
                    """
                    @Sparkify data class Test(
                        val longFirstName: String,
                        val second: LongArray,
                        val somethingSpecial: Map<Int, String>,
                    ): Serializable
                """.trimIndent()
                )

                @Language("kts")
                val html = execForDisplayText(
                    """
                    val rdd =
                        listOf(
                            Test("aaaaaaaaa", longArrayOf(1L, 100000L, 24L), mapOf(1 to "one", 2 to "two")),
                            Test("aaaaaaaaa", longArrayOf(1L, 100000L, 24L), mapOf(1 to "one", 2 to "two")),
                        ).toRDD()
                    
                    rdd
                    """.trimIndent()
                )
                html shouldContain """
                    +-------------+---------------+--------------------+
                    |longFirstName|         second|    somethingSpecial|
                    +-------------+---------------+--------------------+
                    |    aaaaaaaaa|[1, 100000, 24]|{1 -> one, 2 -> two}|
                    |    aaaaaaaaa|[1, 100000, 24]|{1 -> one, 2 -> two}|
                    +-------------+---------------+--------------------+""".trimIndent()
            }

            xshould("render JavaPairRDDs") {
                @Language("kts")
                val html = execForDisplayText(
                    """
                    val rdd: JavaPairRDD<Int, Int> = rddOf(
                        t(1, 2),
                        t(3, 4),
                    ).toJavaPairRDD()
                    rdd
                    """.trimIndent()
                )
                println(html)

                html shouldContain """
                    +---+---+
                    | _1| _2|
                    +---+---+
                    |  1|  2|
                    |  3|  4|
                    +---+---+""".trimIndent()
            }

            xshould("render JavaDoubleRDD") {
                @Language("kts")
                val html = execForDisplayText(
                    """
                    val rdd: JavaDoubleRDD = rddOf(1.0, 2.0, 3.0, 4.0,).toJavaDoubleRDD()
                    rdd
                    """.trimIndent()
                )
                println(html)

                html shouldContain "1.0"
                html shouldContain "2.0"
                html shouldContain "3.0"
                html shouldContain "4.0"
            }

            xshould("render Scala RDD") {
                @Language("kts")
                val html = execForDisplayText(
                    """
                    val rdd: RDD<List<Int>> = rddOf(
                        listOf(1, 2, 3), 
                        listOf(4, 5, 6),
                    ).rdd()
                    rdd
                    """.trimIndent()
                )
                println(html)

                html shouldContain "1, 2, 3"
                html shouldContain "4, 5, 6"
            }

            xshould("truncate dataset cells using properties") {

                @Language("kts")
                val oldTruncation = exec("""sparkProperties.displayTruncate""") as Int

                @Language("kts")
                val html = execForDisplayText(
                    """
                        @Sparkify data class Test(val a: String)
                        sparkProperties.displayTruncate = 3
                        dsOf(Test("aaaaaaaaaa"))
                    """.trimIndent()
                )

                @Language("kts")
                val restoreTruncation = exec("""sparkProperties.displayTruncate = $oldTruncation""")

                html shouldContain "aaa"
                html shouldNotContain "aaaaaaaaaa"
            }

            xshould("limit dataset rows using properties") {

                @Language("kts")
                val oldLimit = exec("""sparkProperties.displayLimit""") as Int

                @Language("kts")
                val html = execForDisplayText(
                    """
                        @Sparkify data class Test(val a: String)
                        sparkProperties.displayLimit = 3
                        dsOf(Test("a"), Test("b"), Test("c"), Test("d"), Test("e"))
                    """.trimIndent()
                )

                @Language("kts")
                val restoreLimit = exec("""sparkProperties.displayLimit = $oldLimit""")

                html shouldContain "a|"
                html shouldContain "b|"
                html shouldContain "c|"
                html shouldNotContain "d|"
                html shouldNotContain "e|"
            }

            xshould("truncate rdd cells using properties") {

                @Language("kts")
                val oldTruncation = exec("""sparkProperties.displayTruncate""") as Int

                @Language("kts")
                val html = execForDisplayText(
                    """
                        sparkProperties.displayTruncate = 3
                        rddOf("aaaaaaaaaa")
                    """.trimIndent()
                )

                @Language("kts")
                val restoreTruncation = exec("""sparkProperties.displayTruncate = $oldTruncation""")

                html shouldContain "aaa"
                html shouldNotContain "aaaaaaaaaa"
            }

            xshould("limit rdd rows using properties") {

                @Language("kts")
                val oldLimit = exec("""sparkProperties.displayLimit""") as Int

                @Language("kts")
                val html = execForDisplayText(
                    """
                        sparkProperties.displayLimit = 3
                        rddOf("a", "b", "c", "d", "e")
                    """.trimIndent()
                )

                @Language("kts")
                val restoreLimit = exec("""sparkProperties.displayLimit = $oldLimit""")

                html shouldContain " a|"
                html shouldContain " b|"
                html shouldContain " c|"
                html shouldNotContain " d|"
                html shouldNotContain " e|"
            }

            @Language("kts")
            val _stop = exec("""spark.stop()""")
        }
    }
})

class JupyterStreamingTests : ShouldSpec({
    val replProvider = ReplProvider { classpath ->
        createRepl(
            httpUtil = createLibraryHttpUtil(),
            scriptClasspath = classpath,
            isEmbedded = true,
        ).apply {
            eval {
                librariesScanner.addLibrariesFromClassLoader(
                    classLoader = currentClassLoader,
                    host = this,
                    notebook = notebook,
                    integrationTypeNameRules = listOf(
                        PatternNameAcceptanceRule(
                            acceptsFlag = false,
                            pattern = "org.jetbrains.kotlinx.spark.api.jupyter.**",
                        ),
                        PatternNameAcceptanceRule(
                            acceptsFlag = true,
                            pattern = "org.jetbrains.kotlinx.spark.api.jupyter.SparkStreamingIntegration",
                        ),
                    ),
                )
            }
        }
    }

    val currentClassLoader = DependsOn::class.java.classLoader
    val scriptClasspath = classpathFromClassloader(currentClassLoader).orEmpty()

    fun createRepl(): ReplForJupyter = replProvider(scriptClasspath)
    suspend fun withRepl(action: suspend ReplForJupyter.() -> Unit): Unit = createRepl().action()

    xcontext("Jupyter") {
        withRepl {

            // For when onInterrupt is implemented in the Jupyter kernel
            should("Have sscCollection instance") {

                @Language("kts")
                val sscCollection = exec("""sscCollection""")
                sscCollection as? MutableSet<JavaStreamingContext> shouldNotBe null
            }

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

                @Language("kts")
                val value = exec(
                    """
                    import java.util.LinkedList
                    import org.apache.spark.api.java.function.ForeachFunction
                    import org.apache.spark.util.LongAccumulator
                   

                    val input = arrayListOf("aaa", "bbb", "aaa", "ccc")
                    
                    @Volatile
                    var counter: LongAccumulator? = null
    
                    withSparkStreaming(Duration(10), timeout = 1_000) {
    
                        val queue = withSpark(ssc) {
                            LinkedList(listOf(sc.parallelize(input)))
                        }
    
                        val inputStream = ssc.queueStream(queue)
    
                        inputStream.foreachRDD { rdd, _ ->
                            withSpark(rdd) {
                                if (counter == null)
                                    counter = sc.sc().longAccumulator()

                                rdd.toDS().showDS().forEach {
                                    if (it !in input) error(it + " should be in input")
                                    counter!!.add(1L)
                                }
                            }
                        }
                    }
                    counter!!.sum()
                    """.trimIndent()
                ) as Long

                value shouldBe 4L
            }

        }
    }
})


private fun ReplForJupyter.execEx(code: Code): EvalResultEx = evalEx(EvalRequestData(code))

private fun ReplForJupyter.exec(code: Code): Any? = (execEx(code) as? EvalResultEx.Success)?.renderedValue

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

private fun ReplForJupyter.execForDisplayText(code: Code): String {
    val res = exec<MimeTypedResult>(code)
    val text = res[MimeTypes.PLAIN_TEXT]
    text.shouldNotBeNull()
    return text
}

class Counter(@Volatile var value: Int) : Serializable
