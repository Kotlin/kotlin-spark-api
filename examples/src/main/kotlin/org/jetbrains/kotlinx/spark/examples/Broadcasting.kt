package org.jetbrains.kotlinx.spark.examples

import org.jetbrains.kotlinx.spark.api.broadcast
import org.jetbrains.kotlinx.spark.api.map
import org.jetbrains.kotlinx.spark.api.sparkContext
import org.jetbrains.kotlinx.spark.api.withSpark
import java.io.Serializable

// (data) class must be Serializable to be broadcast
data class SomeClass(val a: IntArray, val b: Int) : Serializable

fun main() = withSpark {
    val broadcastVariable = spark.sparkContext.broadcast(SomeClass(a = intArrayOf(5, 6), b = 3))
    val result = listOf(1, 2, 3, 4, 5)
            .toDS()
            .map {
                val receivedBroadcast = broadcastVariable.value
                it + receivedBroadcast.a.first()
            }
            .collectAsList()

    println(result)
}