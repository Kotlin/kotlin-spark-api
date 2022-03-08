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
package org.jetbrains.kotlinx.spark.examples

import org.jetbrains.kotlinx.spark.api.*

fun main() {
    withSpark(props = mapOf("spark.sql.codegen.wholeStage" to true)) {
        dsOf(
            mapOf(1 to c(1, 2, 3), 2 to c(1, 2, 3)),
            mapOf(3 to c(1, 2, 3), 4 to c(1, 2, 3)),
        )
            .flatMap { it.toList().map { p -> listOf(p.first, p.second._1, p.second._2, p.second._3) }.iterator() }
            .flatten()
            .map { c(it) }
            .also { it.printSchema() }
            .distinct()
            .sort("_1")
            .debugCodegen()
            .show()
    }
}

