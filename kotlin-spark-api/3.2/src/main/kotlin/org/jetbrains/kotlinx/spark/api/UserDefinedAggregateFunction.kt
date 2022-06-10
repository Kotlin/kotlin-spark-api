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
package org.jetbrains.kotlinx.spark.api

import org.apache.spark.sql.Encoder
import org.apache.spark.sql.UDFRegistration
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.functions
import java.io.Serializable
import kotlin.reflect.typeOf

/** Creates an [Aggregator] in functional manner.
 *
 * @param zero A zero value for this aggregation. Should satisfy the property that any b + zero = b.
 * @param reduce Combine two values to produce a new value. For performance, the function may modify `b` and
 *      return it instead of constructing new object for b.
 * @param merge Merge two intermediate values.
 * @param finish Transform the output of the reduction.
 * @param bufferEncoder Optional. Specifies the `Encoder` for the intermediate value type.
 * @param outputEncoder Optional. Specifies the `Encoder` for the final output value type.
 * */
inline fun <reified IN, reified BUF, reified OUT> aggregatorOf(
    zero: BUF,
    noinline reduce: (b: BUF, a: IN) -> BUF,
    noinline merge: (b1: BUF, b2: BUF) -> BUF,
    noinline finish: (reduction: BUF) -> OUT,
    bufferEncoder: Encoder<BUF> = encoder(),
    outputEncoder: Encoder<OUT> = encoder(),
): Aggregator<IN, BUF, OUT> = object : Aggregator<IN, BUF, OUT>(), Serializable {
    override fun reduce(b: BUF, a: IN): BUF = reduce(b, a)
    override fun merge(b1: BUF, b2: BUF): BUF = merge(b1, b2)
    override fun finish(reduction: BUF): OUT = finish(reduction)
    override fun bufferEncoder(): Encoder<BUF> = bufferEncoder
    override fun zero(): BUF = zero
    override fun outputEncoder(): Encoder<OUT> = outputEncoder
}

/**
 * Obtains a [NamedUserDefinedFunction1] that wraps the given [agg] so that it may be used with Data Frames.
 * @see functions.udaf
 *
 * @param agg the given [Aggregator] to convert into a UDAF. Can also be created using [aggregatorOf].
 * @param name Optional. Tries to obtain name from the class of [agg] if not supplied.
 *      Use [udafUnnamed] if no name is wanted.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 *
 * @return a [NamedUserDefinedFunction1] that can be used as an aggregating expression
 *
 * @see udaf for a named variant.
 */
inline fun <reified IN, reified OUT, reified AGG : Aggregator<IN, *, OUT>> udaf(
    agg: AGG,
    name: String = agg::class.simpleName
        ?: error("Could not obtain name from [agg], either supply a name or use [udafUnnamed()]"),
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<IN, OUT> =
    udafUnnamed(agg = agg, nondeterministic = nondeterministic).withName(name)

/**
 * Obtains a [UserDefinedFunction1] that wraps the given [agg] so that it may be used with Data Frames.
 * @see functions.udaf
 *
 * @param agg the given [Aggregator] to convert into a UDAF. Can also be created using [aggregatorOf].
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 *
 * @return a [UserDefinedFunction1] that can be used as an aggregating expression
 *
 * @see udaf for a named variant.
 */
inline fun <reified IN, reified OUT, reified AGG : Aggregator<IN, *, OUT>> udafUnnamed(
    agg: AGG,
    nondeterministic: Boolean = false,
): UserDefinedFunction1<IN, OUT> {
    IN::class.checkForValidType("IN")
    OUT::class.checkForValidType("OUT")

    return UserDefinedFunction1(
        udf = functions.udaf(agg, encoder<IN>())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<OUT>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<OUT>(),
    )
}

/**
 * Obtains a [UserDefinedFunction1] created from an [Aggregator] created by the given arguments
 * so that it may be used with Data Frames.
 * @see functions.udaf
 * @see aggregatorOf
 *
 * @param zero A zero value for this aggregation. Should satisfy the property that any b + zero = b.
 * @param reduce Combine two values to produce a new value. For performance, the function may modify `b` and
 *      return it instead of constructing new object for b.
 * @param merge Merge two intermediate values.
 * @param finish Transform the output of the reduction.
 * @param bufferEncoder Optional. Specifies the `Encoder` for the intermediate value type.
 * @param outputEncoder Optional. Specifies the `Encoder` for the final output value type.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 *
 * @return a [UserDefinedFunction1] that can be used as an aggregating expression
 *
 * @see udaf for a named variant.
 */
inline fun <reified IN, reified BUF, reified OUT> udafUnnamed(
    zero: BUF,
    noinline reduce: (b: BUF, a: IN) -> BUF,
    noinline merge: (b1: BUF, b2: BUF) -> BUF,
    noinline finish: (reduction: BUF) -> OUT,
    bufferEncoder: Encoder<BUF> = encoder(),
    outputEncoder: Encoder<OUT> = encoder(),
    nondeterministic: Boolean = false,
): UserDefinedFunction1<IN, OUT> = udafUnnamed(
    agg = aggregatorOf(
        zero = zero,
        reduce = reduce,
        merge = merge,
        finish = finish,
        bufferEncoder = bufferEncoder,
        outputEncoder = outputEncoder,
    ),
    nondeterministic = nondeterministic,
)

/**
 * Obtains a [UserDefinedFunction1] created from an [Aggregator] created by the given arguments
 * so that it may be used with Data Frames.
 * @see functions.udaf
 * @see aggregatorOf
 *
 * @param zero A zero value for this aggregation. Should satisfy the property that any b + zero = b.
 * @param reduce Combine two values to produce a new value. For performance, the function may modify `b` and
 *      return it instead of constructing new object for b.
 * @param merge Merge two intermediate values.
 * @param finish Transform the output of the reduction.
 * @param bufferEncoder Optional. Specifies the `Encoder` for the intermediate value type.
 * @param outputEncoder Optional. Specifies the `Encoder` for the final output value type.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 *
 * @return a [UserDefinedFunction1] that can be used as an aggregating expression
 *
 * @see udaf for a named variant.
 */
inline fun <reified IN, reified BUF, reified OUT> udaf(
    zero: BUF,
    noinline reduce: (b: BUF, a: IN) -> BUF,
    noinline merge: (b1: BUF, b2: BUF) -> BUF,
    noinline finish: (reduction: BUF) -> OUT,
    bufferEncoder: Encoder<BUF> = encoder(),
    outputEncoder: Encoder<OUT> = encoder(),
    nondeterministic: Boolean = false,
): UserDefinedFunction1<IN, OUT> = udafUnnamed(
    zero = zero,
    reduce = reduce,
    merge = merge,
    finish = finish,
    bufferEncoder = bufferEncoder,
    outputEncoder = outputEncoder,
    nondeterministic = nondeterministic,
)


/**
 * Obtains a [NamedUserDefinedFunction1] that wraps the given [agg] so that it may be used with Data Frames.
 * so that it may be used with Data Frames.
 * @see functions.udaf
 * @see aggregatorOf
 *
 * @param name Optional. Name for the UDAF.
 * @param zero A zero value for this aggregation. Should satisfy the property that any b + zero = b.
 * @param reduce Combine two values to produce a new value. For performance, the function may modify `b` and
 *      return it instead of constructing new object for b.
 * @param merge Merge two intermediate values.
 * @param finish Transform the output of the reduction.
 * @param bufferEncoder Optional. Specifies the `Encoder` for the intermediate value type.
 * @param outputEncoder Optional. Specifies the `Encoder` for the final output value type.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 *
 * @return a [UserDefinedFunction1] that can be used as an aggregating expression
 *
 * @see udafUnnamed for an unnamed variant.
 */
inline fun <reified IN, reified BUF, reified OUT> udaf(
    name: String,
    zero: BUF,
    noinline reduce: (b: BUF, a: IN) -> BUF,
    noinline merge: (b1: BUF, b2: BUF) -> BUF,
    noinline finish: (reduction: BUF) -> OUT,
    bufferEncoder: Encoder<BUF> = encoder(),
    outputEncoder: Encoder<OUT> = encoder(),
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<IN, OUT> = udaf(
    agg = aggregatorOf(
        zero = zero,
        reduce = reduce,
        merge = merge,
        finish = finish,
        bufferEncoder = bufferEncoder,
        outputEncoder = outputEncoder,
    ),
    name = name,
    nondeterministic = nondeterministic,
)

/**
 * Registers [agg] as a UDAF for SQL. Returns the UDAF as [NamedUserDefinedFunction].
 * Obtains a [NamedUserDefinedFunction1] that wraps the given [agg] so that it may be used with Data Frames.
 * @see UDFRegistration.register
 * @see functions.udaf
 *
 * @param agg the given [Aggregator] to convert into a UDAF. Can also be created using [aggregatorOf].
 * @param name Optional. Tries to obtain name from the class of [agg] if not supplied.
 *      Use [udafUnnamed] if no name is wanted.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 *
 * @return a [NamedUserDefinedFunction1] that can be used as an aggregating expression
 */
inline fun <reified T1, reified R> UDFRegistration.register(
    agg: Aggregator<T1, *, R>,
    name: String = agg::class.simpleName
        ?: error("Could not create a name for this UDAF, please define one in this function call."),
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<T1, R> = register(
    udaf(agg = agg, name = name, nondeterministic = nondeterministic)
)

/**
 * Registers a UDAF for SQL based on the given arguments. Returns the UDAF as [NamedUserDefinedFunction].
 * Obtains a [NamedUserDefinedFunction1] that wraps the given [agg] so that it may be used with Data Frames.
 * @see UDFRegistration.register
 * @see functions.udaf
 *
 * @param name Optional. Name for the UDAF.
 * @param zero A zero value for this aggregation. Should satisfy the property that any b + zero = b.
 * @param reduce Combine two values to produce a new value. For performance, the function may modify `b` and
 *      return it instead of constructing new object for b.
 * @param merge Merge two intermediate values.
 * @param finish Transform the output of the reduction.
 * @param bufferEncoder Optional. Specifies the `Encoder` for the intermediate value type.
 * @param outputEncoder Optional. Specifies the `Encoder` for the final output value type.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 *
 * @return a [NamedUserDefinedFunction1] that can be used as an aggregating expression.
 */
inline fun <reified IN, reified BUF, reified OUT> UDFRegistration.register(
    name: String,
    zero: BUF,
    noinline reduce: (b: BUF, a: IN) -> BUF,
    noinline merge: (b1: BUF, b2: BUF) -> BUF,
    noinline finish: (reduction: BUF) -> OUT,
    bufferEncoder: Encoder<BUF> = encoder(),
    outputEncoder: Encoder<OUT> = encoder(),
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<IN, OUT> = register(
    udaf(
        zero = zero,
        reduce = reduce,
        merge = merge,
        finish = finish,
        bufferEncoder = bufferEncoder,
        outputEncoder = outputEncoder,
        name = name,
        nondeterministic = nondeterministic,
    )
)
