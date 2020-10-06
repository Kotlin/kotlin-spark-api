@file:Suppress("NOTHING_TO_INLINE")

package org.jetbrains.kotlinx.spark.api

import scala.collection.JavaConversions
import java.util.concurrent.ConcurrentMap
import java.util.Enumeration
import java.util.Dictionary
import java.util.Properties
import scala.collection.Iterator as ScalaIterator
import scala.collection.concurrent.Map as ScalaConcurrentMap
import scala.collection.mutable.Map as ScalaMutableMap
import scala.collection.mutable.Set as ScalaMutableSet
import scala.collection.Iterable as ScalaIterable
import scala.collection.mutable.Buffer as ScalaMutableBuffer
import scala.collection.Seq as ScalaSequence
import scala.collection.Set as ScalaSet
import scala.collection.mutable.Seq as ScalaMutableSequence
import scala.collection.Map as ScalaMap

/**
 * @see JavaConversions.asScalaIterator for more information.
 */
inline fun <A> Iterator<A>.asScalaIterator():ScalaIterator<A> = JavaConversions.asScalaIterator(this)

/**
 * @see JavaConversions.enumerationAsScalaIterator for more information.
 */
inline fun <A> Enumeration<A>.asScalaIterator():ScalaIterator<A> = JavaConversions.enumerationAsScalaIterator(this)

/**
 * @see JavaConversions.iterableAsScalaIterable for more information.
 */
inline fun <A> Iterable<A>.asScalaIterable():ScalaIterable<A> = JavaConversions.iterableAsScalaIterable(this)

/**
 * @see JavaConversions.collectionAsScalaIterable for more information.
 */
inline fun <A> Collection<A>.asScalaIterable():ScalaIterable<A> = JavaConversions.collectionAsScalaIterable(this)

/**
 * @see JavaConversions.asScalaBuffer for more information.
 */
inline fun <A> MutableList<A>.asScalaMutableBuffer():ScalaMutableBuffer<A> = JavaConversions.asScalaBuffer(this)

/**
 * @see JavaConversions.asScalaSet for more information.
 */
inline fun <A> MutableSet<A>.asScalaMutableSet():ScalaMutableSet<A> = JavaConversions.asScalaSet(this)

/**
 * @see JavaConversions.mapAsScalaMap for more information.
 */
inline fun <A, B> MutableMap<A, B>.asScalaMutableMap():ScalaMutableMap<A, B> = JavaConversions.mapAsScalaMap(this)

/**
 * @see JavaConversions.mapAsScalaConcurrentMap for more information.
 */
inline fun <A, B> ConcurrentMap<A, B>.asScalaConcurrentMap():ScalaConcurrentMap<A, B> = JavaConversions.mapAsScalaConcurrentMap(this)

/**
 * @see JavaConversions.dictionaryAsScalaMap for more information.
 */
inline fun <A, B> Dictionary<A, B>.asScalaMap():ScalaMutableMap<A, B> = JavaConversions.dictionaryAsScalaMap(this)

/**
 * @see JavaConversions.propertiesAsScalaMap for more information.
 */
inline fun Properties.asScalaMap():ScalaMutableMap<String, String> = JavaConversions.propertiesAsScalaMap(this)

/**
 * @see JavaConversions.asJavaIterator for more information.
 */
inline fun <A> ScalaIterator<A>.asIterator():Iterator<A> = JavaConversions.asJavaIterator(this)

/**
 * @see JavaConversions.asJavaEnumeration for more information.
 */
inline fun <A> ScalaIterator<A>.asEnumeration(): Enumeration<A> = JavaConversions.asJavaEnumeration(this)

/**
 * @see JavaConversions.asJavaIterable for more information.
 */
inline fun <A> ScalaIterable<A>.asIterable():Iterable<A> = JavaConversions.asJavaIterable(this)

/**
 * @see JavaConversions.asJavaCollection for more information.
 */
inline fun <A> ScalaIterable<A>.asCollection():Collection<A> = JavaConversions.asJavaCollection(this)

/**
 * @see JavaConversions.bufferAsJavaList for more information.
 */
inline fun <A> ScalaMutableBuffer<A>.asMutableList():MutableList<A> = JavaConversions.bufferAsJavaList(this)

/**
 * @see JavaConversions.mutableSeqAsJavaList for more information.
 */
inline fun <A> ScalaMutableSequence<A>.asMutableList():MutableList<A> = JavaConversions.mutableSeqAsJavaList(this)

/**
 * @see JavaConversions.seqAsJavaList for more information.
 */
inline fun <A> ScalaSequence<A>.asList():List<A> = JavaConversions.seqAsJavaList(this)

/**
 * @see JavaConversions.mutableSetAsJavaSet for more information.
 */
inline fun <A> ScalaMutableSet<A>.asMutableSet():MutableSet<A> = JavaConversions.mutableSetAsJavaSet(this)

/**
 * @see JavaConversions.setAsJavaSet for more information.
 */
inline fun <A> ScalaSet<A>.asSet():Set<A> = JavaConversions.setAsJavaSet(this)

/**
 * @see JavaConversions.mutableMapAsJavaMap for more information.
 */
inline fun <A, B> ScalaMutableMap<A, B>.asMutableMap():MutableMap<A, B> = JavaConversions.mutableMapAsJavaMap(this)

/**
 * @see JavaConversions.asJavaDictionary for more information.
 */
inline fun <A, B> ScalaMutableMap<A, B>.asDictionary(): Dictionary<A, B> = JavaConversions.asJavaDictionary(this)

/**
 * @see JavaConversions.mapAsJavaMap for more information.
 */
inline fun <A, B> ScalaMap<A, B>.asMap():Map<A, B> = JavaConversions.mapAsJavaMap(this)

/**
 * @see JavaConversions.mapAsJavaConcurrentMap for more information.
 */
inline fun <A, B> ScalaConcurrentMap<A, B>.asConcurrentMap(): ConcurrentMap<A, B> = JavaConversions.mapAsJavaConcurrentMap(this)
