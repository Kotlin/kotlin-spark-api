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
package org.jetbrains.spark.api

class PartitioningIterator<T>(
    private val source: Iterator<T>,
    private val size: Int,
    private val cutIncomplete: Boolean = false
) : AbstractIterator<List<T>>() {
    override fun computeNext() {
        if (!source.hasNext()) return done()
        val interimResult = arrayListOf<T>()
        repeat(size) {
            if (source.hasNext()) interimResult.add(source.next())
            else return if (cutIncomplete) done() else setNext(interimResult)
        }
        setNext(interimResult)
    }
}

class MappingIterator<T, R>(
    private val self: Iterator<T>,
    private val func: (T) -> R
) : AbstractIterator<R>() {
    override fun computeNext() = if (self.hasNext()) setNext(func(self.next())) else done()
}

class FilteringIterator<T>(
    private val source: Iterator<T>,
    private val predicate: (T) -> Boolean
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
fun <T, R> Iterator<T>.map(func: (T) -> R): Iterator<R> = MappingIterator(this, func)

fun <T> Iterator<T>.filter(func: (T) -> Boolean): Iterator<T> = FilteringIterator(this, func)

fun <T> Iterator<T>.partition(size: Int): Iterator<List<T>> = PartitioningIterator(this, size)

