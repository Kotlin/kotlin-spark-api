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
import org.apache.spark.sql.functions.*
import org.apache.spark.sql.Dataset
import org.apache.spark.unsafe.array.ByteArrayMethods
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.api.HTML
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import org.jetbrains.kotlinx.spark.api.KSparkSession
import org.jetbrains.kotlinx.spark.api.asKotlinList
import java.io.InputStreamReader

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
        import("org.apache.spark.api.java.*")
        import("org.apache.spark.sql.SparkSession.Builder")
        import("scala.collection.Seq")

        // starting spark and unwrapping KSparkContext functions
        onLoaded {
            println("Running!!")

            @Language("kts")
            val spark = execute(
                """
                val spark = org.jetbrains.kotlinx.spark.api.SparkSession
                    .builder()
                    .master(SparkConf().get("spark.master", "local[*]"))
                    .appName("Jupyter")
                    .getOrCreate()
                """.trimIndent()
            )

            @Language("kts")
            val logLevel = execute("""spark.sparkContext.setLogLevel(SparkLogLevel.ERROR)""")

            @Language("kts")
            val sc = execute("""val sc = org.apache.spark.api.java.JavaSparkContext(spark.sparkContext)""")


        }


        // Render Dataset
        render<Dataset<*>> {
            HTML(it.toHtml())
        }
    }
}


private fun <T> Dataset<T>.toHtml(limit: Int = 20, truncate: Int = 30): String = buildString {
    appendHTML().head {
        style("text/css") {
            unsafe {
                val resource = "/table.css"
                val res = Integration::class.java
                    .getResourceAsStream(resource) ?: error("Resource '$resource' not found")
                val readRes = InputStreamReader(res).readText()
                raw("\n" + readRes)
            }
        }
    }

    appendHTML().table("dataset") {
        val numRows = limit.coerceIn(0 until ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH)
        val tmpRows = getRows(numRows, truncate).asKotlinList().map { it.asKotlinList() }

        val hasMoreData = tmpRows.size - 1 > numRows
        val rows = tmpRows.take(numRows + 1)


        tr {
            for (header in rows.first()) th {
                +header.let {
                    if (truncate > 0 && it.length > truncate) {
                        // do not show ellipses for strings shorter than 4 characters.
                        if (truncate < 4) it.substring(0, truncate)
                        else it.substring(0, truncate - 3) + "..."
                    } else {
                        it
                    }
                }
            }
        }

        for (row in rows.drop(1)) tr {
            for (item in row) td {
                +item
            }
        }

        if (hasMoreData) tr {
            +"only showing top $numRows ${if (numRows == 1) "row" else "rows"}"
        }
    }

}
