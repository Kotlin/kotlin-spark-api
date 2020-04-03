package org.jetbrains.spark.api.examples

import org.apache.spark.api.java.function.ReduceFunction
import org.apache.spark.sql.SparkSession
import org.jetbrains.spark.api.*

data class Q<T>(val id: Int, val text: T)
object Main {

    @OptIn(ExperimentalStdlibApi::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val spark = SparkSession
                .builder()
                .master("local[2]")
                .appName("Simple Application").orCreate

        val triples = spark
                .toDS(listOf(Q(1, 1 to null), Q(2, 2 to "22"), Q(3, 3 to "333")))
                .map { (a, b) -> a + b.first to b.second?.length }
                .map { it to 1 }
                .map { (a, b) -> Triple(a.first, a.second, b) }


        val pairs = spark
                .toDS(listOf(2 to "ад", 4 to "луна", 6 to "ягодка"))

        triples
                .leftJoin(pairs, triples.col("first").multiply(2).eq(pairs.col("first")))
//                .also { it.printSchema() }
                .map { (triple, pair) -> Five(triple.first, triple.second, triple.third, pair?.first, pair?.second) }
                .groupByKey { it.a }
                .reduceGroups(ReduceFunction { v1, v2 -> v1.copy(a = v1.a + v2.a, b = v1.a + v2.a) })
                .map { it._2 }
                .repartition(1)
//                .also { it.printSchema() }
//                .debugCodegen()
                .show()

        spark.stop()
    }

    data class Five<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
}

inline fun withSpark(func: SparkSession.() -> Unit): SparkSession {
    return SparkSession
            .builder()
            .master("local[2]")
            .appName("Simple Application")
            .orCreate
            .apply(func)
            .also { it.stop() }
}
