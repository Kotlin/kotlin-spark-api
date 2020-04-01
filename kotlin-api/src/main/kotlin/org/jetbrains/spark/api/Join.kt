package org.jetbrains.spark.api

import org.apache.spark.sql.SparkSession

data class Left(val id: Int, val name: String)
data class Right(val id: Int, val value: Int)

private val spark: SparkSession = SparkSession
        .builder()
        .master("local[2]")
//                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//        .config("spark.sql.codegen.wholeStage", false)
        .appName("Simple Application").orCreate

fun main() {


    val first = spark.toDS(listOf(Left(1, "a"), Left(2, "b")))
    val second = spark.toDS(listOf(Right(1, 100), Right(3, 300)))
    first
            .leftJoin(second, first.col("id").eq(second.col("id")))
            .debugCodegen()
            .show()

    spark.stop()
}
