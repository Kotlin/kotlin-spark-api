/*-
 * =LICENSE=
 * Kotlin Spark API: Examples for Spark 3.2+ (Scala 2.12)
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
package org.jetbrains.kotlinx.spark.examples

import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.linalg.VectorUDT
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.Row
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType
import org.jetbrains.kotlinx.spark.api.showDS
import org.jetbrains.kotlinx.spark.api.tuples.*
import org.jetbrains.kotlinx.spark.api.withSpark


fun main() = withSpark {
    val data = listOf(
        Vectors.sparse(4, intArrayOf(0, 3), doubleArrayOf(1.0, -2.0)),
        Vectors.dense(4.0, 5.0, 0.0, 3.0),
        Vectors.dense(6.0, 7.0, 0.0, 8.0),
        Vectors.sparse(4, intArrayOf(0, 3), doubleArrayOf(9.0, 1.0))
    ).map(::tupleOf)

    val df = data.toDS()

    val r1: Matrix = Correlation.corr(df, "_1").head().getAs(0)
    println(
        """
        |Pearson correlation matrix:
        |$r1
        |
        """.trimMargin()
    )

    val r2: Matrix = Correlation.corr(df, "_1", "spearman").head().getAs(0)
    println(
        """
        |Spearman correlation matrix:
        |$r2
        |
        """.trimMargin()
    )
}