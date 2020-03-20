package org.jetbrains.spark.api

import org.apache.spark.sql.SparkSession

data class Q<T>(val id: Int, val text: T)
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
//        println("hello")
//        val logFile = "/Users/vitaly.khudobakhshov/Documents/scaladays2019.txt"
        val spark = SparkSession
                .builder()
                .master("local[2]")
//                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//                .config("spark.sql.codegen.wholeStage", false)
                .appName("Simple Application").orCreate

//        val logData = spark.read().textFile(logFile).cache()
//
//        val numAs = logData.org.jetbrains.spark.api.filter { s -> s.contains("a") }.count()
//        val numBs = logData.org.jetbrains.spark.api.filter { s -> s.contains("b") }.count()
//
//        println("Lines with a: $numAs, lines with b: $numBs")

//        val list = listOf(Q(1, "1"), Q(2, "22"), Q(3, "333"))
//
//        println("TEST >>> $ds")
//        val enc = KotlinEncoder.bean(Pair::class.java)
//        val enc = Encoders.kryo(Pair::class.java)
        val triples = spark
                .toDS(listOf(Q(1, 1 to "1"), Q(2, 2 to "22"), Q(3, 3 to "333")))
                .map { (a, b) -> a + b.first to b.second.length }
                .map { it to 1 }
                .map { (a, b) -> Triple(a.first, a.second, b) }


        val pairs = spark
                .toDS(listOf(2 to "ад", 4 to "луна", 6 to "ягодка"))

        triples
                .leftJoin(pairs, triples.col("first").multiply(2).eq(pairs.col("first")))
                .map { (triple, pair) -> Five(triple.first, triple.second, triple.third, pair?.first, pair?.second) }
                .forEach { println(it) }

//        println(">>>  CT=" + enc.clsTag())
//        println(">>>  SC=" + enc.schema())

//        val jsc = JavaSparkContext(spark.sparkContext())
//        val rdd = jsc.parallelize(listOf(Q(1, "hello"), Q(2, "world")))
//        println(rdd.take(1))
        spark.stop()
    }

    data class Five<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
}