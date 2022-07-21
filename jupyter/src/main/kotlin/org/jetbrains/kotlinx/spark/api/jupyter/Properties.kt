package org.jetbrains.kotlinx.spark.api.jupyter

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put

@Serializable
data class Properties(
    val displayLimit: Int = 20,
    val displayTruncate: Int = 30,
) {
    companion object {
        fun from(map: Map<String, String?>): Properties = Json.decodeFromJsonElement(
            buildJsonObject { map.forEach(::put) }
        )
    }
}