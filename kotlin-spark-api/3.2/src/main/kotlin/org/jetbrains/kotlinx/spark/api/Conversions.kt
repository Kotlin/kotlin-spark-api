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

/**
 * This files contains conversions of Iterators, Collections, Tuples, etc. between the Scala-
 * and Kotlin/Java variants.
 */

@file:Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments", "unused")

package org.jetbrains.kotlinx.spark.api

import org.apache.spark.api.java.Optional
import scala.*
import scala.collection.JavaConverters
import java.util.*
import java.util.Enumeration
import java.util.concurrent.ConcurrentMap
import scala.collection.Iterable as ScalaIterable
import scala.collection.Iterator as ScalaIterator
import scala.collection.Map as ScalaMap
import scala.collection.Seq as ScalaSeq
import scala.collection.Set as ScalaSet
import scala.collection.concurrent.Map as ScalaConcurrentMap
import scala.collection.mutable.Buffer as ScalaMutableBuffer
import scala.collection.mutable.Map as ScalaMutableMap
import scala.collection.mutable.Seq as ScalaMutableSeq
import scala.collection.mutable.Set as ScalaMutableSet


/** Converts Scala [Option] to Kotlin nullable. */
fun <T> Option<T>.toNullable(): T? = getOrElse { null }

/** Converts nullable value to Scala [Option]. */
fun <T> T?.toOption(): Option<T> = Option.apply(this)

/** Converts [Optional] to Kotlin nullable. */
fun <T> Optional<T>.toNullable(): T? = orElse(null)

/** Converts nullable value to [Optional]. */
fun <T> T?.toOptional(): Optional<T> = Optional.ofNullable(this)

/**
 * @see JavaConverters.asScalaIterator for more information.
 */
fun <A> Iterator<A>.asScalaIterator(): ScalaIterator<A> = JavaConverters.asScalaIterator<A>(this)

/**
 * @see JavaConverters.enumerationAsScalaIterator for more information.
 */
fun <A> Enumeration<A>.asScalaIterator(): ScalaIterator<A> = JavaConverters.enumerationAsScalaIterator<A>(this)

/**
 * @see JavaConverters.iterableAsScalaIterable for more information.
 */
fun <A> Iterable<A>.asScalaIterable(): ScalaIterable<A> = JavaConverters.iterableAsScalaIterable<A>(this)

/**
 * @see JavaConverters.collectionAsScalaIterable for more information.
 */
fun <A> Collection<A>.asScalaIterable(): ScalaIterable<A> = JavaConverters.collectionAsScalaIterable<A>(this)

/**
 * @see JavaConverters.asScalaBuffer for more information.
 */
fun <A> MutableList<A>.asScalaMutableBuffer(): ScalaMutableBuffer<A> = JavaConverters.asScalaBuffer<A>(this)

/**
 * @see JavaConverters.asScalaSet for more information.
 */
fun <A> MutableSet<A>.asScalaMutableSet(): ScalaMutableSet<A> = JavaConverters.asScalaSet<A>(this)

/**
 * @see JavaConverters.mapAsScalaMap for more information.
 */
fun <A, B> MutableMap<A, B>.asScalaMutableMap(): ScalaMutableMap<A, B> = JavaConverters.mapAsScalaMap<A, B>(this)

/**
 * @see JavaConverters.dictionaryAsScalaMap for more information.
 */
fun <A, B> Map<A, B>.asScalaMap(): ScalaMap<A, B> = JavaConverters.mapAsScalaMap<A, B>(this)

/**
 * @see JavaConverters.mapAsScalaConcurrentMap for more information.
 */
fun <A, B> ConcurrentMap<A, B>.asScalaConcurrentMap(): ScalaConcurrentMap<A, B> =
    JavaConverters.mapAsScalaConcurrentMap<A, B>(this)

/**
 * @see JavaConverters.dictionaryAsScalaMap for more information.
 */
fun <A, B> Dictionary<A, B>.asScalaMap(): ScalaMutableMap<A, B> = JavaConverters.dictionaryAsScalaMap<A, B>(this)

/**
 * @see JavaConverters.propertiesAsScalaMap for more information.
 */
fun Properties.asScalaMap(): ScalaMutableMap<String, String> = JavaConverters.propertiesAsScalaMap(this)


/**
 * @see JavaConverters.asJavaIterator for more information.
 */
fun <A> ScalaIterator<A>.asKotlinIterator(): Iterator<A> = JavaConverters.asJavaIterator<A>(this)

/**
 * @see JavaConverters.asJavaEnumeration for more information.
 */
fun <A> ScalaIterator<A>.asKotlinEnumeration(): Enumeration<A> = JavaConverters.asJavaEnumeration<A>(this)

/**
 * @see JavaConverters.asJavaIterable for more information.
 */
fun <A> ScalaIterable<A>.asKotlinIterable(): Iterable<A> = JavaConverters.asJavaIterable<A>(this)

/**
 * @see JavaConverters.asJavaCollection for more information.
 */
fun <A> ScalaIterable<A>.asKotlinCollection(): Collection<A> = JavaConverters.asJavaCollection<A>(this)

/**
 * @see JavaConverters.bufferAsJavaList for more information.
 */
fun <A> ScalaMutableBuffer<A>.asKotlinMutableList(): MutableList<A> = JavaConverters.bufferAsJavaList<A>(this)

/**
 * @see JavaConverters.mutableSeqAsJavaList for more information.
 */
fun <A> ScalaMutableSeq<A>.asKotlinMutableList(): MutableList<A> = JavaConverters.mutableSeqAsJavaList<A>(this)

/**
 * @see JavaConverters.seqAsJavaList for more information.
 */
fun <A> ScalaSeq<A>.asKotlinList(): List<A> = JavaConverters.seqAsJavaList<A>(this)

/**
 * @see JavaConverters.mutableSetAsJavaSet for more information.
 */
fun <A> ScalaMutableSet<A>.asKotlinMutableSet(): MutableSet<A> = JavaConverters.mutableSetAsJavaSet<A>(this)

/**
 * @see JavaConverters.setAsJavaSet for more information.
 */
fun <A> ScalaSet<A>.asKotlinSet(): Set<A> = JavaConverters.setAsJavaSet<A>(this)

/**
 * @see JavaConverters.mutableMapAsJavaMap for more information.
 */
fun <A, B> ScalaMutableMap<A, B>.asKotlinMutableMap(): MutableMap<A, B> = JavaConverters.mutableMapAsJavaMap<A, B>(this)

/**
 * @see JavaConverters.asJavaDictionary for more information.
 */
fun <A, B> ScalaMutableMap<A, B>.asKotlinDictionary(): Dictionary<A, B> = JavaConverters.asJavaDictionary<A, B>(this)

/**
 * @see JavaConverters.mapAsJavaMap for more information.
 */
fun <A, B> ScalaMap<A, B>.asKotlinMap(): Map<A, B> = JavaConverters.mapAsJavaMap<A, B>(this)

/**
 * @see JavaConverters.mapAsJavaConcurrentMap for more information.
 */
fun <A, B> ScalaConcurrentMap<A, B>.asKotlinConcurrentMap(): ConcurrentMap<A, B> =
    JavaConverters.mapAsJavaConcurrentMap<A, B>(this)


/**
 * Returns a new [Tuple2] based on the arguments in the current [Pair].
 */
fun <T1, T2> Pair<T1, T2>.toTuple(): Tuple2<T1, T2> = Tuple2<T1, T2>(first, second)

/**
 * Returns a new [Arity2] based on the arguments in the current [Pair].
 */
fun <T1, T2> Pair<T1, T2>.toArity(): Arity2<T1, T2> = Arity2<T1, T2>(first, second)

/**
 * Returns a new [Pair] based on the arguments in the current [Tuple2].
 */
fun <T1, T2> Tuple2<T1, T2>.toPair(): Pair<T1, T2> = Pair<T1, T2>(_1(), _2())

/**
 * Returns a new [Pair] based on the arguments in the current [Arity2].
 */
fun <T1, T2> Arity2<T1, T2>.toPair(): Pair<T1, T2> = Pair<T1, T2>(_1, _2)


/**
 * Returns a new [Tuple3] based on the arguments in the current [Triple].
 */
fun <T1, T2, T3> Triple<T1, T2, T3>.toTuple(): Tuple3<T1, T2, T3> = Tuple3<T1, T2, T3>(first, second, third)

/**
 * Returns a new [Arity3] based on the arguments in the current [Triple].
 */
fun <T1, T2, T3> Triple<T1, T2, T3>.toArity(): Arity3<T1, T2, T3> = Arity3<T1, T2, T3>(first, second, third)

/**
 * Returns a new [Triple] based on the arguments in the current [Tuple3].
 */
fun <T1, T2, T3> Tuple3<T1, T2, T3>.toTriple(): Triple<T1, T2, T3> = Triple<T1, T2, T3>(_1(), _2(), _3())

/**
 * Returns a new [Triple] based on the arguments in the current [Arity3].
 */
fun <T1, T2, T3> Arity3<T1, T2, T3>.toTriple(): Triple<T1, T2, T3> = Triple<T1, T2, T3>(_1, _2, _3)


/**
 * Returns a new Arity1 based on this Tuple1.
 **/
fun <T1> Tuple1<T1>.toArity(): Arity1<T1> = Arity1<T1>(this._1())

/**
 * Returns a new Arity2 based on this Tuple2.
 **/
fun <T1, T2> Tuple2<T1, T2>.toArity(): Arity2<T1, T2> = Arity2<T1, T2>(this._1(), this._2())

/**
 * Returns a new Arity3 based on this Tuple3.
 **/
fun <T1, T2, T3> Tuple3<T1, T2, T3>.toArity(): Arity3<T1, T2, T3> = Arity3<T1, T2, T3>(this._1(), this._2(), this._3())

/**
 * Returns a new Arity4 based on this Tuple4.
 **/
fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.toArity(): Arity4<T1, T2, T3, T4> = Arity4<T1, T2, T3, T4>(this._1(), this._2(), this._3(), this._4())

/**
 * Returns a new Arity5 based on this Tuple5.
 **/
fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.toArity(): Arity5<T1, T2, T3, T4, T5> = Arity5<T1, T2, T3, T4, T5>(this._1(), this._2(), this._3(), this._4(), this._5())

/**
 * Returns a new Arity6 based on this Tuple6.
 **/
fun <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6>.toArity(): Arity6<T1, T2, T3, T4, T5, T6> = Arity6<T1, T2, T3, T4, T5, T6>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6())

/**
 * Returns a new Arity7 based on this Tuple7.
 **/
fun <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7>.toArity(): Arity7<T1, T2, T3, T4, T5, T6, T7> = Arity7<T1, T2, T3, T4, T5, T6, T7>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7())

/**
 * Returns a new Arity8 based on this Tuple8.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>.toArity(): Arity8<T1, T2, T3, T4, T5, T6, T7, T8> = Arity8<T1, T2, T3, T4, T5, T6, T7, T8>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8())

/**
 * Returns a new Arity9 based on this Tuple9.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.toArity(): Arity9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Arity9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9())

/**
 * Returns a new Arity10 based on this Tuple10.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>.toArity(): Arity10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Arity10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10())

/**
 * Returns a new Arity11 based on this Tuple11.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>.toArity(): Arity11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> = Arity11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11())

/**
 * Returns a new Arity12 based on this Tuple12.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>.toArity(): Arity12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> = Arity12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12())

/**
 * Returns a new Arity13 based on this Tuple13.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>.toArity(): Arity13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> = Arity13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13())

/**
 * Returns a new Arity14 based on this Tuple14.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>.toArity(): Arity14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> = Arity14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14())

/**
 * Returns a new Arity15 based on this Tuple15.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>.toArity(): Arity15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> = Arity15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15())

/**
 * Returns a new Arity16 based on this Tuple16.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>.toArity(): Arity16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> = Arity16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16())

/**
 * Returns a new Arity17 based on this Tuple17.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>.toArity(): Arity17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> = Arity17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17())

/**
 * Returns a new Arity18 based on this Tuple18.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>.toArity(): Arity18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> = Arity18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18())

/**
 * Returns a new Arity19 based on this Tuple19.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>.toArity(): Arity19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> = Arity19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19())

/**
 * Returns a new Arity20 based on this Tuple20.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>.toArity(): Arity20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> = Arity20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), this._20())

/**
 * Returns a new Arity21 based on this Tuple21.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>.toArity(): Arity21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> = Arity21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), this._20(), this._21())

/**
 * Returns a new Arity22 based on this Tuple22.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>.toArity(): Arity22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> = Arity22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7(), this._8(), this._9(), this._10(), this._11(), this._12(), this._13(), this._14(), this._15(), this._16(), this._17(), this._18(), this._19(), this._20(), this._21(), this._22())

/**
 * Returns a new Tuple1 based on this Arity1.
 **/
fun <T1> Arity1<T1>.toTuple(): Tuple1<T1> = Tuple1<T1>(this._1)

/**
 * Returns a new Tuple2 based on this Arity2.
 **/
fun <T1, T2> Arity2<T1, T2>.toTuple(): Tuple2<T1, T2> = Tuple2<T1, T2>(this._1, this._2)

/**
 * Returns a new Tuple3 based on this Arity3.
 **/
fun <T1, T2, T3> Arity3<T1, T2, T3>.toTuple(): Tuple3<T1, T2, T3> = Tuple3<T1, T2, T3>(this._1, this._2, this._3)

/**
 * Returns a new Tuple4 based on this Arity4.
 **/
fun <T1, T2, T3, T4> Arity4<T1, T2, T3, T4>.toTuple(): Tuple4<T1, T2, T3, T4> = Tuple4<T1, T2, T3, T4>(this._1, this._2, this._3, this._4)

/**
 * Returns a new Tuple5 based on this Arity5.
 **/
fun <T1, T2, T3, T4, T5> Arity5<T1, T2, T3, T4, T5>.toTuple(): Tuple5<T1, T2, T3, T4, T5> = Tuple5<T1, T2, T3, T4, T5>(this._1, this._2, this._3, this._4, this._5)

/**
 * Returns a new Tuple6 based on this Arity6.
 **/
fun <T1, T2, T3, T4, T5, T6> Arity6<T1, T2, T3, T4, T5, T6>.toTuple(): Tuple6<T1, T2, T3, T4, T5, T6> = Tuple6<T1, T2, T3, T4, T5, T6>(this._1, this._2, this._3, this._4, this._5, this._6)

/**
 * Returns a new Tuple7 based on this Arity7.
 **/
fun <T1, T2, T3, T4, T5, T6, T7> Arity7<T1, T2, T3, T4, T5, T6, T7>.toTuple(): Tuple7<T1, T2, T3, T4, T5, T6, T7> = Tuple7<T1, T2, T3, T4, T5, T6, T7>(this._1, this._2, this._3, this._4, this._5, this._6, this._7)

/**
 * Returns a new Tuple8 based on this Arity8.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8> Arity8<T1, T2, T3, T4, T5, T6, T7, T8>.toTuple(): Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8)

/**
 * Returns a new Tuple9 based on this Arity9.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Arity9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.toTuple(): Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9)

/**
 * Returns a new Tuple10 based on this Arity10.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Arity10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>.toTuple(): Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10)

/**
 * Returns a new Tuple11 based on this Arity11.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Arity11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>.toTuple(): Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> = Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11)

/**
 * Returns a new Tuple12 based on this Arity12.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Arity12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>.toTuple(): Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> = Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12)

/**
 * Returns a new Tuple13 based on this Arity13.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Arity13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>.toTuple(): Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> = Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12, this._13)

/**
 * Returns a new Tuple14 based on this Arity14.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Arity14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>.toTuple(): Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> = Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12, this._13, this._14)

/**
 * Returns a new Tuple15 based on this Arity15.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Arity15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>.toTuple(): Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> = Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12, this._13, this._14, this._15)

/**
 * Returns a new Tuple16 based on this Arity16.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Arity16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>.toTuple(): Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> = Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12, this._13, this._14, this._15, this._16)

/**
 * Returns a new Tuple17 based on this Arity17.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Arity17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>.toTuple(): Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> = Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12, this._13, this._14, this._15, this._16, this._17)

/**
 * Returns a new Tuple18 based on this Arity18.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Arity18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>.toTuple(): Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> = Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12, this._13, this._14, this._15, this._16, this._17, this._18)

/**
 * Returns a new Tuple19 based on this Arity19.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Arity19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>.toTuple(): Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> = Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12, this._13, this._14, this._15, this._16, this._17, this._18, this._19)

/**
 * Returns a new Tuple20 based on this Arity20.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Arity20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>.toTuple(): Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> = Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12, this._13, this._14, this._15, this._16, this._17, this._18, this._19, this._20)

/**
 * Returns a new Tuple21 based on this Arity21.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Arity21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>.toTuple(): Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> = Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12, this._13, this._14, this._15, this._16, this._17, this._18, this._19, this._20, this._21)

/**
 * Returns a new Tuple22 based on this Arity22.
 **/
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Arity22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>.toTuple(): Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> = Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8, this._9, this._10, this._11, this._12, this._13, this._14, this._15, this._16, this._17, this._18, this._19, this._20, this._21, this._22)
