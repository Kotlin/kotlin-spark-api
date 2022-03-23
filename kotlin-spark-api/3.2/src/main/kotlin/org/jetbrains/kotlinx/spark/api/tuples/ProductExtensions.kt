/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 3.2+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2022 JetBrains
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
package org.jetbrains.kotlinx.spark.api.tuples

import scala.Product
import scala.collection.JavaConverters
import kotlin.jvm.Throws

/**
 * Extra extensions for Scala [Product]s such as Tuples.
 *
 * For example:
 *
 * ```kotlin
 * 1 in tupleOf(1, 2, 3) == true
 *
 * for (x in tupleOf("a", "b", "c")) { ... }
 *
 * val a: List<Any?> = tupleOf(1, "a", 3L).asIterable().toList()
 *
 * tupleOf(1, 2, 3).size == 3
 *
 * tupleOf(1, 2, 3)[0] == 1
 *
 * tupleOf(1, 1, 2)[1..2] == tupleOf(1, 2, 2)[0..1]
 * ```
 *
 */

/** Tests whether this iterator contains a given value as an element.
 *  Note: may not terminate for infinite iterators.
 *
 *  @param item  the element to test.
 *  @return     `true` if this iterator produces some value that
 *               is equal (as determined by `==`) to `elem`, `false` otherwise.
 *  @note Reuse: After calling this method, one should discard the iterator it was called on.
 *               Using it is undefined and subject to change.
 */
operator fun Product.contains(item: Any?): Boolean = productIterator().contains(item)

/**
 * An iterator over all the elements of this product.
 *  @return     in the default implementation, an `Iterator<Any?>`
 */
operator fun Product.iterator(): Iterator<Any?> = JavaConverters.asJavaIterator(productIterator())

/**
 * Converts this product to an `Any?` iterable.
 */
fun Product.asIterable(): Iterable<Any?> = object : Iterable<Any?> {
    override fun iterator(): Iterator<Any?> = JavaConverters.asJavaIterator(productIterator())
}

/** The size of this product.
 *  @return     for a product `A(x,,1,,, ..., x,,k,,)`, returns `k`
 */
val Product.size: Int
    get() = productArity()

/** The n'th element of this product, 0-based.  In other words, for a
 *  product `A(x,,1,,, ..., x,,k,,)`, returns `x,,(n+1),,` where `0 <= n < k`.
 *
 *  @param    n   the index of the element to return
 *  @throws       IndexOutOfBoundsException
 *  @return       the element `n` elements after the first element
 */
@Throws(IndexOutOfBoundsException::class)
operator fun Product.get(n: Int): Any? = productElement(n)

/** The n'th element of this product, 0-based.  In other words, for a
 *  product `A(x,,1,,, ..., x,,k,,)`, returns `x,,(n+1),,` where `0 <= n < k`.
 *
 *  @param    n   the index of the element to return
 *  @return       the element `n` elements after the first element, `null` if out of bounds
 */
fun Product.getOrNull(n: Int): Any? = if (n in 0 until size) productElement(n) else null

/** The n'th element of this product, 0-based.  In other words, for a
 *  product `A(x,,1,,, ..., x,,k,,)`, returns `x,,(n+1),,` where `0 <= n < k`.
 *  The result is cast to the given type [T].
 *
 *  @param    n   the index of the element to return
 *  @throws       IndexOutOfBoundsException
 *  @throws       ClassCastException
 *  @return       the element `n` elements after the first element
 */
@Suppress("UNCHECKED_CAST")
@Throws(IndexOutOfBoundsException::class, ClassCastException::class)
fun <T> Product.getAs(n: Int): T = productElement(n) as T

/** The n'th element of this product, 0-based.  In other words, for a
 *  product `A(x,,1,,, ..., x,,k,,)`, returns `x,,(n+1),,` where `0 <= n < k`.
 *  The result is cast to the given type [T].
 *
 *  @param    n   the index of the element to return
 *  @return       the element `n` elements after the first element, `null` if out of bounds or unable to be cast
 */
@Suppress("UNCHECKED_CAST")
fun <T> Product.getAsOrNull(n: Int): T? = getOrNull(n) as? T

/** The range of n'th elements of this product, 0-based.  In other words, for a
 *  product `A(x,,1,,, ..., x,,k,,)`, returns `x,,(n+1),,` where `0 <= n < k`.
 *
 *  @param        indexRange   the indices of the elements to return
 *  @throws       IndexOutOfBoundsException
 *  @return       the elements in [indexRange]
 */
@Throws(IndexOutOfBoundsException::class)
operator fun Product.get(indexRange: IntRange): List<Any?> = indexRange.map(::get)

/** The range of n'th elements of this product, 0-based.  In other words, for a
 *  product `A(x,,1,,, ..., x,,k,,)`, returns `x,,(n+1),,` where `0 <= n < k`.
 *
 *  @param        indexRange   the indices of the elements to return
 *  @return       the elements in [indexRange], `null` if out of bounds
 */
fun Product.getOrNull(indexRange: IntRange): List<Any?> = indexRange.map(::getOrNull)

/** The range of n'th elements of this product, 0-based.  In other words, for a
 *  product `A(x,,1,,, ..., x,,k,,)`, returns `x,,(n+1),,` where `0 <= n < k`.
 *  The results are cast to the given type [T].
 *
 *  @param        indexRange   the indices of the elements to return
 *  @throws       IndexOutOfBoundsException
 *  @throws       ClassCastException
 *  @return       the elements in [indexRange]
 */
@Throws(IndexOutOfBoundsException::class, ClassCastException::class)
fun <T> Product.getAs(indexRange: IntRange): List<T> = indexRange.map(::getAs)

/** The range of n'th elements of this product, 0-based.  In other words, for a
 *  product `A(x,,1,,, ..., x,,k,,)`, returns `x,,(n+1),,` where `0 <= n < k`.
 *  The results are cast to the given type [T].
 *
 *  @param        indexRange   the indices of the elements to return
 *  @return       the elements in [indexRange], `null` is out of bounds or unable to be cast
 */
fun <T> Product.getAsOrNull(indexRange: IntRange): List<T?> = indexRange.map(::getAsOrNull)
