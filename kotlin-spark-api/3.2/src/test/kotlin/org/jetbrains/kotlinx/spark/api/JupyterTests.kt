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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import jupyter.kotlin.DependsOn
import org.apache.spark.api.java.JavaSparkContext
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.EvalRequestData
import org.jetbrains.kotlinx.jupyter.ReplForJupyter
import org.jetbrains.kotlinx.jupyter.api.Code
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import org.jetbrains.kotlinx.jupyter.repl.EvalResultEx
import org.jetbrains.kotlinx.jupyter.testkit.ReplProvider
import kotlin.script.experimental.jvm.util.classpathFromClassloader

class JupyterTests : ShouldSpec({
    val replProvider: ReplProvider = ReplProvider.withoutLibraryResolution
    val currentClassLoader = DependsOn::class.java.classLoader
    val scriptClasspath = classpathFromClassloader(currentClassLoader).orEmpty()

    fun createRepl(): ReplForJupyter = replProvider(scriptClasspath)
    fun withRepl(action: ReplForJupyter.() -> Unit): Unit = createRepl().action()

    context("Jupyter") {
        should("Have spark instance") {
            withRepl {
                @Language("kts")
                val spark = exec("""spark""")

                spark as? SparkSession shouldNotBe null
            }
        }

        should("Have JavaSparkContext instance") {
            withRepl {
                @Language("kts")
                val sc = exec("""sc""")

                sc as? JavaSparkContext shouldNotBe null
            }
        }

        should("render Datasets") {
            withRepl {
                @Language("kts")
                val html = execHtml(
                    """
                    val ds = listOf(1, 2, 3).toDS(spark)
                    ds
                    """.trimIndent()
                )
                println(html)

                html shouldContain "value"
                html shouldContain "1"
                html shouldContain "2"
                html shouldContain "3"
            }
        }

        should("render JavaRDDs") {
            withRepl {
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

                html shouldContain "[1, 2, 3]"
                html shouldContain "[4, 5, 6]"
            }
        }

        should("render JavaRDDs with Arrays") {
            withRepl {
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

                html shouldContain "[1, 2, 3]"
                html shouldContain "[4, 5, 6]"
            }
        }

        should("render JavaPairRDDs") {
            withRepl {
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

                html shouldContain "(1,2)"
                html shouldContain "(3,4)"

            }
        }

        should("render JavaDoubleRDD") {
            withRepl {
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
        }

        should("render Scala RDD") {
            withRepl {
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

                html shouldContain "[1, 2, 3]"
                html shouldContain "[4, 5, 6]"
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