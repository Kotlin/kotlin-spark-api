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


import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.api.FieldValue
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost

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
                println("To start a spark streaming session, simply use `withSparkStreaming { }` inside a cell. To use Spark normally, use `withSpark { }` in a cell, or use `%use spark` to start a Spark session for the whole notebook.")""".trimIndent(),
        ).map(::execute)
    }

    override fun KotlinKernelHost.onShutdown() = Unit

    override fun KotlinKernelHost.afterCellExecution(snippetInstance: Any, result: FieldValue) = Unit
}
