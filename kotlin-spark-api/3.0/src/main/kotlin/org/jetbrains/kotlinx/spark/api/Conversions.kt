@file:Suppress("NOTHING_TO_INLINE")

package org.jetbrains.kotlinx.spark.api

import scala.collection.JavaConverters
import java.util.*
import java.util.concurrent.ConcurrentMap
import scala.collection.Iterable as ScalaIterable
import scala.collection.Iterator as ScalaIterator
import scala.collection.Map as ScalaMap
import scala.collection.Seq as ScalaSequence
import scala.collection.Set as ScalaSet
import scala.collection.concurrent.Map as ScalaConcurrentMap
import scala.collection.mutable.Buffer as ScalaMutableBuffer
import scala.collection.mutable.Map as ScalaMutableMap
import scala.collection.mutable.Seq as ScalaMutableSequence
import scala.collection.mutable.Set as ScalaMutableSet

/**
 * @see JavaConverters.asScalaIterator for more information.
 */
fun <A> Iterator<A>.asScalaIterator(): ScalaIterator<A> = JavaConverters.asScalaIterator(this)

/**
 * @see JavaConverters.enumerationAsScalaIterator for more information.
 */
fun <A> Enumeration<A>.asScalaIterator(): ScalaIterator<A> = JavaConverters.enumerationAsScalaIterator(this)

/**
 * @see JavaConverters.iterableAsScalaIterable for more information.
 */
fun <A> Iterable<A>.asScalaIterable(): ScalaIterable<A> = JavaConverters.iterableAsScalaIterable(this)

/**
 * @see JavaConverters.collectionAsScalaIterable for more information.
 */
fun <A> Collection<A>.asScalaIterable(): ScalaIterable<A> = JavaConverters.collectionAsScalaIterable(this)

/**
 * @see JavaConverters.asScalaBuffer for more information.
 */
fun <A> MutableList<A>.asScalaMutableBuffer(): ScalaMutableBuffer<A> = JavaConverters.asScalaBuffer(this)

/**
 * @see JavaConverters.asScalaSet for more information.
 */
fun <A> MutableSet<A>.asScalaMutableSet(): ScalaMutableSet<A> = JavaConverters.asScalaSet(this)

/**
 * @see JavaConverters.mapAsScalaMap for more information.
 */
fun <A, B> MutableMap<A, B>.asScalaMutableMap(): ScalaMutableMap<A, B> = JavaConverters.mapAsScalaMap(this)

/**
 * @see JavaConverters.mapAsScalaConcurrentMap for more information.
 */
fun <A, B> ConcurrentMap<A, B>.asScalaConcurrentMap(): ScalaConcurrentMap<A, B> = JavaConverters.mapAsScalaConcurrentMap(this)

/**
 * @see JavaConverters.dictionaryAsScalaMap for more information.
 */
fun <A, B> Dictionary<A, B>.asScalaMap(): ScalaMutableMap<A, B> = JavaConverters.dictionaryAsScalaMap(this)

/**
 * @see JavaConverters.propertiesAsScalaMap for more information.
 */
fun Properties.asScalaMap(): ScalaMutableMap<String, String> = JavaConverters.propertiesAsScalaMap(this)

/**
 * @see JavaConverters.asJavaIterator for more information.
 */
fun <A> ScalaIterator<A>.asIterator(): Iterator<A> = JavaConverters.asJavaIterator(this)

/**
 * @see JavaConverters.asJavaEnumeration for more information.
 */
fun <A> ScalaIterator<A>.asEnumeration(): Enumeration<A> = JavaConverters.asJavaEnumeration(this)

/**
 * @see JavaConverters.asJavaIterable for more information.
 */
fun <A> ScalaIterable<A>.asIterable(): Iterable<A> = JavaConverters.asJavaIterable(this)

/**
 * @see JavaConverters.asJavaCollection for more information.
 */
fun <A> ScalaIterable<A>.asCollection(): Collection<A> = JavaConverters.asJavaCollection(this)

/**
 * @see JavaConverters.bufferAsJavaList for more information.
 */
fun <A> ScalaMutableBuffer<A>.asMutableList(): MutableList<A> = JavaConverters.bufferAsJavaList(this)

/**
 * @see JavaConverters.mutableSeqAsJavaList for more information.
 */
fun <A> ScalaMutableSequence<A>.asMutableList(): MutableList<A> = JavaConverters.mutableSeqAsJavaList(this)

/**
 * @see JavaConverters.seqAsJavaList for more information.
 */
fun <A> ScalaSequence<A>.asList(): List<A> = JavaConverters.seqAsJavaList(this)

/**
 * @see JavaConverters.mutableSetAsJavaSet for more information.
 */
fun <A> ScalaMutableSet<A>.asMutableSet(): MutableSet<A> = JavaConverters.mutableSetAsJavaSet(this)

/**
 * @see JavaConverters.setAsJavaSet for more information.
 */
fun <A> ScalaSet<A>.asSet(): Set<A> = JavaConverters.setAsJavaSet(this)

/**
 * @see JavaConverters.mutableMapAsJavaMap for more information.
 */
fun <A, B> ScalaMutableMap<A, B>.asMutableMap(): MutableMap<A, B> = JavaConverters.mutableMapAsJavaMap(this)

/**
 * @see JavaConverters.asJavaDictionary for more information.
 */
fun <A, B> ScalaMutableMap<A, B>.asDictionary(): Dictionary<A, B> = JavaConverters.asJavaDictionary(this)

/**
 * @see JavaConverters.mapAsJavaMap for more information.
 */
fun <A, B> ScalaMap<A, B>.asMap(): Map<A, B> = JavaConverters.mapAsJavaMap(this)

/**
 * @see JavaConverters.mapAsJavaConcurrentMap for more information.
 */
fun <A, B> ScalaConcurrentMap<A, B>.asConcurrentMap(): ConcurrentMap<A, B> = JavaConverters.mapAsJavaConcurrentMap(this)
