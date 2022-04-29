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
import scala.collection.*
import org.jetbrains.kotlinx.spark.api.SparkSession
import scala.Product
import java.io.Serializable
import scala.collection.Iterable as ScalaIterable
import scala.collection.Iterator as ScalaIterator

@Suppress("UNUSED_VARIABLE", "LocalVariableName")
@OptIn(ExperimentalStdlibApi::class)
internal class SparkIntegration : JupyterIntegration() {

    private val kotlinVersion = "1.6.21"
    private val scalaCompatVersion = "2.12"
    private val scalaVersion = "2.12.15"
    private val spark3Version = "3.2.1"

    override fun Builder.onLoaded() {

        dependencies(
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

        println("SparkIntegration loaded")

        import(
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

        // onLoaded is only done for the non-streaming variant of kotlin-spark-api in the json file
        onLoaded {
            val _0 = execute("""%dumpClassesForSpark""")

            @Language("kts")
            val _1 = listOf(
                """
                val spark = org.jetbrains.kotlinx.spark.api.SparkSession
                    .builder()
                    .master(SparkConf().get("spark.master", "local[*]"))
                    .appName("Jupyter")
                    .config("spark.sql.codegen.wholeStage", false)
                    .config("spark.io.compression.codec", "snappy")
                    .getOrCreate()""".trimIndent(),
                """
                spark.sparkContext.setLogLevel(org.jetbrains.kotlinx.spark.api.SparkLogLevel.ERROR)""".trimIndent(),
                """
                val sc by lazy { 
                    org.apache.spark.api.java.JavaSparkContext(spark.sparkContext) 
                }""".trimIndent(),
                """
                println("Spark session has been started and is running. No `withSpark { }` necessary, you can access `spark` and `sc` directly. To use Spark streaming, use `%use kotlin-spark-api-streaming` instead.")""".trimIndent(),
                """
                inline fun <reified T> List<T>.toDS(): Dataset<T> = toDS(spark)""".trimIndent(),
                """
                inline fun <reified T> Array<T>.toDS(): Dataset<T> = spark.dsOf(*this)""".trimIndent(),
                """
                inline fun <reified T> dsOf(vararg arg: T): Dataset<T> = spark.dsOf(*arg)""".trimIndent(),
                """
                inline fun <reified T> RDD<T>.toDS(): Dataset<T> = toDS(spark)""".trimIndent(),
                """
                inline fun <reified T> JavaRDDLike<T, *>.toDS(): Dataset<T> = toDS(spark)""".trimIndent(),
                """
                inline fun <reified T> RDD<T>.toDF(): Dataset<Row> = toDF(spark)""".trimIndent(),
                """
                inline fun <reified T> JavaRDDLike<T, *>.toDF(): Dataset<Row> = toDF(spark)""".trimIndent(),
                """
                val udf: UDFRegistration get() = spark.udf()""".trimIndent(),
            ).map(::execute)
        }

        onShutdown {
            @Language("kts")
            val _0 = execute("""
                spark.stop()""".trimIndent()
            )
        }


        // Render Dataset
        render<Dataset<*>> {
            HTML(it.toHtml())
        }

        render<RDD<*>> {
            HTML(it.toJavaRDD().toHtml())
        }

        render<JavaRDDLike<*, *>> {
            HTML(it.toHtml())
        }
    }
}
