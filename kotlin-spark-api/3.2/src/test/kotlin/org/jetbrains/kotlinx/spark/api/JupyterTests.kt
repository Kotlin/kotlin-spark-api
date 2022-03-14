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
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import jupyter.kotlin.DependsOn
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

    fun createRepl() = replProvider(scriptClasspath)
    fun withRepl(action: ReplForJupyter.() -> Unit) = createRepl().action()

    context("DF rendering") {
        should("render DFs") {
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
    }
})

fun ReplForJupyter.execEx(code: Code): EvalResultEx {
    return evalEx(EvalRequestData(code))
}

fun ReplForJupyter.exec(code: Code): Any? {
    return execEx(code).renderedValue
}

fun ReplForJupyter.execRaw(code: Code): Any? {
    return execEx(code).rawValue
}

@JvmName("execTyped")
inline fun <reified T : Any> ReplForJupyter.exec(code: Code): T {
    val res = exec(code)
    res.shouldBeInstanceOf<T>()
    return res
}

fun ReplForJupyter.execHtml(code: Code): String {
    val res = exec<MimeTypedResult>(code)
    val html = res["text/html"]
    html.shouldNotBeNull()
    return html
}