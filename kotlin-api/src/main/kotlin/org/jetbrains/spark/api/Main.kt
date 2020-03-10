package org.jetbrains.spark.api

import org.apache.spark.sql.Encoders
import org.apache.spark.sql.SparkSession
import java.lang.StringBuilder

object Main {
    data class Q<T>(val id: Int, val text: T)

    @JvmStatic
    fun main(args: Array<String>) {
        println("hello")
        val logFile = "/Users/vitaly.khudobakhshov/Documents/scaladays2019.txt"
        val spark = SparkSession
                .builder()
                .master("local[2]")
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .appName("Simple Application").orCreate

//        val logData = spark.read().textFile(logFile).cache()
//
//        val numAs = logData.org.jetbrains.spark.api.filter { s -> s.contains("a") }.count()
//        val numBs = logData.org.jetbrains.spark.api.filter { s -> s.contains("b") }.count()
//
//        println("Lines with a: $numAs, lines with b: $numBs")

        val list = listOf(Q(1, "1"), Q(2, "22"), Q(3, StringBuilder("333")))
        val ds = spark
                .toDS(list)
                .map { (a, b) -> a to b.length }
                .map { it to 1 }
                .map { (a, b) -> Triple(a.first, a.second, b) }
                .map { (a, b, c) -> a + b + c }
                .forEach { println(it) }
//
//        println("TEST >>> $ds")
//        val enc = KotlinEncoder.bean(Pair::class.java)
//        val enc = Encoders.kryo(Pair::class.java)

//        println(">>>  CT=" + enc.clsTag())
//        println(">>>  SC=" + enc.schema())

//        val jsc = JavaSparkContext(spark.sparkContext())
//        val rdd = jsc.parallelize(listOf(Q(1, "hello"), Q(2, "world")))
//        println(rdd.take(1))
        spark.stop()
    }
}