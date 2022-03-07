package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.apache.spark.sql.execution.streaming.MemoryStream
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.streaming.Duration

class StreamingTest : ShouldSpec({
    context("streaming") {
        should("stream") {

            withSpark/*Streaming(Duration(1))*/ {
            // WIP this doesn't use ssc at all?

                val events = MemoryStream<Int>(100, spark.sqlContext(), null, encoder())
                val sessions = events.toDS()
                sessions.isStreaming shouldBe true

                val transformedSessions = sessions.map { (it * 2).toString() }

                val streamingQuery = transformedSessions
                    .writeStream()
                    .format("memory")
                    .queryName("test")
                    .outputMode(OutputMode.Append())
                    .start()

                val currentOffset = events.addData(listOf(1, 2, 3).asScalaIterable())
                streamingQuery.processAllAvailable()
                events.commit(currentOffset)

                spark.table("test")
                    .show(false)


            }


        }
    }
})