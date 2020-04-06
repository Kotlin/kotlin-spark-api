package org.jetbrains.spark.api

import org.apache.spark.sql.SparkSession

inline fun withSpark(props: Map<String, Any> = emptyMap(), master: String = "local[*]", appName: String = "Sample app", func: SparkSession.() -> Unit) {
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
            .also {
                it.sparkContext().setLogLevel("DEBUG")
            }
            .apply(func)
            .also { it.stop() }
}
