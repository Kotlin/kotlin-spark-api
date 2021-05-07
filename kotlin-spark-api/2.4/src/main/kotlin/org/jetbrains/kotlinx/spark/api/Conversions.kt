/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 2.4+ (Scala 2.12)
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
@file:Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments", "unused")

package org.jetbrains.kotlinx.spark.api

import scala.collection.JavaConversions
import java.util.*
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

/**
 * @see JavaConversions.asScalaIterator for more information.
 */
fun <A> Iterator<A>.asScalaIterator(): ScalaIterator<A> = JavaConversions.asScalaIterator<A>(this)

/**
 * @see JavaConversions.enumerationAsScalaIterator for more information.
 */
fun <A> Enumeration<A>.asScalaIterator(): ScalaIterator<A> = JavaConversions.enumerationAsScalaIterator<A>(this)

/**
 * @see JavaConversions.iterableAsScalaIterable for more information.
 */
fun <A> Iterable<A>.asScalaIterable(): ScalaIterable<A> = JavaConversions.iterableAsScalaIterable<A>(this)

/**
 * @see JavaConversions.collectionAsScalaIterable for more information.
 */
fun <A> Collection<A>.asScalaIterable(): ScalaIterable<A> = JavaConversions.collectionAsScalaIterable<A>(this)

/**
 * @see JavaConversions.asScalaBuffer for more information.
 */
fun <A> MutableList<A>.asScalaMutableBuffer(): ScalaMutableBuffer<A> = JavaConversions.asScalaBuffer<A>(this)

/**
 * @see JavaConversions.asScalaSet for more information.
 */
fun <A> MutableSet<A>.asScalaMutableSet(): ScalaMutableSet<A> = JavaConversions.asScalaSet<A>(this)

/**
 * @see JavaConversions.mapAsScalaMap for more information.
 */
fun <A, B> MutableMap<A, B>.asScalaMutableMap(): ScalaMutableMap<A, B> = JavaConversions.mapAsScalaMap<A, B>(this)

/**
 * @see JavaConversions.dictionaryAsScalaMap for more information.
 */
fun <A, B> Map<A, B>.asScalaMap(): ScalaMap<A, B> = JavaConversions.mapAsScalaMap<A, B>(this)

/**
 * @see JavaConversions.mapAsScalaConcurrentMap for more information.
 */
fun <A, B> ConcurrentMap<A, B>.asScalaConcurrentMap(): ScalaConcurrentMap<A, B> = JavaConversions.mapAsScalaConcurrentMap<A, B>(this)

/**
 * @see JavaConversions.dictionaryAsScalaMap for more information.
 */
fun <A, B> Dictionary<A, B>.asScalaMap(): ScalaMutableMap<A, B> = JavaConversions.dictionaryAsScalaMap<A, B>(this)

/**
 * @see JavaConversions.propertiesAsScalaMap for more information.
 */
fun Properties.asScalaMap(): ScalaMutableMap<String, String> = JavaConversions.propertiesAsScalaMap(this)


/**
 * @see JavaConversions.asJavaIterator for more information.
 */
fun <A> ScalaIterator<A>.asKotlinIterator(): Iterator<A> = JavaConversions.asJavaIterator<A>(this)

/**
 * @see JavaConversions.asJavaEnumeration for more information.
 */
fun <A> ScalaIterator<A>.asKotlinEnumeration(): Enumeration<A> = JavaConversions.asJavaEnumeration<A>(this)

/**
 * @see JavaConversions.asJavaIterable for more information.
 */
fun <A> ScalaIterable<A>.asKotlinIterable(): Iterable<A> = JavaConversions.asJavaIterable<A>(this)

/**
 * @see JavaConversions.asJavaCollection for more information.
 */
fun <A> ScalaIterable<A>.asKotlinCollection(): Collection<A> = JavaConversions.asJavaCollection<A>(this)

/**
 * @see JavaConversions.bufferAsJavaList for more information.
 */
fun <A> ScalaMutableBuffer<A>.asKotlinMutableList(): MutableList<A> = JavaConversions.bufferAsJavaList<A>(this)

/**
 * @see JavaConversions.mutableSeqAsJavaList for more information.
 */
fun <A> ScalaMutableSeq<A>.asKotlinMutableList(): MutableList<A> = JavaConversions.mutableSeqAsJavaList<A>(this)

/**
 * @see JavaConversions.seqAsJavaList for more information.
 */
fun <A> ScalaSeq<A>.asKotlinList(): List<A> = JavaConversions.seqAsJavaList<A>(this)

/**
 * @see JavaConversions.mutableSetAsJavaSet for more information.
 */
fun <A> ScalaMutableSet<A>.asKotlinMutableSet(): MutableSet<A> = JavaConversions.mutableSetAsJavaSet<A>(this)

/**
 * @see JavaConversions.setAsJavaSet for more information.
 */
fun <A> ScalaSet<A>.asKotlinSet(): Set<A> = JavaConversions.setAsJavaSet<A>(this)

/**
 * @see JavaConversions.mutableMapAsJavaMap for more information.
 */
fun <A, B> ScalaMutableMap<A, B>.asKotlinMutableMap(): MutableMap<A, B> = JavaConversions.mutableMapAsJavaMap<A, B>(this)

/**
 * @see JavaConversions.asJavaDictionary for more information.
 */
fun <A, B> ScalaMutableMap<A, B>.asKotlinDictionary(): Dictionary<A, B> = JavaConversions.asJavaDictionary<A, B>(this)

/**
 * @see JavaConversions.mapAsJavaMap for more information.
 */
fun <A, B> ScalaMap<A, B>.asKotlinMap(): Map<A, B> = JavaConversions.mapAsJavaMap<A, B>(this)

/**
 * @see JavaConversions.mapAsJavaConcurrentMap for more information.
 */
fun <A, B> ScalaConcurrentMap<A, B>.asKotlinConcurrentMap(): ConcurrentMap<A, B> = JavaConversions.mapAsJavaConcurrentMap<A, B>(this)

