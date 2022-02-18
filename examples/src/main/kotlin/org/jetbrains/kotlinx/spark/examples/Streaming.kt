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

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.jetbrains.kotlinx.spark.api.withSpark
import scala.Tuple2
import java.io.Serializable

data class Row @JvmOverloads constructor(
    var word: String = "",
) : Serializable

fun main() = withSpark {

    val context = JavaStreamingContext(
        SparkConf()
            .setMaster("local[*]")
            .setAppName("Test"),
        Durations.seconds(1),
    )

    val lines = context.socketTextStream("localhost", 9999)

    val words = lines.flatMap { it.split(" ").iterator() }

    words.foreachRDD { rdd, time ->

        // todo convert rdd to dataset using kotlin data class?

        val rowRdd = rdd.map { Row(it) }

        val dataframe = spark.createDataFrame(rowRdd, Row::class.java)


    }


    context.start()
    context.awaitTermination()
}