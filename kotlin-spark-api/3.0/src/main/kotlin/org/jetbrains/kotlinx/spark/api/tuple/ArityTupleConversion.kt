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
import org.jetbrains.kotlinx.spark.api.Arity1
import org.jetbrains.kotlinx.spark.api.Arity2
import org.jetbrains.kotlinx.spark.api.Arity3
import org.jetbrains.kotlinx.spark.api.Arity4
import org.jetbrains.kotlinx.spark.api.Arity5
import org.jetbrains.kotlinx.spark.api.Arity6
import org.jetbrains.kotlinx.spark.api.Arity7
import org.jetbrains.kotlinx.spark.api.Arity8
import org.jetbrains.kotlinx.spark.api.Arity9
import org.jetbrains.kotlinx.spark.api.Arity10
import org.jetbrains.kotlinx.spark.api.Arity11
import org.jetbrains.kotlinx.spark.api.Arity12
import org.jetbrains.kotlinx.spark.api.Arity13
import org.jetbrains.kotlinx.spark.api.Arity14
import org.jetbrains.kotlinx.spark.api.Arity15
import org.jetbrains.kotlinx.spark.api.Arity16
import org.jetbrains.kotlinx.spark.api.Arity17
import org.jetbrains.kotlinx.spark.api.Arity18
import org.jetbrains.kotlinx.spark.api.Arity19
import org.jetbrains.kotlinx.spark.api.Arity20
import org.jetbrains.kotlinx.spark.api.Arity21
import org.jetbrains.kotlinx.spark.api.Arity22

fun <T1> Arity1<T1>.toTuple(): Tuple1<T1> = Tuple1(_1)
fun <T1, T2> Arity2<T1, T2>.toTuple(): Tuple2<T1, T2> = Tuple2(_1, _2)
fun <T1, T2, T3> Arity3<T1, T2, T3>.toTuple(): Tuple3<T1, T2, T3> = Tuple3(_1, _2, _3)
fun <T1, T2, T3, T4> Arity4<T1, T2, T3, T4>.toTuple(): Tuple4<T1, T2, T3, T4> = Tuple4(_1, _2, _3, _4)
fun <T1, T2, T3, T4, T5> Arity5<T1, T2, T3, T4, T5>.toTuple(): Tuple5<T1, T2, T3, T4, T5> = Tuple5(_1, _2, _3, _4, _5)
fun <T1, T2, T3, T4, T5, T6> Arity6<T1, T2, T3, T4, T5, T6>.toTuple(): Tuple6<T1, T2, T3, T4, T5, T6> = Tuple6(_1, _2, _3, _4, _5, _6)
fun <T1, T2, T3, T4, T5, T6, T7> Arity7<T1, T2, T3, T4, T5, T6, T7>.toTuple(): Tuple7<T1, T2, T3, T4, T5, T6, T7> = Tuple7(_1, _2, _3, _4, _5, _6, _7)
fun <T1, T2, T3, T4, T5, T6, T7, T8> Arity8<T1, T2, T3, T4, T5, T6, T7, T8>.toTuple(): Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8(_1, _2, _3, _4, _5, _6, _7, _8)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Arity9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.toTuple(): Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9(_1, _2, _3, _4, _5, _6, _7, _8, _9)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Arity10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>.toTuple(): Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Arity11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>.toTuple(): Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> = Tuple11(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Arity12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>.toTuple(): Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> = Tuple12(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Arity13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>.toTuple(): Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> = Tuple13(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Arity14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>.toTuple(): Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> = Tuple14(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Arity15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>.toTuple(): Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> = Tuple15(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Arity16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>.toTuple(): Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> = Tuple16(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Arity17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>.toTuple(): Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> = Tuple17(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Arity18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>.toTuple(): Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> = Tuple18(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Arity19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>.toTuple(): Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> = Tuple19(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Arity20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>.toTuple(): Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> = Tuple20(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Arity21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>.toTuple(): Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> = Tuple21(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Arity22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>.toTuple(): Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> = Tuple22(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22)
fun <T1> Tuple1<T1>.toArity(): Arity1<T1> = Arity1(_1())
fun <T1, T2> Tuple2<T1, T2>.toArity(): Arity2<T1, T2> = Arity2(_1(), _2())
fun <T1, T2, T3> Tuple3<T1, T2, T3>.toArity(): Arity3<T1, T2, T3> = Arity3(_1(), _2(), _3())
fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.toArity(): Arity4<T1, T2, T3, T4> = Arity4(_1(), _2(), _3(), _4())
fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.toArity(): Arity5<T1, T2, T3, T4, T5> = Arity5(_1(), _2(), _3(), _4(), _5())
fun <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6>.toArity(): Arity6<T1, T2, T3, T4, T5, T6> = Arity6(_1(), _2(), _3(), _4(), _5(), _6())
fun <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7>.toArity(): Arity7<T1, T2, T3, T4, T5, T6, T7> = Arity7(_1(), _2(), _3(), _4(), _5(), _6(), _7())
fun <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>.toArity(): Arity8<T1, T2, T3, T4, T5, T6, T7, T8> = Arity8(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.toArity(): Arity9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Arity9(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>.toArity(): Arity10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Arity10(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>.toArity(): Arity11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> = Arity11(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>.toArity(): Arity12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> = Arity12(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>.toArity(): Arity13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> = Arity13(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12(), _13())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>.toArity(): Arity14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> = Arity14(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12(), _13(), _14())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>.toArity(): Arity15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> = Arity15(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12(), _13(), _14(), _15())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>.toArity(): Arity16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> = Arity16(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12(), _13(), _14(), _15(), _16())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>.toArity(): Arity17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> = Arity17(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12(), _13(), _14(), _15(), _16(), _17())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>.toArity(): Arity18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> = Arity18(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12(), _13(), _14(), _15(), _16(), _17(), _18())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>.toArity(): Arity19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> = Arity19(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12(), _13(), _14(), _15(), _16(), _17(), _18(), _19())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>.toArity(): Arity20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> = Arity20(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12(), _13(), _14(), _15(), _16(), _17(), _18(), _19(), _20())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>.toArity(): Arity21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> = Arity21(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12(), _13(), _14(), _15(), _16(), _17(), _18(), _19(), _20(), _21())
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>.toArity(): Arity22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> = Arity22(_1(), _2(), _3(), _4(), _5(), _6(), _7(), _8(), _9(), _10(), _11(), _12(), _13(), _14(), _15(), _16(), _17(), _18(), _19(), _20(), _21(), _22())
