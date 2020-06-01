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
package org.jetbrains.spark.api

import org.apache.spark.sql.SparkSession

/**
 * Wrapper for spark creation which allows to set different spark params
 *
 * @param props spark options, value types are runtime-checked for type-correctness
 * @param master [SparkSession.Builder.master]
 * @param appName [SparkSession.Builder.appName]
 * @param func function which will be executed in context of [KSparkSession] (it means that `this` inside block will point to [KSparkSession])
 */
inline fun withSpark(props: Map<String, Any> = emptyMap(), master: String = "local[*]", appName: String = "Kotlin Spark Sample", func: KSparkSession.() -> Unit) {
    SparkSession
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
            .orCreate
            .apply { KSparkSession(this).apply(func) }
            .also { it.stop() }
}

/**
 * This wrapper over [SparkSession] which provides several additional methods to create [org.apache.spark.sql.Dataset]
 */
@Suppress("EXPERIMENTAL_FEATURE_WARNING", "unused")
inline class KSparkSession(val spark: SparkSession) {
    inline fun <reified T> List<T>.toDS() = toDS(spark)
    inline fun <reified T> Array<T>.toDS() = spark.dsOf(*this)
    inline fun <reified T> dsOf(vararg arg: T) = spark.dsOf(*arg)
}
