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

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.stat.ChiSquareTest
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.ml.stat.Summarizer.*
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.col
import org.jetbrains.kotlinx.spark.api.KSparkSession
import org.jetbrains.kotlinx.spark.api.to
import org.jetbrains.kotlinx.spark.api.tuples.t
import org.jetbrains.kotlinx.spark.api.tuples.tupleOf
import org.jetbrains.kotlinx.spark.api.withSpark


fun main() = withSpark {
    // https://spark.apache.org/docs/latest/ml-statistics.html
    correlation()
    chiSquare()
    summarizer()

    // https://spark.apache.org/docs/latest/ml-pipeline.html
    estimatorTransformerParam()
    pipeline()
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

private fun KSparkSession.estimatorTransformerParam() {
    println("Estimator, Transformer, and Param")

    // Prepare training data from a list of (label, features) tuples.
    val training = listOf(
        t(1.0, Vectors.dense(0.0, 1.1, 0.1)),
        t(0.0, Vectors.dense(2.0, 1.0, -1.0)),
        t(0.0, Vectors.dense(2.0, 1.3, 1.0)),
        t(1.0, Vectors.dense(0.0, 1.2, -0.5))
    ).toDF("label", "features")

    // Create a LogisticRegression instance. This instance is an Estimator.
    val lr = LogisticRegression()

    // Print out the parameters, documentation, and any default values.
    println("LogisticRegression parameters:\n ${lr.explainParams()}\n")

    // We may set parameters using setter methods.
    lr.apply {
        maxIter = 10
        regParam = 0.01
    }

    // Learn a LogisticRegression model. This uses the parameters stored in lr.
    val model1 = lr.fit(training)
    // Since model1 is a Model (i.e., a Transformer produced by an Estimator),
    // we can view the parameters it used during fit().
    // This prints the parameter (name: value) pairs, where names are unique IDs for this
    // LogisticRegression instance.
    println("Model 1 was fit using parameters: ${model1.parent().extractParamMap()}")

    // We may alternatively specify parameters using a ParamMap.
    val paramMap = ParamMap()
        .put(lr.maxIter().w(20)) // Specify 1 Param.
        .put(lr.maxIter(), 30) // This overwrites the original maxIter.
        .put(lr.regParam().w(0.1), lr.threshold().w(0.55)) // Specify multiple Params.

    // One can also combine ParamMaps.
    val paramMap2 = ParamMap()
        .put(lr.probabilityCol().w("myProbability")) // Change output column name

    val paramMapCombined = paramMap.`$plus$plus`(paramMap2)

    // Now learn a new model using the paramMapCombined parameters.
    // paramMapCombined overrides all parameters set earlier via lr.set* methods.
    val model2: LogisticRegressionModel = lr.fit(training, paramMapCombined)
    println("Model 2 was fit using parameters: ${model2.parent().extractParamMap()}")

    // Prepare test documents.
    val test = listOf(
        t(1.0, Vectors.dense(-1.0, 1.5, 1.3)),
        t(0.0, Vectors.dense(3.0, 2.0, -0.1)),
        t(1.0, Vectors.dense(0.0, 2.2, -1.5)),
    ).toDF("label", "features")

    // Make predictions on test documents using the Transformer.transform() method.
    // LogisticRegression.transform will only use the 'features' column.
    // Note that model2.transform() outputs a 'myProbability' column instead of the usual
    // 'probability' column since we renamed the lr.probabilityCol parameter previously.
    val results = model2.transform(test)
    val rows = results.select("features", "label", "myProbability", "prediction")
    for (r: Row in rows.collectAsList())
        println("(${r[0]}, ${r[1]}) -> prob=${r[2]}, prediction=${r[3]}")

    println()
}

private fun KSparkSession.pipeline() {
    println("Pipeline:")
// Prepare training documents from a list of (id, text, label) tuples.
    val training = listOf(
        t(0L, "a b c d e spark", 1.0),
        t(1L, "b d", 0.0),
        t(2L, "spark f g h", 1.0),
        t(3L, "hadoop mapreduce", 0.0)
    ).toDF("id", "text", "label")

    // Configure an ML pipeline, which consists of three stages: tokenizer, hashingTF, and lr.
    val tokenizer = Tokenizer()
        .setInputCol("text")
        .setOutputCol("words")
    val hashingTF = HashingTF()
        .setNumFeatures(1000)
        .setInputCol(tokenizer.outputCol)
        .setOutputCol("features")
    val lr = LogisticRegression()
        .setMaxIter(10)
        .setRegParam(0.001)
    val pipeline = Pipeline()
        .setStages(
            arrayOf(
                tokenizer,
                hashingTF,
                lr,
            )
        )

    // Fit the pipeline to training documents.
    val model = pipeline.fit(training)

    // Now we can optionally save the fitted pipeline to disk
    model.write().overwrite().save("/tmp/spark-logistic-regression-model")

    // We can also save this unfit pipeline to disk
    pipeline.write().overwrite().save("/tmp/unfit-lr-model")

    // And load it back in during production
    val sameModel = PipelineModel.load("/tmp/spark-logistic-regression-model")

    // Prepare test documents, which are unlabeled (id, text) tuples.
    val test = listOf(
        t(4L, "spark i j k"),
        t(5L, "l m n"),
        t(6L, "spark hadoop spark"),
        t(7L, "apache hadoop"),
    ).toDF("id", "text")

    // Make predictions on test documents.
    val predictions = model.transform(test)
        .select("id", "text", "probability", "prediction")
        .collectAsList()

    for (r in predictions)
        println("(${r[0]}, ${r[1]}) --> prob=${r[2]}, prediction=${r[3]}")

    println()
}