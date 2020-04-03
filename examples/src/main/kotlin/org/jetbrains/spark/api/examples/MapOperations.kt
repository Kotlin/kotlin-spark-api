package org.jetbrains.spark.api.examples
import org.jetbrains.spark.api.*

fun main() {
    withSpark {
        dsOf(mapOf(1 to c(1,2,3), 2 to c(1,2,3)), mapOf(3 to c(1,2,3), 4 to c(1,2,3)))
                .debugCodegen()
                .flatMap { it.toList().iterator() }
                .show()

    }
}