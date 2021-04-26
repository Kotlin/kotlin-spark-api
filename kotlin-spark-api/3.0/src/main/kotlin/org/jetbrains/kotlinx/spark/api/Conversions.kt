@file:Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")

package org.jetbrains.kotlinx.spark.api

import scala.collection.JavaConverters
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
fun <A, B> ConcurrentMap<A, B>.asScalaConcurrentMap(): ScalaConcurrentMap<A, B> = JavaConverters.mapAsScalaConcurrentMap<A, B>(this)

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
fun <A, B> ScalaConcurrentMap<A, B>.asKotlinConcurrentMap(): ConcurrentMap<A, B> = JavaConverters.mapAsJavaConcurrentMap<A, B>(this)

