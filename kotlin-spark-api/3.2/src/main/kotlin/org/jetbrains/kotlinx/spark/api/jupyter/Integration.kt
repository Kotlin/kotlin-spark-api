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

import org.apache.spark.SparkConf
import org.jetbrains.kotlinx.jupyter.api.VariableDeclaration
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import org.jetbrains.kotlinx.spark.api.*
import org.jetbrains.kotlinx.spark.api.setLogLevel
import kotlin.reflect.*
import org.jetbrains.kotlinx.spark.api.sparkContext

@OptIn(ExperimentalStdlibApi::class)
internal class Integration : JupyterIntegration() {

    private val kotlinVersion = "1.5.30"
    private val scalaCompatVersion = "2.12"
    private val scalaVersion = "2.12.15"
    private val spark3Version = "3.2.1"

    override fun Builder.onLoaded() {


        dependencies(
            "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion",
            "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion",
            "org.apache.spark:spark-sql_$scalaCompatVersion:$spark3Version",
            "org.apache.spark:spark-streaming_$scalaCompatVersion:$spark3Version",
            "org.apache.spark:spark-mllib_$scalaCompatVersion:$spark3Version",
            "org.apache.spark:spark-sql_$scalaCompatVersion:$spark3Version",
            "org.apache.spark:spark-repl_$scalaCompatVersion:$spark3Version",
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

        import("org.jetbrains.kotlinx.spark.api.*")
        import("org.apache.spark.sql.functions.*")
        import("org.apache.spark.*")
        import("org.apache.spark.sql.*")
        import("org.apache.spark.sql.SparkSession.Builder")
        import("scala.collection.Seq")

        // starting spark and unwrapping KSparkContext functions
        onLoaded {
            execute("""println("Running!!")""")
            execute(
                """|val spark = org.apache.spark.sql.SparkSession
                   |    .builder()
                   |    .master(SparkConf().get("spark.master", "local[*]"))
                   |    .appName("Jupyter")
                   |    .getOrCreate()""".trimMargin()
            )
//            execute("""spark.sparkContext.setLogLevel(SparkLogLevel.ERROR)""")
//            execute("""val sc: JavaSparkContext by lazy { JavaSparkContext(spark.sparkContext) }""")
//            val spark = org.apache.spark.sql.SparkSession
//                .builder()
//                .master(SparkConf().get("spark.master", "local[*]"))
//                .appName("Jupyter")
//                .getOrCreate()
//            spark.sparkContext.setLogLevel(SparkLogLevel.ERROR)

//            declare(
//                listOf(VariableDeclaration(
//                    name = "spark",
//                    value = spark,
//                    type = typeOf<org.apache.spark.sql.SparkSession>(),
//                ))
//            )
        }
    }
}
