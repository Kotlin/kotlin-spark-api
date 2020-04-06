package org.jetbrains.spark.api.examples

import org.jetbrains.spark.api.*

fun main() {
    withSpark(props = mapOf("spark.sql.codegen.wholeStage" to true)) {
        dsOf(1, null, 2)
                .map { c(it) }
//                .also { it.printSchema() }
//                .also { it.explain() }
//                .debugCodegen()
                .show()
    }

}