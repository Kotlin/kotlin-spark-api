package org.jetbrains.kotlinx.spark.api.jupyter

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put

interface Properties : MutableMap<String, String?> {

    companion object {
        internal const val sparkPropertiesName = "sparkProperties"

        internal const val sparkMasterName = "spark.master"
        internal const val appNameName = "spark.app.name"
        internal const val sparkName = "spark"
        internal const val scalaName = "scala"
        internal const val versionName = "v"
        internal const val displayLimitName = "display.limit"
        internal const val displayTruncateName = "display.truncate"
    }

//    val sparkMaster: String
//        get() = this[sparkMasterName] ?: "local[*]"
//
//    val appName: String
//        get() = this[appNameName] ?: "Jupyter"

    var displayLimit: Int
        set(value) { this[displayLimitName] = value.toString() }
        get() = this[displayLimitName]!!.toIntOrNull() ?: 20

    var displayTruncate: Int
        set(value) { this[displayTruncateName] = value.toString() }
        get() = this[displayTruncateName]?.toIntOrNull() ?: 30


    operator fun invoke(block: Properties.() -> Unit): Properties = apply(block)
}
