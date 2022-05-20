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

import org.apache.spark.api.java.JavaRDDLike
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.jetbrains.kotlinx.jupyter.api.*
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import kotlin.reflect.typeOf

abstract class Integration : JupyterIntegration() {

    private val kotlinVersion = "1.6.21"
    private val scalaCompatVersion = "2.12"
    private val scalaVersion = "2.12.15"
    private val spark3Version = "3.2.1"

    private val displayLimit = "DISPLAY_LIMIT"
    private val displayLimitDefault = 20
    private val displayTruncate = "DISPLAY_TRUNCATE"
    private val displayTruncateDefault = 30

    /**
     * Will be run after importing all dependencies
     */
    open fun KotlinKernelHost.onLoaded() = Unit

    open fun KotlinKernelHost.onShutdown() = Unit

    open fun KotlinKernelHost.onInterrupt() = Unit

    open fun KotlinKernelHost.beforeCellExecution() = Unit

    open fun KotlinKernelHost.afterCellExecution(snippetInstance: Any, result: FieldValue) = Unit

    open fun Builder.onLoadedAlsoDo() = Unit

    open val dependencies: Array<String> = arrayOf(
        "org.apache.spark:spark-repl_$scalaCompatVersion:$spark3Version",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion",
        "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion",
        "org.apache.spark:spark-sql_$scalaCompatVersion:$spark3Version",
        "org.apache.spark:spark-streaming_$scalaCompatVersion:$spark3Version",
        "org.apache.spark:spark-mllib_$scalaCompatVersion:$spark3Version",
        "org.apache.spark:spark-sql_$scalaCompatVersion:$spark3Version",
        "org.apache.spark:spark-graphx_$scalaCompatVersion:$spark3Version",
        "org.apache.spark:spark-launcher_$scalaCompatVersion:$spark3Version",
        "org.apache.spark:spark-catalyst_$scalaCompatVersion:$spark3Version",
        "org.apache.spark:spark-streaming_$scalaCompatVersion:$spark3Version",
        "org.apache.spark:spark-core_$scalaCompatVersion:$spark3Version",
        "org.scala-lang:scala-library:$scalaVersion",
        "org.scala-lang.modules:scala-xml_$scalaCompatVersion:2.0.1",
        "org.scala-lang:scala-reflect:$scalaVersion",
        "org.scala-lang:scala-compiler:$scalaVersion",
        "commons-io:commons-io:2.11.0",
    )

    open val imports: Array<String> = arrayOf(
        "org.jetbrains.kotlinx.spark.api.*",
        "org.jetbrains.kotlinx.spark.api.tuples.*",
        *(1..22).map { "scala.Tuple$it" }.toTypedArray(),
        "org.apache.spark.sql.functions.*",
        "org.apache.spark.*",
        "org.apache.spark.sql.*",
        "org.apache.spark.api.java.*",
        "scala.collection.Seq",
        "org.apache.spark.rdd.*",
        "java.io.Serializable",
        "org.apache.spark.streaming.api.java.*",
        "org.apache.spark.streaming.api.*",
        "org.apache.spark.streaming.*",
    )

    override fun Builder.onLoaded() {
        dependencies(*dependencies)
        import(*imports)

        onLoaded {
            declare(
                VariableDeclaration(
                    name = displayLimit,
                    value = displayLimitDefault,
                    type = typeOf<Int>(),
                    isMutable = true,
                ),
                VariableDeclaration(
                    name = displayTruncate,
                    value = displayTruncateDefault,
                    type = typeOf<Int>(),
                    isMutable = true,
                ),
            )

            onLoaded()
        }

        beforeCellExecution {
            execute("""scala.Console.setOut(System.out)""")

            beforeCellExecution()
        }

        afterCellExecution { snippetInstance, result ->
            afterCellExecution(snippetInstance, result)
        }

        onInterrupt {
            onInterrupt()
        }

        onShutdown {
            onShutdown()
        }

        fun getLimitAndTruncate() = Pair(
            notebook
                .variablesState[displayLimit]
                ?.value
                ?.getOrNull() as? Int
                ?: displayLimitDefault,
            notebook
                .variablesState[displayTruncate]
                ?.value
                ?.getOrNull() as? Int
                ?: displayTruncateDefault
        )


        // Render Dataset
        render<Dataset<*>> {
            val (limit, truncate) = getLimitAndTruncate()

            HTML(it.toHtml(limit = limit, truncate = truncate))
        }

        render<RDD<*>> {
            val (limit, truncate) = getLimitAndTruncate()

            HTML(it.toJavaRDD().toHtml(limit = limit, truncate = truncate))
        }

        render<JavaRDDLike<*, *>> {
            val (limit, truncate) = getLimitAndTruncate()

            HTML(it.toHtml(limit = limit, truncate = truncate))
        }

        onLoadedAlsoDo()
    }
}
