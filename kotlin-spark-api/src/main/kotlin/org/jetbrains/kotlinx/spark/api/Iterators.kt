/*-
 * =LICENSE=
 * Kotlin Spark API
 * ----------
 * Copyright (C) 2019 - 2020 JetBrains
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
 * This file contains several ways to wrap and modify iterators lazily.
 * This includes mapping, filtering, and partitioning.
 */

package org.jetbrains.kotlinx.spark.api

/** Partitions the values of the iterator lazily in groups of [size]. */
class PartitioningIterator<T>(
    private val source: Iterator<T>,
    private val size: Int,
    private val cutIncomplete: Boolean = false,
) : AbstractIterator<List<T>>() {

    override fun computeNext() {
        if (!source.hasNext()) return done()
        val interimResult = arrayListOf<T>()
        repeat(size) {
            if (source.hasNext())
                interimResult.add(source.next())
            else
                return if (cutIncomplete)
                    done()
                else
                    setNext(interimResult)
        }
        setNext(interimResult)
    }

}

/** Maps the values of the iterator lazily using [func]. */
class MappingIterator<T, R>(
    private val source: Iterator<T>,
    private val func: (T) -> R,
) : AbstractIterator<R>() {

    override fun computeNext(): Unit =
        if (source.hasNext())
            setNext(func(source.next()))
        else
            done()
}

/** Filters the values of the iterator lazily using [predicate]. */
class FilteringIterator<T>(
    private val source: Iterator<T>,
    private val predicate: (T) -> Boolean,
) : AbstractIterator<T>() {

    override fun computeNext() {
        while (source.hasNext()) {
            val next = source.next()
            if (predicate(next)) {
                setNext(next)
                return
            }
        }
        done()
    }

}

/** Maps the values of the iterator lazily using [func]. */
fun <T, R> Iterator<T>.map(func: (T) -> R): Iterator<R> = MappingIterator(this, func)

/** Filters the values of the iterator lazily using [predicate]. */
fun <T> Iterator<T>.filter(predicate: (T) -> Boolean): Iterator<T> = FilteringIterator(this, predicate)

/** Partitions the values of the iterator lazily in groups of [size]. */
fun <T> Iterator<T>.partition(size: Int): Iterator<List<T>> = PartitioningIterator(this, size)

