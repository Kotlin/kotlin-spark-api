/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 3.2+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2022 JetBrains
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
package org.jetbrains.kotlinx.spark.api.jupyter

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.apache.spark.SparkException
import org.apache.spark.api.java.JavaRDDLike
import org.apache.spark.sql.Dataset
import org.apache.spark.unsafe.array.ByteArrayMethods
import org.jetbrains.kotlinx.spark.api.asKotlinIterable
import org.jetbrains.kotlinx.spark.api.asKotlinIterator
import org.jetbrains.kotlinx.spark.api.asKotlinList
import scala.Product
import java.io.InputStreamReader
import java.io.Serializable

private fun createHtmlTable(fillTable: TABLE.() -> Unit): String = buildString {
    appendHTML().head {
        style("text/css") {
            unsafe {
                val resource = "/table.css"
                val res = SparkIntegration::class.java
                    .getResourceAsStream(resource) ?: error("Resource '$resource' not found")
                val readRes = InputStreamReader(res).readText()
                raw("\n" + readRes)
            }
        }
    }

    appendHTML().table("dataset", fillTable)
}


internal fun <T> JavaRDDLike<T, *>.toHtml(limit: Int = 20, truncate: Int = 30): String = try {
    createHtmlTable {
        val numRows = limit.coerceIn(0 until ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH)
        val tmpRows = take(numRows).toList()

        val hasMoreData = tmpRows.size - 1 > numRows
        val rows = tmpRows.take(numRows)

        tr { th { +"Values" } }

        for (row in rows) tr {
            td {
                val string = when (row) {
                    is ByteArray -> row.joinToString(prefix = "[", postfix = "]") { "%02X".format(it) }

                    is CharArray -> row.iterator().asSequence().toList().toString()
                    is ShortArray -> row.iterator().asSequence().toList().toString()
                    is IntArray -> row.iterator().asSequence().toList().toString()
                    is LongArray -> row.iterator().asSequence().toList().toString()
                    is FloatArray -> row.iterator().asSequence().toList().toString()
                    is DoubleArray -> row.iterator().asSequence().toList().toString()
                    is BooleanArray -> row.iterator().asSequence().toList().toString()
                    is Array<*> -> row.iterator().asSequence().toList().toString()
                    is Iterable<*> -> row.iterator().asSequence().toList().toString()
                    is scala.collection.Iterable<*> -> row.asKotlinIterable().iterator().asSequence().toList().toString()
                    is Iterator<*> -> row.asSequence().toList().toString()
                    is scala.collection.Iterator<*> -> row.asKotlinIterator().asSequence().toList().toString()
                    is Product -> row.productIterator().asKotlinIterator().asSequence().toList().toString()
                    is Serializable -> row.toString()
                    // maybe others?

                    is Any? -> row.toString()
                    else -> row.toString()
                }

                +string.let {
                    if (truncate > 0 && it.length > truncate) {
                        // do not show ellipses for strings shorter than 4 characters.
                        if (truncate < 4) it.substring(0, truncate)
                        else it.substring(0, truncate - 3) + "..."
                    } else {
                        it
                    }
                }
            }
        }

        if (hasMoreData) tr {
            +"only showing top $numRows ${if (numRows == 1) "row" else "rows"}"
        }
    }
} catch (e: SparkException) {
    // Whenever toString() on the contents doesn't work, since the class might be unknown...
    """${toString()}
        |Cannot render this RDD of this class.""".trimMargin()
}

internal fun <T> Dataset<T>.toHtml(limit: Int = 20, truncate: Int = 30): String = createHtmlTable {
    val numRows = limit.coerceIn(0 until ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH)
    val tmpRows = getRows(numRows, truncate).asKotlinList().map { it.asKotlinList() }

    val hasMoreData = tmpRows.size - 1 > numRows
    val rows = tmpRows.take(numRows + 1)

    tr {
        for (header in rows.first()) th {
            +header.let {
                if (truncate > 0 && it.length > truncate) {
                    // do not show ellipses for strings shorter than 4 characters.
                    if (truncate < 4) it.substring(0, truncate)
                    else it.substring(0, truncate - 3) + "..."
                } else {
                    it
                }
            }
        }
    }

    for (row in rows.drop(1)) tr {
        for (item in row) td {
            +item
        }
    }

    if (hasMoreData) tr {
        +"only showing top $numRows ${if (numRows == 1) "row" else "rows"}"
    }
}
