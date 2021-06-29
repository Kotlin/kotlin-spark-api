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


data class Left(val id: Int, val name: String)

data class Right(val id: Int, val value: Int)


fun main() {
    withSpark(logLevel = SparkLogLevel.INFO) {
        val first = dsOf(Left(1, "a"), Left(2, "b"))
        val second = dsOf(Right(1, 100), Right(3, 300))
        first
            .leftJoin(second, first.col("id").eq(second.col("id")))
            .debugCodegen()
            .also { it.show() }
            .map { c(it.first.id, it.first.name, it.second?.value) }
            .show()

    }
}

