package org.jetbrains.spark.api.examples

import org.apache.spark.sql.Dataset
import org.jetbrains.spark.api.*

const val MEANINGFUL_WORD_LENGTH = 4

fun main() {
    withSpark {
        spark
                .read()
                .textFile("/home/finkel/voina-i-mir.txt")
                .map { it.split(Regex("\\s")) }
                .flatten()
                .clear()
                .groupByKey { it }
                .mapGroups { k, iter -> k to iter.asSequence().count() }
                .sort { arrayOf(it.col("second").desc()) }
                .limit(20)
                .map { it.second to it.first }
                .show(false)

    }
}

fun Dataset<String>.clear() =
        filter { it.isNotBlank() }
                .map { it.trim(',', ' ', '\n', ':', '.', ';', '?', '!', '"', '\'', '\t', '　') }
                .filter { it.length >= MEANINGFUL_WORD_LENGTH }