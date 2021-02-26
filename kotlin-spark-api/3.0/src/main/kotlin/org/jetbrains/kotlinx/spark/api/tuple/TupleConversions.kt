/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 3.0+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2021 JetBrains
 * ----------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =LICENSEEND=
 */
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
