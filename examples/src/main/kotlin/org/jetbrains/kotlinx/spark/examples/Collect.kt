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

import org.apache.spark.sql.Row
import org.jetbrains.kotlinx.spark.api.*
import org.jetbrains.kotlinx.spark.api.tuples.*

fun main() {
    withSpark {
        val sd = dsOf(1, 2, 3)
        sd.createOrReplaceTempView("ds")
        spark.sql("select * from ds")
            .withCached {
                println("asList: ${toList<Int>()}")
                println("asArray: ${toArray<Int>().contentToString()}")
                this
            }
            .to<Int>()
            .withCached {
                println("typed collect: " + (collect() as Array<Int>).contentToString())
                println("type collectAsList: " + collectAsList())
            }

        dsOf(1, 2, 3)
            .map { t(it, it + 1, it + 2) }
            .to<Row>()
            .select("_1")
            .collectAsList()
            .forEach { println(it) }
    }
}
