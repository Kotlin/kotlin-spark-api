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

import org.apache.spark.api.java.JavaDoubleRDD
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaRDDLike
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.api.Code
import org.jetbrains.kotlinx.jupyter.api.FieldValue
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import org.jetbrains.kotlinx.jupyter.api.Notebook
import org.jetbrains.kotlinx.jupyter.api.VariableDeclaration
import org.jetbrains.kotlinx.jupyter.api.createRendererByCompileTimeType
import org.jetbrains.kotlinx.jupyter.api.declare
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import org.jetbrains.kotlinx.jupyter.api.textResult
import org.jetbrains.kotlinx.spark.api.SparkSession
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.displayLimitName
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.displayTruncateName
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.scalaName
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.sparkName
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.sparkPropertiesName
import org.jetbrains.kotlinx.spark.api.jupyter.Properties.Companion.versionName
import org.jetbrains.kotlinx.spark.api.kotlinEncoderFor
import org.jetbrains.kotlinx.spark.api.plugin.annotations.ColumnName
import org.jetbrains.kotlinx.spark.api.plugin.annotations.Sparkify
import scala.Tuple2
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf


abstract class Integration(private val notebook: Notebook, private val options: MutableMap<String, String?>) :
    JupyterIntegration() {

    protected val kotlinVersion = /*$"\""+kotlin+"\""$*/ /*-*/ ""
    protected val scalaCompatVersion = /*$"\""+scalaCompat+"\""$*/ /*-*/ ""
    protected val scalaVersion = /*$"\""+scala+"\""$*/ /*-*/ ""
    protected val sparkVersion = /*$"\""+spark+"\""$*/ /*-*/ ""
    protected val version = /*$"\""+version+"\""$*/ /*-*/ ""

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
        "org.jetbrains.kotlinx.spark.api.plugin.annotations.*",
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

    // Needs to be set by integration
    var spark: SparkSession? = null

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

            onLoaded()
        }

        beforeCellExecution {
            if (scalaCompatVersion.toDouble() >= 2.13) {
                execute("scala.`Console\$`.`MODULE\$`.setOutDirect(System.out)")
            } else {
                execute("""scala.Console.setOut(System.out)""")
            }

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

        onClassAnnotation<Sparkify> {
            for (klass in it) {
                if (klass.isData) {
                    execute(generateSparkifyClass(klass))
                }
            }
        }

        // Render Dataset
        render<Dataset<*>> {
            renderDataset(it)
        }

        // using compile time KType, convert this JavaRDDLike to Dataset and render it
        notebook.renderersProcessor.registerWithoutOptimizing(
            createRendererByCompileTimeType<JavaRDDLike<*, *>> {
                if (spark == null) return@createRendererByCompileTimeType it.value.toString()

                val rdd = (it.value as JavaRDDLike<*, *>).rdd()
                val type = when {
                    it.type.isSubtypeOf(typeOf<JavaDoubleRDD>()) ->
                        typeOf<Double>()

                    it.type.isSubtypeOf(typeOf<JavaPairRDD<*, *>>()) ->
                        Tuple2::class.createType(
                            listOf(
                                it.type.arguments.first(),
                                it.type.arguments.last(),
                            )
                        )

                    it.type.isSubtypeOf(typeOf<JavaRDD<*>>()) ->
                        it.type.arguments.first().type!!

                    else -> it.type.arguments.first().type!!
                }
                val ds = spark!!.createDataset(rdd, kotlinEncoderFor(type))
                renderDataset(ds)
            }
        )

        // using compile time KType, convert this RDD to Dataset and render it
        notebook.renderersProcessor.registerWithoutOptimizing(
            createRendererByCompileTimeType<RDD<*>> {
                if (spark == null) return@createRendererByCompileTimeType it.value.toString()

                val rdd = it.value as RDD<*>
                val type = it.type.arguments.first().type!!
                val ds = spark!!.createDataset(rdd, kotlinEncoderFor(type))
                renderDataset(ds)
            }
        )

        onLoadedAlsoDo()
    }

    private fun renderDataset(it: Dataset<*>): MimeTypedResult =
        with(properties) {
//            val showFunction = Dataset::class
//                .memberFunctions
//                .firstOrNull { it.name == "showString" && it.valueParameters.size == 3 }
//
//            textResult(
//                if (showFunction != null) {
//                    showFunction.call(it, displayLimit, displayTruncate, false) as String
//                } else {
//                    // if the function cannot be called, make sure it will call println instead
//                    it.show(displayLimit, displayTruncate)
//                    ""
//                }
//            )

            // don't actually render, instead use `show()`, which calls System.out
            it.show(displayLimit, displayTruncate)
            textResult("")
        }


    // TODO wip
    private fun generateSparkifyClass(klass: KClass<*>): Code {
//        val name = "`${klass.simpleName!!}${'$'}Generated`"
        val name = klass.simpleName
        val constructorArgs = klass.primaryConstructor!!.parameters
        val visibility = klass.visibility?.name?.lowercase() ?: ""
        val memberProperties = klass.memberProperties

        val properties = constructorArgs.associateWith {
            memberProperties.first { it.name == it.name }
        }

        val constructorParamsCode = properties.entries.joinToString("\n") { (param, prop) ->
            // TODO check override
            if (param.isOptional) TODO()
            val modifier = if (prop is KMutableProperty<*>) "var" else "val"
            val paramVisiblity = prop.visibility?.name?.lowercase() ?: ""
            val columnName = param.findAnnotation<ColumnName>()?.name ?: param.name!!

            "|     @get:kotlin.jvm.JvmName(\"$columnName\") $paramVisiblity $modifier ${param.name}: ${param.type},"
        }

        val productElementWhenParamsCode = properties.entries.joinToString("\n") { (param, _) ->
            "|        ${param.index} -> this.${param.name}"
        }

        @Language("kotlin")
        val code = """
            |$visibility data class $name(
            $constructorParamsCode
            |): scala.Product, java.io.Serializable {
            |    override fun canEqual(that: Any?): Boolean = that is $name
            |    override fun productArity(): Int = ${constructorArgs.size}
            |    override fun productElement(n: Int): Any = when (n) {
            $productElementWhenParamsCode
            |        else -> throw IndexOutOfBoundsException()
            |    }
            |}
        """.trimMargin()
        return code
    }
}
