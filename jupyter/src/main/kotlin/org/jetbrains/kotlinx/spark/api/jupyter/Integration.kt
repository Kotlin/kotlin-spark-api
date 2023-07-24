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

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.apache.spark.api.java.JavaRDDLike
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.api.*
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.displayLimitName
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.displayTruncateName
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.scalaName
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.sparkName
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.sparkPropertiesName
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.versionName
import kotlin.reflect.KProperty1
import kotlin.reflect.typeOf


abstract class Integration(private val notebook: Notebook, private val options: MutableMap<String, String?>) :
    JupyterIntegration() {

    protected val kotlinVersion = /*$"\""+kotlin+"\""$*/ /*-*/ ""
    protected val scalaCompatVersion = /*$"\""+scalaCompat+"\""$*/ /*-*/ ""
    protected val scalaVersion = /*$"\""+scala+"\""$*/ /*-*/ ""
    protected val sparkVersion = /*$"\""+spark+"\""$*/ /*-*/ ""
    protected val version = /*$"\""+version+"\""$*/ /*-*/ ""

    protected val displayLimitOld = "DISPLAY_LIMIT"
    protected val displayTruncateOld = "DISPLAY_TRUNCATE"

    protected val properties: Properties
        get() = notebook
            .variablesState[sparkPropertiesName]!!
            .value
            .getOrThrow() as Properties


    protected open val usingProperties = arrayOf(
        displayLimitName,
        displayTruncateName,
        sparkName,
        scalaName,
        versionName,
    )

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
        "org.apache.spark:spark-repl_$scalaCompatVersion:$sparkVersion",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion",
        "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion",
        "org.apache.spark:spark-sql_$scalaCompatVersion:$sparkVersion",
        "org.apache.spark:spark-yarn_$scalaCompatVersion:$sparkVersion",
        "org.apache.spark:spark-streaming_$scalaCompatVersion:$sparkVersion",
        "org.apache.spark:spark-mllib_$scalaCompatVersion:$sparkVersion",
        "org.apache.spark:spark-sql_$scalaCompatVersion:$sparkVersion",
        "org.apache.spark:spark-graphx_$scalaCompatVersion:$sparkVersion",
        "org.apache.spark:spark-launcher_$scalaCompatVersion:$sparkVersion",
        "org.apache.spark:spark-catalyst_$scalaCompatVersion:$sparkVersion",
        "org.apache.spark:spark-streaming_$scalaCompatVersion:$sparkVersion",
        "org.apache.spark:spark-core_$scalaCompatVersion:$sparkVersion",
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

            val mutableOptions = options.toMutableMap()

            declare(
                VariableDeclaration(
                    name = sparkPropertiesName,
                    value = object : Properties, MutableMap<String, String?> by mutableOptions {
                        override fun toString(): String = "Properties: $mutableOptions"
                    },
                    type = typeOf<Properties>(),
                    isMutable = true,
                )
            )

            @Language("kts")
            val _0 = execute(
                """
                @Deprecated("Use ${displayLimitName}=${properties.displayLimit} in %use magic or ${sparkPropertiesName}.${displayLimitName} = ${properties.displayLimit} instead", ReplaceWith("${sparkPropertiesName}.${displayLimitName}"))
                var $displayLimitOld: Int
                    get() = ${sparkPropertiesName}.${displayLimitName}
                    set(value) {
                        println("$displayLimitOld is deprecated: Use ${sparkPropertiesName}.${displayLimitName} instead")
                        ${sparkPropertiesName}.${displayLimitName} = value
                    }
                
                @Deprecated("Use ${displayTruncateName}=${properties.displayTruncate} in %use magic or ${sparkPropertiesName}.${displayTruncateName} = ${properties.displayTruncate} instead", ReplaceWith("${sparkPropertiesName}.${displayTruncateName}"))
                var $displayTruncateOld: Int
                    get() = ${sparkPropertiesName}.${displayTruncateName}
                    set(value) {
                        println("$displayTruncateOld is deprecated: Use ${sparkPropertiesName}.${displayTruncateName} instead")
                        ${sparkPropertiesName}.${displayTruncateName} = value
                    }
            """.trimIndent()
            )

            onLoaded()
        }

        beforeCellExecution {
            if (scalaCompatVersion.toDouble() >= 2.13)
                execute("scala.`Console\$`.`MODULE\$`.setOutDirect(System.out)")
            else
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


        // Render Dataset
        render<Dataset<*>> {
            with(properties) {
                HTML(it.toHtml(limit = displayLimit, truncate = displayTruncate))
            }
        }

        render<RDD<*>> {
            with(properties) {
                HTML(it.toJavaRDD().toHtml(limit = displayLimit, truncate = displayTruncate))
            }
        }

        render<JavaRDDLike<*, *>> {
            with(properties) {
                HTML(it.toHtml(limit = displayLimit, truncate = displayTruncate))
            }

        }

        onLoadedAlsoDo()
    }
}
