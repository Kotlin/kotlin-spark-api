package org.jetbrains.spark.api.examples

import org.jetbrains.spark.api.*

fun main() {
    withSpark {
        dsOf(1, 2, 3, 4, 5)
                .map { it to (it + 2) }
                .withCached {
                    showDS()

                    filter { it.first % 2 == 0 }.showDS()
                }
                .map { c(it.first, it.second, (it.first + it.second) * 2) }
                .show()
    }
}