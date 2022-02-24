/*-
 * =LICENSE=
 * Kotlin Spark API
 * ----------
 * Copyright (C) 2019 - 2020 JetBrains
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

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaRDDLike
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession.Builder
import org.apache.spark.sql.UDFRegistration
import org.jetbrains.kotlinx.spark.api.SparkLogLevel.ERROR

/**
 * Wrapper for spark creation which allows setting different spark params.
 *
 * @param props spark options, value types are runtime-checked for type-correctness
 * @param master Sets the Spark master URL to connect to, such as "local" to run locally, "local[4]" to
 *  run locally with 4 cores, or "spark://master:7077" to run on a Spark standalone cluster. By default, it
 *  tries to get the system value "spark.master", otherwise it uses "local[*]"
 * @param appName Sets a name for the application, which will be shown in the Spark web UI.
 *  If no application name is set, a randomly generated name will be used.
 * @param logLevel Control our logLevel. This overrides any user-defined log settings.
 * @param func function which will be executed in context of [KSparkSession] (it means that `this` inside block will point to [KSparkSession])
 */
@JvmOverloads
inline fun withSpark(
    props: Map<String, Any> = emptyMap(),
    master: String = SparkConf().get("spark.master", "local[*]"),
    appName: String = "Kotlin Spark Sample",
    logLevel: SparkLogLevel = ERROR,
    func: KSparkSession.() -> Unit,
) {
    val builder = SparkSession
        .builder()
        .master(master)
        .appName(appName)
        .apply {
            props.forEach {
                when (val value = it.value) {
                    is String -> config(it.key, value)
                    is Boolean -> config(it.key, value)
                    is Long -> config(it.key, value)
                    is Double -> config(it.key, value)
                    else -> throw IllegalArgumentException("Cannot set property ${it.key} because value $value of unsupported type ${value::class}")
                }
            }
        }
    withSpark(builder, logLevel, func)

}

/**
 * Wrapper for spark creation which allows setting different spark params.
 *
 * @param builder A [SparkSession.Builder] object, configured how you want.
 * @param logLevel Control our logLevel. This overrides any user-defined log settings.
 * @param func function which will be executed in context of [KSparkSession] (it means that `this` inside block will point to [KSparkSession])
 */
@JvmOverloads
inline fun withSpark(builder: Builder, logLevel: SparkLogLevel = ERROR, func: KSparkSession.() -> Unit) {
    builder
        .getOrCreate()
        .apply {
            KSparkSession(this).apply {
                sparkContext.setLogLevel(logLevel)
                func()
                spark.stop()
            }
        }
}

/**
 * Wrapper for spark creation which copies params from [sparkConf].
 *
 * @param sparkConf Sets a list of config options based on this.
 * @param logLevel Control our logLevel. This overrides any user-defined log settings.
 * @param func function which will be executed in context of [KSparkSession] (it means that `this` inside block will point to [KSparkSession])
 */
@JvmOverloads
inline fun withSpark(sparkConf: SparkConf, logLevel: SparkLogLevel = ERROR, func: KSparkSession.() -> Unit) {
    withSpark(
        builder = SparkSession.builder().config(sparkConf),
        logLevel = logLevel,
        func = func,
    )
}

/**
 * This wrapper over [SparkSession] which provides several additional methods to create [org.apache.spark.sql.Dataset]
 */
class KSparkSession(val spark: SparkSession) {

    val sc: JavaSparkContext by lazy { JavaSparkContext(spark.sparkContext) }

    inline fun <reified T> List<T>.toDS() = toDS(spark)
    inline fun <reified T> Array<T>.toDS() = spark.dsOf(*this)
    inline fun <reified T> dsOf(vararg arg: T) = spark.dsOf(*arg)
    inline fun <reified T> RDD<T>.toDS() = toDS(spark)
    inline fun <reified T> JavaRDDLike<T, *>.toDS() = toDS(spark)
    val udf: UDFRegistration get() = spark.udf()
}
