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

@file:Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments", "unused", "DEPRECATION")

package org.jetbrains.kotlinx.spark.api

import org.apache.spark.api.java.Optional
import scala.* 
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
import org.apache.spark.streaming.State

/** Returns state value if it exists, else `null`. */
fun <T> State<T>.getOrNull(): T? = if (exists()) get() else null

/** Returns state value if it exists, else [other]. */
fun <T> State<T>.getOrElse(other: T): T = if (exists()) get() else other


/** Converts Scala [Option] to Kotlin nullable. */
fun <T> Option<T>.getOrNull(): T? = getOrElse(null)

/** Get if available else [other]. */
fun <T> Option<T>.getOrElse(other: T): T = getOrElse { other }

/** Converts nullable value to Scala [Option]. */
fun <T> T?.toOption(): Option<T> = Option.apply(this)

/** Converts Scala [Option] to Java [Optional]. */
fun <T> Option<T>.toOptional(): Optional<T> = Optional.ofNullable(getOrNull())


/** Converts [Optional] to Kotlin nullable. */
fun <T> Optional<T>.getOrNull(): T? = orNull()

/** Get if available else [other]. */
fun <T> Optional<T>.getOrElse(other: T): T = orElse(other)

/** Converts nullable value to [Optional]. */
fun <T> T?.toOptional(): Optional<T> = Optional.ofNullable(this)

/** Converts Java [Optional] to Scala [Option]. */
fun <T> Optional<T>.toOption(): Option<T> = Option.apply(getOrNull())

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.asScalaIterator for more information. */
//#endif
fun <A> Iterator<A>.asScalaIterator(): ScalaIterator<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A>(this)
    //#else
    //$scala.collection.JavaConverters.asScalaIterator<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.enumerationAsScalaIterator for more information. */
//#endif
fun <A> Enumeration<A>.asScalaIterator(): ScalaIterator<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A>(this)
    //#else
    //$scala.collection.JavaConverters.enumerationAsScalaIterator<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.iterableAsScalaIterable for more information. */
//#endif
fun <A> Iterable<A>.asScalaIterable(): ScalaIterable<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A>(this)
    //#else
    //$scala.collection.JavaConverters.iterableAsScalaIterable<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.collectionAsScalaIterable for more information. */
//#endif
fun <A> Collection<A>.asScalaIterable(): ScalaIterable<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A>(this)
    //#else
    //$scala.collection.JavaConverters.collectionAsScalaIterable<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.asScalaBuffer for more information. */
//#endif
fun <A> MutableList<A>.asScalaMutableBuffer(): ScalaMutableBuffer<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A>(this)
    //#else
    //$scala.collection.JavaConverters.asScalaBuffer<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.asScalaSet for more information. */
//#endif
fun <A> MutableSet<A>.asScalaMutableSet(): ScalaMutableSet<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A>(this)
    //#else
    //$scala.collection.JavaConverters.asScalaSet<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.mapAsScalaMap for more information. */
//#endif
fun <A, B> MutableMap<A, B>.asScalaMutableMap(): ScalaMutableMap<A, B> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A, B>(this)
    //#else
    //$scala.collection.JavaConverters.mapAsScalaMap<A, B>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.mapAsScalaMap for more information. */
//#endif
fun <A, B> Map<A, B>.asScalaMap(): ScalaMap<A, B> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A, B>(this)
    //#else
    //$scala.collection.JavaConverters.mapAsScalaMap<A, B>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.mapAsScalaConcurrentMap for more information. */
//#endif
fun <A, B> ConcurrentMap<A, B>.asScalaConcurrentMap(): ScalaConcurrentMap<A, B> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A, B>(this)
    //#else
    //$scala.collection.JavaConverters.mapAsScalaConcurrentMap<A, B>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.dictionaryAsScalaMap for more information. */
//#endif
fun <A, B> Dictionary<A, B>.asScalaMap(): ScalaMutableMap<A, B> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A, B>(this)
    //#else
    //$scala.collection.JavaConverters.dictionaryAsScalaMap<A, B>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asScala for more information. */
//#else
//$/** @see scala.collection.JavaConverters.propertiesAsScalaMap for more information. */
//#endif
fun Properties.asScalaMap(): ScalaMutableMap<String, String> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala(this)
    //#else
    //$scala.collection.JavaConverters.propertiesAsScalaMap(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJava for more information. */
//#else
//$/** @see scala.collection.JavaConverters.asJavaIterator for more information. */
//#endif
fun <A> ScalaIterator<A>.asKotlinIterator(): Iterator<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJava<A>(this)
    //#else
    //$scala.collection.JavaConverters.asJavaIterator<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJavaEnumeration for more information. */
//#else
//$/** @see scala.collection.JavaConverters.asJavaEnumeration for more information. */
//#endif
fun <A> ScalaIterator<A>.asKotlinEnumeration(): Enumeration<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJavaEnumeration<A>(this)
    //#else
    //$scala.collection.JavaConverters.asJavaEnumeration<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJava for more information. */
//#else
//$/** @see scala.collection.JavaConverters.asJavaIterable for more information. */
//#endif
fun <A> ScalaIterable<A>.asKotlinIterable(): Iterable<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJava<A>(this)
    //#else
    //$scala.collection.JavaConverters.asJavaIterable<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJavaCollection for more information. */
//#else
//$/** @see scala.collection.JavaConverters.asJavaCollection for more information. */
//#endif
fun <A> ScalaIterable<A>.asKotlinCollection(): Collection<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJavaCollection<A>(this)
    //#else
    //$scala.collection.JavaConverters.asJavaCollection<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJava for more information. */
//#else
//$/** @see scala.collection.JavaConverters.bufferAsJavaList for more information. */
//#endif
fun <A> ScalaMutableBuffer<A>.asKotlinMutableList(): MutableList<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJava<A>(this)
    //#else
    //$scala.collection.JavaConverters.bufferAsJavaList<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJava for more information. */
//#else
//$/** @see scala.collection.JavaConverters.mutableSeqAsJavaList for more information. */
//#endif
fun <A> ScalaMutableSeq<A>.asKotlinMutableList(): MutableList<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJava<A>(this)
    //#else
    //$scala.collection.JavaConverters.mutableSeqAsJavaList<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJava for more information. */
//#else
//$/** @see scala.collection.JavaConverters.seqAsJavaList for more information. */
//#endif
fun <A> ScalaSeq<A>.asKotlinList(): List<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJava<A>(this)
    //#else
    //$scala.collection.JavaConverters.seqAsJavaList<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJava for more information. */
//#else
//$/** @see scala.collection.JavaConverters.mutableSetAsJavaSet for more information. */
//#endif
fun <A> ScalaMutableSet<A>.asKotlinMutableSet(): MutableSet<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJava<A>(this)
    //#else
    //$scala.collection.JavaConverters.mutableSetAsJavaSet<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJava for more information. */
//#else
//$/** @see scala.collection.JavaConverters.setAsJavaSet for more information. */
//#endif
fun <A> ScalaSet<A>.asKotlinSet(): Set<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJava<A>(this)
    //#else
    //$scala.collection.JavaConverters.setAsJavaSet<A>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJava for more information. */
//#else
//$/** @see scala.collection.JavaConverters.mutableMapAsJavaMap for more information. */
//#endif
fun <A, B> ScalaMutableMap<A, B>.asKotlinMutableMap(): MutableMap<A, B> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJava<A, B>(this)
    //#else
    //$scala.collection.JavaConverters.mutableMapAsJavaMap<A, B>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJavaDictionary for more information. */
//#else
//$/** @see scala.collection.JavaConverters.asJavaDictionary for more information. */
//#endif
fun <A, B> ScalaMutableMap<A, B>.asKotlinDictionary(): Dictionary<A, B> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJavaDictionary<A, B>(this)
    //#else
    //$scala.collection.JavaConverters.asJavaDictionary<A, B>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJava for more information. */
//#else
//$/** @see scala.collection.JavaConverters.mapAsJavaMap for more information. */
//#endif
fun <A, B> ScalaMap<A, B>.asKotlinMap(): Map<A, B> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJava<A, B>(this)
    //#else
    //$scala.collection.JavaConverters.mapAsJavaMap<A, B>(this)
    //#endif

//#if scalaCompat >= 2.13
/** @see scala.jdk.javaapi.CollectionConverters.asJava for more information. */
//#else
//$/** @see scala.collection.JavaConverters.mapAsJavaConcurrentMap for more information. */
//#endif
fun <A, B> ScalaConcurrentMap<A, B>.asKotlinConcurrentMap(): ConcurrentMap<A, B> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asJava<A, B>(this)
    //#else
    //$scala.collection.JavaConverters.mapAsJavaConcurrentMap<A, B>(this)
    //#endif


/**
 * Returns a new [Arity2] based on the arguments in the current [Pair].
 */
@Deprecated("Use Scala tuples instead.", ReplaceWith("this.toTuple()", "scala.Tuple2"))
fun <T1, T2> Pair<T1, T2>.toArity(): Arity2<T1, T2> = Arity2<T1, T2>(first, second)

/**
 * Returns a new [Pair] based on the arguments in the current [Arity2].
 */
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2> Arity2<T1, T2>.toPair(): Pair<T1, T2> = Pair<T1, T2>(_1, _2)

/**
 * Returns a new [Arity3] based on the arguments in the current [Triple].
 */
@Deprecated("Use Scala tuples instead.", ReplaceWith("this.toTuple()", "scala.Tuple3"))
fun <T1, T2, T3> Triple<T1, T2, T3>.toArity(): Arity3<T1, T2, T3> = Arity3<T1, T2, T3>(first, second, third)

/**
 * Returns a new [Triple] based on the arguments in the current [Arity3].
 */
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3> Arity3<T1, T2, T3>.toTriple(): Triple<T1, T2, T3> = Triple<T1, T2, T3>(_1, _2, _3)


/**
 * Returns a new Arity1 based on this Tuple1.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1> Tuple1<T1>.toArity(): Arity1<T1> = Arity1<T1>(this._1())

/**
 * Returns a new Arity2 based on this Tuple2.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2> Tuple2<T1, T2>.toArity(): Arity2<T1, T2> = Arity2<T1, T2>(this._1(), this._2())

/**
 * Returns a new Arity3 based on this Tuple3.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3> Tuple3<T1, T2, T3>.toArity(): Arity3<T1, T2, T3> = Arity3<T1, T2, T3>(this._1(), this._2(), this._3())

/**
 * Returns a new Arity4 based on this Tuple4.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.toArity(): Arity4<T1, T2, T3, T4> =
    Arity4<T1, T2, T3, T4>(this._1(), this._2(), this._3(), this._4())

/**
 * Returns a new Arity5 based on this Tuple5.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.toArity(): Arity5<T1, T2, T3, T4, T5> =
    Arity5<T1, T2, T3, T4, T5>(this._1(), this._2(), this._3(), this._4(), this._5())

/**
 * Returns a new Arity6 based on this Tuple6.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6>.toArity(): Arity6<T1, T2, T3, T4, T5, T6> =
    Arity6<T1, T2, T3, T4, T5, T6>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6())

/**
 * Returns a new Arity7 based on this Tuple7.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7>.toArity(): Arity7<T1, T2, T3, T4, T5, T6, T7> =
    Arity7<T1, T2, T3, T4, T5, T6, T7>(this._1(), this._2(), this._3(), this._4(), this._5(), this._6(), this._7())

/**
 * Returns a new Arity8 based on this Tuple8.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>.toArity(): Arity8<T1, T2, T3, T4, T5, T6, T7, T8> =
    Arity8<T1, T2, T3, T4, T5, T6, T7, T8>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8()
    )

/**
 * Returns a new Arity9 based on this Tuple9.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.toArity(): Arity9<T1, T2, T3, T4, T5, T6, T7, T8, T9> =
    Arity9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9()
    )

/**
 * Returns a new Arity10 based on this Tuple10.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>.toArity(): Arity10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> =
    Arity10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10()
    )

/**
 * Returns a new Arity11 based on this Tuple11.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>.toArity(): Arity11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> =
    Arity11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11()
    )

/**
 * Returns a new Arity12 based on this Tuple12.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>.toArity(): Arity12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> =
    Arity12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12()
    )

/**
 * Returns a new Arity13 based on this Tuple13.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>.toArity(): Arity13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> =
    Arity13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12(),
        this._13()
    )

/**
 * Returns a new Arity14 based on this Tuple14.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>.toArity(): Arity14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> =
    Arity14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12(),
        this._13(),
        this._14()
    )

/**
 * Returns a new Arity15 based on this Tuple15.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>.toArity(): Arity15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> =
    Arity15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12(),
        this._13(),
        this._14(),
        this._15()
    )

/**
 * Returns a new Arity16 based on this Tuple16.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>.toArity(): Arity16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> =
    Arity16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12(),
        this._13(),
        this._14(),
        this._15(),
        this._16()
    )

/**
 * Returns a new Arity17 based on this Tuple17.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>.toArity(): Arity17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> =
    Arity17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12(),
        this._13(),
        this._14(),
        this._15(),
        this._16(),
        this._17()
    )

/**
 * Returns a new Arity18 based on this Tuple18.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>.toArity(): Arity18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> =
    Arity18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12(),
        this._13(),
        this._14(),
        this._15(),
        this._16(),
        this._17(),
        this._18()
    )

/**
 * Returns a new Arity19 based on this Tuple19.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>.toArity(): Arity19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> =
    Arity19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12(),
        this._13(),
        this._14(),
        this._15(),
        this._16(),
        this._17(),
        this._18(),
        this._19()
    )

/**
 * Returns a new Arity20 based on this Tuple20.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>.toArity(): Arity20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> =
    Arity20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12(),
        this._13(),
        this._14(),
        this._15(),
        this._16(),
        this._17(),
        this._18(),
        this._19(),
        this._20()
    )

/**
 * Returns a new Arity21 based on this Tuple21.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>.toArity(): Arity21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> =
    Arity21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12(),
        this._13(),
        this._14(),
        this._15(),
        this._16(),
        this._17(),
        this._18(),
        this._19(),
        this._20(),
        this._21()
    )

/**
 * Returns a new Arity22 based on this Tuple22.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>.toArity(): Arity22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> =
    Arity22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>(
        this._1(),
        this._2(),
        this._3(),
        this._4(),
        this._5(),
        this._6(),
        this._7(),
        this._8(),
        this._9(),
        this._10(),
        this._11(),
        this._12(),
        this._13(),
        this._14(),
        this._15(),
        this._16(),
        this._17(),
        this._18(),
        this._19(),
        this._20(),
        this._21(),
        this._22()
    )

/**
 * Returns a new Tuple1 based on this Arity1.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1> Arity1<T1>.toTuple(): Tuple1<T1> = Tuple1<T1>(this._1)

/**
 * Returns a new Tuple2 based on this Arity2.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2> Arity2<T1, T2>.toTuple(): Tuple2<T1, T2> = Tuple2<T1, T2>(this._1, this._2)

/**
 * Returns a new Tuple3 based on this Arity3.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3> Arity3<T1, T2, T3>.toTuple(): Tuple3<T1, T2, T3> = Tuple3<T1, T2, T3>(this._1, this._2, this._3)

/**
 * Returns a new Tuple4 based on this Arity4.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4> Arity4<T1, T2, T3, T4>.toTuple(): Tuple4<T1, T2, T3, T4> =
    Tuple4<T1, T2, T3, T4>(this._1, this._2, this._3, this._4)

/**
 * Returns a new Tuple5 based on this Arity5.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5> Arity5<T1, T2, T3, T4, T5>.toTuple(): Tuple5<T1, T2, T3, T4, T5> =
    Tuple5<T1, T2, T3, T4, T5>(this._1, this._2, this._3, this._4, this._5)

/**
 * Returns a new Tuple6 based on this Arity6.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6> Arity6<T1, T2, T3, T4, T5, T6>.toTuple(): Tuple6<T1, T2, T3, T4, T5, T6> =
    Tuple6<T1, T2, T3, T4, T5, T6>(this._1, this._2, this._3, this._4, this._5, this._6)

/**
 * Returns a new Tuple7 based on this Arity7.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7> Arity7<T1, T2, T3, T4, T5, T6, T7>.toTuple(): Tuple7<T1, T2, T3, T4, T5, T6, T7> =
    Tuple7<T1, T2, T3, T4, T5, T6, T7>(this._1, this._2, this._3, this._4, this._5, this._6, this._7)

/**
 * Returns a new Tuple8 based on this Arity8.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8> Arity8<T1, T2, T3, T4, T5, T6, T7, T8>.toTuple(): Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> =
    Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(this._1, this._2, this._3, this._4, this._5, this._6, this._7, this._8)

/**
 * Returns a new Tuple9 based on this Arity9.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Arity9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.toTuple(): Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> =
    Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9
    )

/**
 * Returns a new Tuple10 based on this Arity10.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Arity10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>.toTuple(): Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> =
    Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10
    )

/**
 * Returns a new Tuple11 based on this Arity11.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Arity11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>.toTuple(): Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> =
    Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11
    )

/**
 * Returns a new Tuple12 based on this Arity12.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Arity12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>.toTuple(): Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> =
    Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12
    )

/**
 * Returns a new Tuple13 based on this Arity13.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Arity13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>.toTuple(): Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> =
    Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12,
        this._13
    )

/**
 * Returns a new Tuple14 based on this Arity14.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Arity14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>.toTuple(): Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> =
    Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12,
        this._13,
        this._14
    )

/**
 * Returns a new Tuple15 based on this Arity15.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Arity15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>.toTuple(): Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> =
    Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12,
        this._13,
        this._14,
        this._15
    )

/**
 * Returns a new Tuple16 based on this Arity16.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Arity16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>.toTuple(): Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> =
    Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12,
        this._13,
        this._14,
        this._15,
        this._16
    )

/**
 * Returns a new Tuple17 based on this Arity17.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Arity17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>.toTuple(): Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> =
    Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12,
        this._13,
        this._14,
        this._15,
        this._16,
        this._17
    )

/**
 * Returns a new Tuple18 based on this Arity18.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Arity18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>.toTuple(): Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> =
    Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12,
        this._13,
        this._14,
        this._15,
        this._16,
        this._17,
        this._18
    )

/**
 * Returns a new Tuple19 based on this Arity19.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Arity19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>.toTuple(): Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> =
    Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12,
        this._13,
        this._14,
        this._15,
        this._16,
        this._17,
        this._18,
        this._19
    )

/**
 * Returns a new Tuple20 based on this Arity20.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Arity20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>.toTuple(): Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> =
    Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12,
        this._13,
        this._14,
        this._15,
        this._16,
        this._17,
        this._18,
        this._19,
        this._20
    )

/**
 * Returns a new Tuple21 based on this Arity21.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Arity21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>.toTuple(): Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> =
    Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12,
        this._13,
        this._14,
        this._15,
        this._16,
        this._17,
        this._18,
        this._19,
        this._20,
        this._21
    )

/**
 * Returns a new Tuple22 based on this Arity22.
 **/
@Deprecated("Use Scala tuples instead.", ReplaceWith(""))
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Arity22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>.toTuple(): Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> =
    Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>(
        this._1,
        this._2,
        this._3,
        this._4,
        this._5,
        this._6,
        this._7,
        this._8,
        this._9,
        this._10,
        this._11,
        this._12,
        this._13,
        this._14,
        this._15,
        this._16,
        this._17,
        this._18,
        this._19,
        this._20,
        this._21,
        this._22
    )
