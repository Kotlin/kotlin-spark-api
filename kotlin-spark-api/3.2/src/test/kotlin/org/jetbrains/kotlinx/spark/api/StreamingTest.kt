package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import org.apache.spark.streaming.Duration
import java.io.Serializable
import org.jetbrains.kotlinx.spark.api.*
import java.util.LinkedList


class StreamingTest : ShouldSpec({
    context("streaming") {
        should("stream") {

            val input = listOf("aaa", "bbb", "aaa", "ccc")

            val results = object : Serializable {
                @Volatile
                var counter = 0
            }

            withSparkStreaming(Duration(10), timeout = 1000) {
                val resultsBroadcast = spark.broadcast(results)

                val rdd = sc.parallelize(input)
                val queue = LinkedList(listOf(rdd))

                val inputStream = ssc.queueStream(queue)

                inputStream.foreachRDD { rdd, _ ->
                    rdd.toDS().forEach {
                        it shouldBeIn input
                        resultsBroadcast.value.counter++
                    }
                }
            }
            results.counter shouldBe input.size

        }
    }
})