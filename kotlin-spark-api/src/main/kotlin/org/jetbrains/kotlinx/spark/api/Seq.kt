package org.jetbrains.kotlinx.spark.api

import scala.collection.immutable.`Seq$`.`MODULE$` as Seq
import scala.collection.immutable.Seq as Seq
import scala.collection.mutable.`Seq$`.`MODULE$` as MutableSeq
import scala.collection.mutable.Seq as MutableSeq

/** Returns a new empty immutable Seq. */
fun <T> emptySeq(): Seq<T> = Seq.empty<T>() as Seq<T>

/** Returns a new immutable Seq with the given elements. */
fun <T> seqOf(vararg elements: T): Seq<T> =
    if (elements.isEmpty())
        emptySeq()
    else
        Seq.newBuilder<T>().apply {
            for (it in elements)
                `$plus$eq`(it)
        }.result() as Seq<T>

/** Returns a new mutable Seq with the given elements. */
fun <T> emptyMutableSeq(): MutableSeq<T> = MutableSeq.empty<T>() as MutableSeq<T>

/** Returns a new mutable Seq with the given elements. */
fun <T> mutableSeqOf(vararg elements: T): MutableSeq<T> =
    if (elements.isEmpty())
        emptyMutableSeq()
    else
        MutableSeq.newBuilder<T>().apply {
            for (it in elements)
                `$plus$eq`(it)
        }.result() as MutableSeq<T>
