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
@file:Suppress("RemoveExplicitTypeArguments", "FunctionName")

package org.jetbrains.kotlinx.spark.api.tuples

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
 * This file provides a descriptive way to create Tuples using [X].
 *
 * For instance:
 * ```val yourTuple = 1 X "test" X a```
 *
 */

/**
 * Returns a new Tuple2 of the given arguments.
 * @see tupleOf
 * @see t
 **/
infix fun <T1, T2> T1.X(other: T2): Tuple2<T1, T2> = Tuple2<T1, T2>(this, other)

infix fun <T1, T2, T3> Tuple2<T1, T2>.X(next: T3): Tuple3<T1, T2, T3> = Tuple3<T1, T2, T3>(this._1(), this._2(), next)
infix fun <T1, T2, T3, T4> Tuple3<T1, T2, T3>.X(next: T4): Tuple4<T1, T2, T3, T4> = Tuple4<T1, T2, T3, T4>(this._1(), this._2(), this._3(), next)
infix fun <T1, T2, T3, T4, T5> Tuple4<T1, T2, T3, T4>.X(next: T5): Tuple5<T1, T2, T3, T4, T5> = Tuple5<T1, T2, T3, T4, T5>(this._1(), this._2(), this._3(), this._4(), next)
infix fun <T1, T2, T3, T4, T5, T6> Tuple5<T1, T2, T3, T4, T5>.X(next: T6): Tuple6<T1, T2, T3, T4, T5, T6> = Tuple6<T1, T2, T3, T4, T5, T6>(this._1(), this._2(), this._3(), this._4(), this._5(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7> Tuple6<T1, T2, T3, T4, T5, T6>.X(next: T7): Tuple7<T1, T2, T3, T4, T5, T6, T7> = Tuple7<T1, T2, T3, T4, T5, T6, T7>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8> Tuple7<T1, T2, T3, T4, T5, T6, T7>.X(next: T8): Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>.X(next: T9): Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.X(next: T10): Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>.X(next: T11): Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> = Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>.X(next: T12): Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> = Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>.X(next: T13): Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> = Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>.X(next: T14): Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> = Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>.X(next: T15): Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> = Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>.X(next: T16): Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> = Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>.X(next: T17): Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> = Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>.X(next: T18): Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> = Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>.X(next: T19): Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> = Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>.X(next: T20): Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> = Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>.X(next: T21): Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> = Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), this._20(), next)
infix fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>.X(next: T22): Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> = Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), this._20(), this._21(), next)
