package org.jetbrains.kotlinx.spark.api


fun <T> emptySeq(): scala.collection.Seq<T> = scala.collection.`Seq$`.`MODULE$`.empty<T>()

fun <T> seqOf(vararg elements: T): scala.collection.Seq<T> =
    if (elements.isEmpty()) emptySeq()
    else scala.collection.`Seq$`.`MODULE$`.from(elements.asIterable().asScalaIterable())

fun <T> emptyImmutableSeq(): scala.collection.immutable.Seq<T> =
    scala.collection.immutable.`Seq$`.`MODULE$`.empty<T>()

fun <T> immutableSeqOf(vararg elements: T): scala.collection.immutable.Seq<T> =
    if (elements.isEmpty()) emptyImmutableSeq()
    else scala.collection.immutable.`Seq$`.`MODULE$`.from(elements.asIterable().asScalaIterable())

fun <T> emptyMutableSeq(): scala.collection.mutable.Seq<T> =
    scala.collection.mutable.`Seq$`.`MODULE$`.empty<T>()

fun <T> mutableSeqOf(vararg elements: T): scala.collection.mutable.Seq<T> =
    if (elements.isEmpty()) emptyMutableSeq()
    else scala.collection.mutable.`Seq$`.`MODULE$`.from(elements.asIterable().asScalaIterable())
