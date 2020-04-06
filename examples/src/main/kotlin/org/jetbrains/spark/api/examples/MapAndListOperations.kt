package org.jetbrains.spark.api.examples

import org.jetbrains.spark.api.*

fun main() {
    withSpark(props = mapOf("spark.sql.codegen.wholeStage" to false)) {
        dsOf(mapOf(1 to c(1, 2, 3), 2 to c(1, 2, 3)), mapOf(3 to c(1, 2, 3), 4 to c(1, 2, 3)))
                .flatMap { it.toList().map { p -> listOf(p.first, p.second.a, p.second.b, p.second.c) }.iterator() }
                .flatten()
                .map { c(it) }
                .also { it.printSchema() }
                .distinct()
                .sort("a")
                .debugCodegen()
                .show()
    }
}
