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
import scala.collection.immutable.Seq as ScalaImmutableSeq
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
//$/** @see scala.collection.JavaConverters.iterableAsScalaIterable for more information. */
//#endif
fun <A> Iterable<A>.asScalaSeq(): ScalaImmutableSeq<A> =
    //#if scalaCompat >= 2.13
    scala.jdk.javaapi.CollectionConverters.asScala<A>(this).toSeq()
    //#else
    //$scala.collection.JavaConverters.iterableAsScalaIterable<A>(this).toSeq()
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
