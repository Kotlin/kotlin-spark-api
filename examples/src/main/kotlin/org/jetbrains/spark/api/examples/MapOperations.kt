package org.jetbrains.spark.api.examples
import org.jetbrains.spark.api.*

fun main() {
    withSpark {
        dsOf(mapOf(1 to "a", 2 to "b"), mapOf(3 to "c", 4 to "d"))
                .flatMap { it.toList().iterator() }
                .show()

    }
}