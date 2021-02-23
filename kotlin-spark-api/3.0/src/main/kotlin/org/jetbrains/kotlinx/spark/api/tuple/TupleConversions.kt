package org.jetbrains.kotlinx.spark.api.tuple

import scala.Tuple2
import scala.Tuple3

/**
 * This file adds conversions between Scala's [Tuple2] and Kotlin's [Pair] and [Tuple3] and [Triple] respectively.
 *
 * by Jolan Rensen, 18-02-2021
 */

fun <T1, T2, T3> Triple<T1, T2, T3>.toTuple(): Tuple3<T1, T2, T3> = Tuple3<T1, T2, T3>(first, second, third)
fun <T1, T2, T3> Tuple3<T1, T2, T3>.toTriple(): Triple<T1, T2, T3> = Triple<T1, T2, T3>(_1(), _2(), _3())

fun <T1, T2> Pair<T1, T2>.toTuple(): Tuple2<T1, T2> = Tuple2<T1, T2>(first, second)
fun <T1, T2> Tuple2<T1, T2>.toPair(): Pair<T1, T2> = Pair<T1, T2>(_1(), _2())