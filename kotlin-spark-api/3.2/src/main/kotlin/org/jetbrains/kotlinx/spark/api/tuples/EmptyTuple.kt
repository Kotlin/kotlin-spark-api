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

import scala.*
import java.io.Serializable

/**
 * Just as in Scala3, we provide the [EmptyTuple]. It is the result of dropping the last item from a [Tuple1]
 * or when calling `tupleOf()` for instance.
 *
 * It can also be used to create tuples like:
 * ```kotlin
 * val tuple: Tuple3<Int, Long, String> = t + 1 + 5L + "test"
 * ```
 * if you really want to.
 */

object EmptyTuple : Product, Serializable {
    override fun canEqual(that: Any?): Boolean = that == EmptyTuple
    override fun productElement(n: Int): Nothing = throw IndexOutOfBoundsException("EmptyTuple has no members")
    override fun productArity(): Int = 0
    override fun toString(): String = "()"
}

public val t: EmptyTuple = EmptyTuple
public fun emptyTuple(): EmptyTuple = EmptyTuple
