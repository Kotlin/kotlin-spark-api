/*-
 * =LICENSE=
 * Kotlin Spark API: Examples
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
package org.jetbrains.spark.api.examples

import org.apache.spark.sql.Dataset
import org.jetbrains.spark.api.*

const val MEANINGFUL_WORD_LENGTH = 4

fun main() {
    withSpark {
        spark
                .read()
                .textFile(this::class.java.classLoader.getResource("voina-i-mir.txt")?.path)
                .map { it.split(Regex("\\s")) }
                .flatten()
                .cleanup()
                .groupByKey { it }
                .mapGroups { k, iter -> k to iter.asSequence().count() }
                .sort { arrayOf(it.col("second").desc()) }
                .limit(20)
                .map { it.second to it.first }
                .show(false)
    }
}

fun Dataset<String>.cleanup() =
        filter { it.isNotBlank() }
                .map { it.trim(',', ' ', '\n', ':', '.', ';', '?', '!', '"', '\'', '\t', '　') }
                .filter { it.length >= MEANINGFUL_WORD_LENGTH }
