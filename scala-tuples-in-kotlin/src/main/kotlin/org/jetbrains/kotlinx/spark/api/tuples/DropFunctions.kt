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

import scala.Tuple1
import scala.Tuple2
import scala.Tuple3
import scala.Tuple4
import scala.Tuple5
import scala.Tuple6
import scala.Tuple7
import scala.Tuple8
import scala.Tuple9
import scala.Tuple10
import scala.Tuple11
import scala.Tuple12
import scala.Tuple13
import scala.Tuple14
import scala.Tuple15
import scala.Tuple16
import scala.Tuple17
import scala.Tuple18
import scala.Tuple19
import scala.Tuple20
import scala.Tuple21
import scala.Tuple22

/**
 * This file contains functions to lower the amount of dimensions of tuples.
 * This can be done using [dropFirst] and [dropLast].
 *
 * For example:
 * ```kotlin
 * val yourTuple: Tuple2<Int, String> = tupleOf(1, "test", a).dropLast()
 * ```
 *
 */

fun Tuple1<*>.dropFirst(): EmptyTuple = EmptyTuple
fun Tuple1<*>.dropLast(): EmptyTuple = EmptyTuple
fun <T1> Tuple2<*, T1>.dropFirst(): Tuple1<T1> = Tuple1<T1>(this._2())
fun <T1> Tuple2<T1, *>.dropLast(): Tuple1<T1> = Tuple1<T1>(this._1())
fun <T1, T2> Tuple3<*, T1, T2>.dropFirst(): Tuple2<T1, T2> = Tuple2<T1, T2>(this._2(), this._3())
fun <T1, T2> Tuple3<T1, T2, *>.dropLast(): Tuple2<T1, T2> = Tuple2<T1, T2>(this._1(), this._2())
fun <T1, T2, T3> Tuple4<*, T1, T2, T3>.dropFirst(): Tuple3<T1, T2, T3> = Tuple3<T1, T2, T3>(this._2(), this._3(), this._4())
fun <T1, T2, T3> Tuple4<T1, T2, T3, *>.dropLast(): Tuple3<T1, T2, T3> = Tuple3<T1, T2, T3>(this._1(), this._2(), this._3())
fun <T1, T2, T3, T4> Tuple5<*, T1, T2, T3, T4>.dropFirst(): Tuple4<T1, T2, T3, T4> = Tuple4<T1, T2, T3, T4>(this._2(), this._3(), this._4(), this._5())
fun <T1, T2, T3, T4> Tuple5<T1, T2, T3, T4, *>.dropLast(): Tuple4<T1, T2, T3, T4> = Tuple4<T1, T2, T3, T4>(this._1(), this._2(), this._3(), this._4())
fun <T1, T2, T3, T4, T5> Tuple6<*, T1, T2, T3, T4, T5>.dropFirst(): Tuple5<T1, T2, T3, T4, T5> = Tuple5<T1, T2, T3, T4, T5>(this._2(), this._3(), this._4(), this._5(), this._6())
fun <T1, T2, T3, T4, T5> Tuple6<T1, T2, T3, T4, T5, *>.dropLast(): Tuple5<T1, T2, T3, T4, T5> = Tuple5<T1, T2, T3, T4, T5>(this._1(), this._2(), this._3(), this._4(), this._5())
fun <T1, T2, T3, T4, T5, T6> Tuple7<*, T1, T2, T3, T4, T5, T6>.dropFirst(): Tuple6<T1, T2, T3, T4, T5, T6> = Tuple6<T1, T2, T3, T4, T5, T6>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7())
fun <T1, T2, T3, T4, T5, T6> Tuple7<T1, T2, T3, T4, T5, T6, *>.dropLast(): Tuple6<T1, T2, T3, T4, T5, T6> = Tuple6<T1, T2, T3, T4, T5, T6>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6())
fun <T1, T2, T3, T4, T5, T6, T7> Tuple8<*, T1, T2, T3, T4, T5, T6, T7>.dropFirst(): Tuple7<T1, T2, T3, T4, T5, T6, T7> = Tuple7<T1, T2, T3, T4, T5, T6, T7>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8())
fun <T1, T2, T3, T4, T5, T6, T7> Tuple8<T1, T2, T3, T4, T5, T6, T7, *>.dropLast(): Tuple7<T1, T2, T3, T4, T5, T6, T7> = Tuple7<T1, T2, T3, T4, T5, T6, T7>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7())
fun <T1, T2, T3, T4, T5, T6, T7, T8> Tuple9<*, T1, T2, T3, T4, T5, T6, T7, T8>.dropFirst(): Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9())
fun <T1, T2, T3, T4, T5, T6, T7, T8> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, *>.dropLast(): Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple10<*, T1, T2, T3, T4, T5, T6, T7, T8, T9>.dropFirst(): Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, *>.dropLast(): Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Tuple11<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>.dropFirst(): Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, *>.dropLast(): Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Tuple12<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>.dropFirst(): Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> = Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, *>.dropLast(): Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> = Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Tuple13<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>.dropFirst(): Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> = Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, *>.dropLast(): Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> = Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Tuple14<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>.dropFirst(): Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> = Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, *>.dropLast(): Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> = Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Tuple15<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>.dropFirst(): Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> = Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, *>.dropLast(): Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> = Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Tuple16<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>.dropFirst(): Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> = Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, *>.dropLast(): Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> = Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Tuple17<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>.dropFirst(): Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> = Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, *>.dropLast(): Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> = Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Tuple18<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>.dropFirst(): Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> = Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, *>.dropLast(): Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> = Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Tuple19<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>.dropFirst(): Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> = Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, *>.dropLast(): Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> = Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Tuple20<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>.dropFirst(): Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> = Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), this._20())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, *>.dropLast(): Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> = Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Tuple21<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>.dropFirst(): Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> = Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), this._20(), this._21())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, *>.dropLast(): Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> = Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), this._20())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Tuple22<*, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>.dropFirst(): Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> = Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>(this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), this._20(), this._21(), this._22())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, *>.dropLast(): Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> = Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), this._20(), this._21())
