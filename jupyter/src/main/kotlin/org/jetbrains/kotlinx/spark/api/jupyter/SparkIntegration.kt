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
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost
import org.jetbrains.kotlinx.jupyter.api.VariableDeclaration
import org.jetbrains.kotlinx.jupyter.api.declare
import kotlin.reflect.typeOf

/**
 * %use spark
 */
@Suppress("UNUSED_VARIABLE", "LocalVariableName")
@OptIn(ExperimentalStdlibApi::class)
internal class SparkIntegration : Integration() {

    private lateinit var sparkConf: Map<String, Any?>
    private val sparkProps = "SPARK_PROPS"

    @Suppress("UNCHECKED_CAST")
    override fun Builder.onLoadedAlsoDo() {
        sparkConf = notebook
            .variablesState[sparkProps]
            ?.value
            ?.getOrNull() as? Map<String, Any?>? ?: emptyMap()
    }

    override fun KotlinKernelHost.onLoaded() {
        val _0 = execute("""%dumpClassesForSpark""")

        @Language("kts")
        val _1 = listOf(
            """
                val spark = org.jetbrains.kotlinx.spark.api.SparkSession
                    .builder()
                    .master(SparkConf().get("spark.master", "local[*]"))
                    .appName("Jupyter")
                    .config("spark.sql.codegen.wholeStage", false)
                    .config("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem::class.java.name)
                    .config("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem::class.java.name)
                    .apply {
                        ${buildString {
                            sparkConf.forEach {
                                when (val value = it.value) {
                                    is String -> appendLine("config(\"${it.key}\", \"$value\")")
                                    is Boolean, Long, Double -> appendLine("config(\"${it.key}\", $value)")
                                    else -> throw IllegalArgumentException("Cannot set property ${it.key} because value $value of unsupported type ${value?.javaClass}")
                                }
                            }
                        }}
                     }
                    .getOrCreate()""".trimIndent(),
            """
                spark.sparkContext.setLogLevel(org.jetbrains.kotlinx.spark.api.SparkLogLevel.ERROR)""".trimIndent(),
            """
                val sc by lazy { 
                    org.apache.spark.api.java.JavaSparkContext(spark.sparkContext) 
                }""".trimIndent(),
            """
                println("Spark session (Spark: $sparkVersion, Scala: $scalaCompatVersion, v: $version)  has been started and is running. No `withSpark { }` necessary, you can access `spark` and `sc` directly. To use Spark streaming, use `%use spark-streaming` instead.")""".trimIndent(),
            """
                inline fun <reified T> List<T>.toDS(): Dataset<T> = toDS(spark)""".trimIndent(),
            """
                inline fun <reified T> List<T>.toDF(vararg colNames: String): Dataset<Row> = toDF(spark, *colNames)""".trimIndent(),
            """
                inline fun <reified T> Array<T>.toDS(): Dataset<T> = toDS(spark)""".trimIndent(),
            """
                inline fun <reified T> Array<T>.toDF(vararg colNames: String): Dataset<Row> = toDF(spark, *colNames)""".trimIndent(),
            """
                inline fun <reified T> dsOf(vararg arg: T): Dataset<T> = spark.dsOf(*arg)""".trimIndent(),
            """
                inline fun <reified T> dfOf(vararg arg: T): Dataset<Row> = spark.dfOf(*arg)""".trimIndent(),
            """
                inline fun <reified T> emptyDataset(): Dataset<T> = spark.emptyDataset(encoder<T>())""".trimIndent(),
            """
                inline fun <reified T> dfOf(colNames: Array<String>, vararg arg: T): Dataset<Row> = spark.dfOf(colNames, *arg)""".trimIndent(),
            """
                inline fun <reified T> RDD<T>.toDS(): Dataset<T> = toDS(spark)""".trimIndent(),
            """
                inline fun <reified T> JavaRDDLike<T, *>.toDS(): Dataset<T> = toDS(spark)""".trimIndent(),
            """
                inline fun <reified T> RDD<T>.toDF(vararg colNames: String): Dataset<Row> = toDF(spark, *colNames)""".trimIndent(),
            """
                inline fun <reified T> JavaRDDLike<T, *>.toDF(vararg colNames: String): Dataset<Row> = toDF(spark, *colNames)""".trimIndent(),
            """
                val udf: UDFRegistration get() = spark.udf()""".trimIndent(),
        ).map(::execute)
    }

    override fun KotlinKernelHost.onShutdown() {
        execute("""spark.stop()""")
    }
}
