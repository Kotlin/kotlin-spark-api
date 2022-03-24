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
@file:Suppress("ObjectPropertyName")

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

val <T> Product1<T>._1: T get() = this._1()

/** Returns the first value of this Tuple or Product. */
fun <T> Product1<T>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product1<T>.last(): T = this._1()

val <T> Product2<T, *>._1: T get() = this._1()

val <T> Product2<*, T>._2: T get() = this._2()

/** Returns the first value of this Tuple or Product. */
fun <T> Product2<T, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product2<*, T>.last(): T = this._2()

val <T> Product3<T, *, *>._1: T get() = this._1()

val <T> Product3<*, T, *>._2: T get() = this._2()

val <T> Product3<*, *, T>._3: T get() = this._3()

/** Returns the first value of this Tuple or Product. */
fun <T> Product3<T, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product3<*, *, T>.last(): T = this._3()

val <T> Product4<T, *, *, *>._1: T get() = this._1()

val <T> Product4<*, T, *, *>._2: T get() = this._2()

val <T> Product4<*, *, T, *>._3: T get() = this._3()

val <T> Product4<*, *, *, T>._4: T get() = this._4()

/** Returns the first value of this Tuple or Product. */
fun <T> Product4<T, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product4<*, *, *, T>.last(): T = this._4()

val <T> Product5<T, *, *, *, *>._1: T get() = this._1()

val <T> Product5<*, T, *, *, *>._2: T get() = this._2()

val <T> Product5<*, *, T, *, *>._3: T get() = this._3()

val <T> Product5<*, *, *, T, *>._4: T get() = this._4()

val <T> Product5<*, *, *, *, T>._5: T get() = this._5()

/** Returns the first value of this Tuple or Product. */
fun <T> Product5<T, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product5<*, *, *, *, T>.last(): T = this._5()

val <T> Product6<T, *, *, *, *, *>._1: T get() = this._1()

val <T> Product6<*, T, *, *, *, *>._2: T get() = this._2()

val <T> Product6<*, *, T, *, *, *>._3: T get() = this._3()

val <T> Product6<*, *, *, T, *, *>._4: T get() = this._4()

val <T> Product6<*, *, *, *, T, *>._5: T get() = this._5()

val <T> Product6<*, *, *, *, *, T>._6: T get() = this._6()

/** Returns the first value of this Tuple or Product. */
fun <T> Product6<T, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product6<*, *, *, *, *, T>.last(): T = this._6()

val <T> Product7<T, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product7<*, T, *, *, *, *, *>._2: T get() = this._2()

val <T> Product7<*, *, T, *, *, *, *>._3: T get() = this._3()

val <T> Product7<*, *, *, T, *, *, *>._4: T get() = this._4()

val <T> Product7<*, *, *, *, T, *, *>._5: T get() = this._5()

val <T> Product7<*, *, *, *, *, T, *>._6: T get() = this._6()

val <T> Product7<*, *, *, *, *, *, T>._7: T get() = this._7()

/** Returns the first value of this Tuple or Product. */
fun <T> Product7<T, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product7<*, *, *, *, *, *, T>.last(): T = this._7()

val <T> Product8<T, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product8<*, T, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product8<*, *, T, *, *, *, *, *>._3: T get() = this._3()

val <T> Product8<*, *, *, T, *, *, *, *>._4: T get() = this._4()

val <T> Product8<*, *, *, *, T, *, *, *>._5: T get() = this._5()

val <T> Product8<*, *, *, *, *, T, *, *>._6: T get() = this._6()

val <T> Product8<*, *, *, *, *, *, T, *>._7: T get() = this._7()

val <T> Product8<*, *, *, *, *, *, *, T>._8: T get() = this._8()

/** Returns the first value of this Tuple or Product. */
fun <T> Product8<T, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product8<*, *, *, *, *, *, *, T>.last(): T = this._8()

val <T> Product9<T, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product9<*, T, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product9<*, *, T, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product9<*, *, *, T, *, *, *, *, *>._4: T get() = this._4()

val <T> Product9<*, *, *, *, T, *, *, *, *>._5: T get() = this._5()

val <T> Product9<*, *, *, *, *, T, *, *, *>._6: T get() = this._6()

val <T> Product9<*, *, *, *, *, *, T, *, *>._7: T get() = this._7()

val <T> Product9<*, *, *, *, *, *, *, T, *>._8: T get() = this._8()

val <T> Product9<*, *, *, *, *, *, *, *, T>._9: T get() = this._9()

/** Returns the first value of this Tuple or Product. */
fun <T> Product9<T, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product9<*, *, *, *, *, *, *, *, T>.last(): T = this._9()

val <T> Product10<T, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product10<*, T, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product10<*, *, T, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product10<*, *, *, T, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product10<*, *, *, *, T, *, *, *, *, *>._5: T get() = this._5()

val <T> Product10<*, *, *, *, *, T, *, *, *, *>._6: T get() = this._6()

val <T> Product10<*, *, *, *, *, *, T, *, *, *>._7: T get() = this._7()

val <T> Product10<*, *, *, *, *, *, *, T, *, *>._8: T get() = this._8()

val <T> Product10<*, *, *, *, *, *, *, *, T, *>._9: T get() = this._9()

val <T> Product10<*, *, *, *, *, *, *, *, *, T>._10: T get() = this._10()

/** Returns the first value of this Tuple or Product. */
fun <T> Product10<T, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product10<*, *, *, *, *, *, *, *, *, T>.last(): T = this._10()

val <T> Product11<T, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product11<*, T, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product11<*, *, T, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product11<*, *, *, T, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product11<*, *, *, *, T, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product11<*, *, *, *, *, T, *, *, *, *, *>._6: T get() = this._6()

val <T> Product11<*, *, *, *, *, *, T, *, *, *, *>._7: T get() = this._7()

val <T> Product11<*, *, *, *, *, *, *, T, *, *, *>._8: T get() = this._8()

val <T> Product11<*, *, *, *, *, *, *, *, T, *, *>._9: T get() = this._9()

val <T> Product11<*, *, *, *, *, *, *, *, *, T, *>._10: T get() = this._10()

val <T> Product11<*, *, *, *, *, *, *, *, *, *, T>._11: T get() = this._11()

/** Returns the first value of this Tuple or Product. */
fun <T> Product11<T, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product11<*, *, *, *, *, *, *, *, *, *, T>.last(): T = this._11()

val <T> Product12<T, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product12<*, T, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product12<*, *, T, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product12<*, *, *, T, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product12<*, *, *, *, T, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product12<*, *, *, *, *, T, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product12<*, *, *, *, *, *, T, *, *, *, *, *>._7: T get() = this._7()

val <T> Product12<*, *, *, *, *, *, *, T, *, *, *, *>._8: T get() = this._8()

val <T> Product12<*, *, *, *, *, *, *, *, T, *, *, *>._9: T get() = this._9()

val <T> Product12<*, *, *, *, *, *, *, *, *, T, *, *>._10: T get() = this._10()

val <T> Product12<*, *, *, *, *, *, *, *, *, *, T, *>._11: T get() = this._11()

val <T> Product12<*, *, *, *, *, *, *, *, *, *, *, T>._12: T get() = this._12()

/** Returns the first value of this Tuple or Product. */
fun <T> Product12<T, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product12<*, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._12()

val <T> Product13<T, *, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product13<*, T, *, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product13<*, *, T, *, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product13<*, *, *, T, *, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product13<*, *, *, *, T, *, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product13<*, *, *, *, *, T, *, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product13<*, *, *, *, *, *, T, *, *, *, *, *, *>._7: T get() = this._7()

val <T> Product13<*, *, *, *, *, *, *, T, *, *, *, *, *>._8: T get() = this._8()

val <T> Product13<*, *, *, *, *, *, *, *, T, *, *, *, *>._9: T get() = this._9()

val <T> Product13<*, *, *, *, *, *, *, *, *, T, *, *, *>._10: T get() = this._10()

val <T> Product13<*, *, *, *, *, *, *, *, *, *, T, *, *>._11: T get() = this._11()

val <T> Product13<*, *, *, *, *, *, *, *, *, *, *, T, *>._12: T get() = this._12()

val <T> Product13<*, *, *, *, *, *, *, *, *, *, *, *, T>._13: T get() = this._13()

/** Returns the first value of this Tuple or Product. */
fun <T> Product13<T, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product13<*, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._13()

val <T> Product14<T, *, *, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product14<*, T, *, *, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product14<*, *, T, *, *, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product14<*, *, *, T, *, *, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product14<*, *, *, *, T, *, *, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product14<*, *, *, *, *, T, *, *, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product14<*, *, *, *, *, *, T, *, *, *, *, *, *, *>._7: T get() = this._7()

val <T> Product14<*, *, *, *, *, *, *, T, *, *, *, *, *, *>._8: T get() = this._8()

val <T> Product14<*, *, *, *, *, *, *, *, T, *, *, *, *, *>._9: T get() = this._9()

val <T> Product14<*, *, *, *, *, *, *, *, *, T, *, *, *, *>._10: T get() = this._10()

val <T> Product14<*, *, *, *, *, *, *, *, *, *, T, *, *, *>._11: T get() = this._11()

val <T> Product14<*, *, *, *, *, *, *, *, *, *, *, T, *, *>._12: T get() = this._12()

val <T> Product14<*, *, *, *, *, *, *, *, *, *, *, *, T, *>._13: T get() = this._13()

val <T> Product14<*, *, *, *, *, *, *, *, *, *, *, *, *, T>._14: T get() = this._14()

/** Returns the first value of this Tuple or Product. */
fun <T> Product14<T, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product14<*, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._14()

val <T> Product15<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product15<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product15<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product15<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product15<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product15<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product15<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *>._7: T get() = this._7()

val <T> Product15<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *>._8: T get() = this._8()

val <T> Product15<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *>._9: T get() = this._9()

val <T> Product15<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *>._10: T get() = this._10()

val <T> Product15<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *>._11: T get() = this._11()

val <T> Product15<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *>._12: T get() = this._12()

val <T> Product15<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *>._13: T get() = this._13()

val <T> Product15<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *>._14: T get() = this._14()

val <T> Product15<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T>._15: T get() = this._15()

/** Returns the first value of this Tuple or Product. */
fun <T> Product15<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product15<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._15()

val <T> Product16<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product16<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product16<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product16<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product16<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product16<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product16<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>._7: T get() = this._7()

val <T> Product16<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>._8: T get() = this._8()

val <T> Product16<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>._9: T get() = this._9()

val <T> Product16<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>._10: T get() = this._10()

val <T> Product16<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>._11: T get() = this._11()

val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>._12: T get() = this._12()

val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>._13: T get() = this._13()

val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>._14: T get() = this._14()

val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>._15: T get() = this._15()

val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>._16: T get() = this._16()

/** Returns the first value of this Tuple or Product. */
fun <T> Product16<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._16()

val <T> Product17<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product17<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product17<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product17<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product17<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product17<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product17<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>._7: T get() = this._7()

val <T> Product17<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>._8: T get() = this._8()

val <T> Product17<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>._9: T get() = this._9()

val <T> Product17<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>._10: T get() = this._10()

val <T> Product17<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>._11: T get() = this._11()

val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>._12: T get() = this._12()

val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>._13: T get() = this._13()

val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>._14: T get() = this._14()

val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>._15: T get() = this._15()

val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>._16: T get() = this._16()

val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>._17: T get() = this._17()

/** Returns the first value of this Tuple or Product. */
fun <T> Product17<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._17()

val <T> Product18<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product18<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product18<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product18<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product18<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product18<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product18<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>._7: T get() = this._7()

val <T> Product18<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>._8: T get() = this._8()

val <T> Product18<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>._9: T get() = this._9()

val <T> Product18<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>._10: T get() = this._10()

val <T> Product18<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>._11: T get() = this._11()

val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>._12: T get() = this._12()

val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>._13: T get() = this._13()

val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>._14: T get() = this._14()

val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>._15: T get() = this._15()

val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>._16: T get() = this._16()

val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>._17: T get() = this._17()

val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>._18: T get() = this._18()

/** Returns the first value of this Tuple or Product. */
fun <T> Product18<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._18()

val <T> Product19<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product19<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product19<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product19<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product19<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product19<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product19<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>._7: T get() = this._7()

val <T> Product19<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>._8: T get() = this._8()

val <T> Product19<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>._9: T get() = this._9()

val <T> Product19<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>._10: T get() = this._10()

val <T> Product19<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>._11: T get() = this._11()

val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>._12: T get() = this._12()

val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>._13: T get() = this._13()

val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>._14: T get() = this._14()

val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>._15: T get() = this._15()

val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>._16: T get() = this._16()

val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>._17: T get() = this._17()

val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>._18: T get() = this._18()

val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>._19: T get() = this._19()

/** Returns the first value of this Tuple or Product. */
fun <T> Product19<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._19()

val <T> Product20<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product20<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product20<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product20<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product20<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product20<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product20<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>._7: T get() = this._7()

val <T> Product20<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>._8: T get() = this._8()

val <T> Product20<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>._9: T get() = this._9()

val <T> Product20<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>._10: T get() = this._10()

val <T> Product20<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>._11: T get() = this._11()

val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>._12: T get() = this._12()

val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>._13: T get() = this._13()

val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>._14: T get() = this._14()

val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>._15: T get() = this._15()

val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>._16: T get() = this._16()

val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>._17: T get() = this._17()

val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>._18: T get() = this._18()

val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>._19: T get() = this._19()

val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>._20: T get() = this._20()

/** Returns the first value of this Tuple or Product. */
fun <T> Product20<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._20()

val <T> Product21<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product21<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product21<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product21<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product21<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product21<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product21<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._7: T get() = this._7()

val <T> Product21<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>._8: T get() = this._8()

val <T> Product21<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>._9: T get() = this._9()

val <T> Product21<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>._10: T get() = this._10()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>._11: T get() = this._11()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>._12: T get() = this._12()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>._13: T get() = this._13()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>._14: T get() = this._14()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>._15: T get() = this._15()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>._16: T get() = this._16()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>._17: T get() = this._17()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>._18: T get() = this._18()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>._19: T get() = this._19()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>._20: T get() = this._20()

val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>._21: T get() = this._21()

/** Returns the first value of this Tuple or Product. */
fun <T> Product21<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._21()

val <T> Product22<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._1: T get() = this._1()

val <T> Product22<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._2: T get() = this._2()

val <T> Product22<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._3: T get() = this._3()

val <T> Product22<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._4: T get() = this._4()

val <T> Product22<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._5: T get() = this._5()

val <T> Product22<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._6: T get() = this._6()

val <T> Product22<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._7: T get() = this._7()

val <T> Product22<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>._8: T get() = this._8()

val <T> Product22<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>._9: T get() = this._9()

val <T> Product22<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>._10: T get() = this._10()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>._11: T get() = this._11()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>._12: T get() = this._12()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>._13: T get() = this._13()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>._14: T get() = this._14()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>._15: T get() = this._15()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>._16: T get() = this._16()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>._17: T get() = this._17()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>._18: T get() = this._18()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>._19: T get() = this._19()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>._20: T get() = this._20()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>._21: T get() = this._21()

val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>._22: T get() = this._22()

/** Returns the first value of this Tuple or Product. */
fun <T> Product22<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first(): T = this._1()

/** Returns the last value of this Tuple or Product. */
fun <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last(): T = this._22()
