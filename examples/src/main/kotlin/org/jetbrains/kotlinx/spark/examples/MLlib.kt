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

import org.apache.spark.*
import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.stat.ChiSquareTest
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.ml.stat.Summarizer
import org.apache.spark.ml.stat.Summarizer.*
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.col
import org.jetbrains.kotlinx.spark.api.*
import org.jetbrains.kotlinx.spark.api.tuples.*
import scala.collection.mutable.WrappedArray


fun main() = withSpark {
    correlation()
    chiSquare()
    summarizer()
}

private fun KSparkSession.correlation() {
    println("Correlation:")

    val data = listOf(
        Vectors.sparse(4, intArrayOf(0, 3), doubleArrayOf(1.0, -2.0)),
        Vectors.dense(4.0, 5.0, 0.0, 3.0),
        Vectors.dense(6.0, 7.0, 0.0, 8.0),
        Vectors.sparse(4, intArrayOf(0, 3), doubleArrayOf(9.0, 1.0))
    ).map(::tupleOf)

    val df = data.toDF("features")

    val r1 = Correlation.corr(df, "features").head().getAs<Matrix>(0)
    println(
        """
        |Pearson correlation matrix:
        |$r1
        |
        """.trimMargin()
    )

    val r2 = Correlation.corr(df, "features", "spearman").head().getAs<Matrix>(0)
    println(
        """
        |Spearman correlation matrix:
        |$r2
        |
        """.trimMargin()
    )
}

private fun KSparkSession.chiSquare() {
    println("ChiSquare:")

    val data = listOf(
        t(0.0, Vectors.dense(0.5, 10.0)),
        t(0.0, Vectors.dense(1.5, 20.0)),
        t(1.0, Vectors.dense(1.5, 30.0)),
        t(0.0, Vectors.dense(3.5, 30.0)),
        t(0.0, Vectors.dense(3.5, 40.0)),
        t(1.0, Vectors.dense(3.5, 40.0)),
    )

    // while df.getAs<Something>(0) works, it's often easier to just parse the result as a typed Dataset :)
    data class ChiSquareTestResult(
        val pValues: Vector,
        val degreesOfFreedom: List<Int>,
        val statistics: Vector,
    )

    val df: Dataset<Row> = data.toDF("label", "features")
    val chi = ChiSquareTest.test(df, "features", "label")
        .to<ChiSquareTestResult>()
        .head()

    println("pValues: ${chi.pValues}")
    println("degreesOfFreedom: ${chi.degreesOfFreedom}")
    println("statistics: ${chi.statistics}")
    println()
}

private fun KSparkSession.summarizer() {
    println("Summarizer:")

    val data = listOf(
        t(Vectors.dense(2.0, 3.0, 5.0), 1.0),
        t(Vectors.dense(4.0, 6.0, 7.0), 2.0)
    )

    val df = data.toDF("features", "weight")

    val result1 = df
        .select(
            metrics("mean", "variance")
                .summary(col("features"), col("weight")).`as`("summary")
        )
        .select("summary.mean", "summary.variance")
        .first()

    println("with weight: mean = ${result1.getAs<Vector>(0)}, variance = ${result1.getAs<Vector>(1)}")

    val result2 = df
        .select(
            mean(col("features")),
            variance(col("features")),
        )
        .first()

    println("without weight: mean = ${result2.getAs<Vector>(0)}, variance = ${result2.getAs<Vector>(1)}")
    println()
}