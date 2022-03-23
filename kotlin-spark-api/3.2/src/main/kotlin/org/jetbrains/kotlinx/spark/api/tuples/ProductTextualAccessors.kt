/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 3.2+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2022 JetBrains
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
package org.jetbrains.kotlinx.spark.api.tuples

import scala.Product1
import scala.Product2
import scala.Product3
import scala.Product4
import scala.Product5
import scala.Product6
import scala.Product7
import scala.Product8
import scala.Product9
import scala.Product10
import scala.Product11
import scala.Product12
import scala.Product13
import scala.Product14
import scala.Product15
import scala.Product16
import scala.Product17
import scala.Product18
import scala.Product19
import scala.Product20
import scala.Product21
import scala.Product22

/**
 *
 * This file provides the functions `yourTuple.first()` and `yourTuple.last()` to access
 * the value you require.
 *
 */

/** Returns the first value of this Tuple or Product. */
fun <T> Product1<T>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product1<T>.last(): T = this._1()

/** Returns the first value of this Tuple or Product. */
fun <T> Product2<T, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product2<*, T>.last(): T = this._2()

/** Returns the first value of this Tuple or Product. */
fun <T> Product3<T, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product3<*, *, T>.last(): T = this._3()

/** Returns the first value of this Tuple or Product. */
fun <T> Product4<T, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product4<*, *, *, T>.last(): T = this._4()

/** Returns the first value of this Tuple or Product. */
fun <T> Product5<T, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product5<*, *, *, *, T>.last(): T = this._5()

/** Returns the first value of this Tuple or Product. */
fun <T> Product6<T, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product6<*, *, *, *, *, T>.last(): T = this._6()

/** Returns the first value of this Tuple or Product. */
fun <T> Product7<T, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product7<*, *, *, *, *, *, T>.last(): T = this._7()

/** Returns the first value of this Tuple or Product. */
fun <T> Product8<T, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product8<*, *, *, *, *, *, *, T>.last(): T = this._8()

/** Returns the first value of this Tuple or Product. */
fun <T> Product9<T, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product9<*, *, *, *, *, *, *, *, T>.last(): T = this._9()

/** Returns the first value of this Tuple or Product. */
fun <T> Product10<T, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product10<*, *, *, *, *, *, *, *, *, T>.last(): T = this._10()

/** Returns the first value of this Tuple or Product. */
fun <T> Product11<T, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product11<*, *, *, *, *, *, *, *, *, *, T>.last(): T = this._11()

/** Returns the first value of this Tuple or Product. */
fun <T> Product12<T, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product12<*, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._12()

/** Returns the first value of this Tuple or Product. */
fun <T> Product13<T, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product13<*, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._13()

/** Returns the first value of this Tuple or Product. */
fun <T> Product14<T, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product14<*, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._14()

/** Returns the first value of this Tuple or Product. */
fun <T> Product15<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product15<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._15()

/** Returns the first value of this Tuple or Product. */
fun <T> Product16<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._16()

/** Returns the first value of this Tuple or Product. */
fun <T> Product17<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._17()

/** Returns the first value of this Tuple or Product. */
fun <T> Product18<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._18()

/** Returns the first value of this Tuple or Product. */
fun <T> Product19<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._19()

/** Returns the first value of this Tuple or Product. */
fun <T> Product20<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._20()

/** Returns the first value of this Tuple or Product. */
fun <T> Product21<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._21()

/** Returns the first value of this Tuple or Product. */
fun <T> Product22<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._22()

