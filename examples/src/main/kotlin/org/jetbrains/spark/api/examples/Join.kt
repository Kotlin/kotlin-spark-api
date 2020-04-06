package org.jetbrains.spark.api.examples

import org.jetbrains.spark.api.*


data class Left(val id: Int, val name: String)

data class Right(val id: Int, val value: Int)


fun main() {
    withSpark {
        val first = dsOf(Left(1, "a"), Left(2, "b"))
        val second = dsOf(Right(1, 100), Right(3, 300))
        first
                .leftJoin(second, first.col("id").eq(second.col("id")))
                .debugCodegen()
                .also { it.show() }
                .map { c(it.first.id, it.first.name, it.second?.value) }
                .show()

    }
}

