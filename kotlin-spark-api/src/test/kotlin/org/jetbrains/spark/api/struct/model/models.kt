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
package org.jetbrains.spark.api.struct.model

import com.beust.klaxon.*

private fun <T> Klaxon.convert(k: kotlin.reflect.KClass<*>, fromJson: (JsonValue) -> T, toJson: (T) -> String, isUnion: Boolean = false) =
        this.converter(object : Converter {
            @Suppress("UNCHECKED_CAST")
            override fun toJson(value: Any) = toJson(value as T)

            override fun fromJson(jv: JsonValue) = fromJson(jv) as Any
            override fun canConvert(cls: Class<*>) = cls == k.java || (isUnion && cls.superclass == k.java)
        })

private val klaxon = Klaxon()
        .convert(JsonObject::class, { it.obj!! }, { it.toJsonString() })
        .convert(DataType::class, { DataType.fromJson(it) }, { it.toJson() }, true)
        .convert(ElementType::class, { ElementType.fromJson(it) }, { it.toJson() }, true)

data class Struct(
        val type: String,
        val fields: List<StructField>? = null,
        val containsNull: Boolean? = null,
        val elementType: ElementType? = null
) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<Struct>(json)
    }
}

data class StructField(
        val name: String,
        val type: DataType,
        val nullable: Boolean,
        val metadata: Metadata
)

typealias Metadata = JsonObject

sealed class DataType {
    data class StructType(val value: Struct) : DataType()
    data class TypeName(val value: String) : DataType()

    public fun toJson(): String = klaxon.toJsonString(when (this) {
        is StructType -> this.value
        is TypeName -> this.value
    })

    companion object {
        public fun fromJson(jv: JsonValue): DataType = when (jv.inside) {
            is JsonObject -> StructType(jv.obj?.let { klaxon.parseFromJsonObject<Struct>(it) }!!)
            is String -> TypeName(jv.string!!)
            else -> throw IllegalArgumentException()
        }
    }
}

sealed class ElementType {
    data class SimpleElement(val value: String) : ElementType()
    data class ComplexElement(val value: Struct) : ElementType()

    public fun toJson(): String = klaxon.toJsonString(when (this) {
        is SimpleElement -> this.value
        is ComplexElement -> this.value
    })

    companion object {
        public fun fromJson(jv: JsonValue): ElementType = when (jv.inside) {
            is JsonObject -> ComplexElement(jv.obj?.let { klaxon.parseFromJsonObject<Struct>(it) }!!)
            is String -> SimpleElement(jv.string!!)
            else -> throw IllegalArgumentException()
        }
    }

}

