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

    override fun KotlinKernelHost.onLoaded() {
        val _0 = execute("""%dumpClassesForSpark""")

        @Language("kts")
        val _1 = listOf(
            """
                println("To start a spark streaming session, simply use `withSparkStreaming { }` inside a cell. To use Spark normally, use `withSpark { }` in a cell, or use `%use spark` to start a Spark session for the whole notebook.")""".trimIndent(),
        ).map(::execute)
    }
}
