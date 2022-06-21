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
@file:Suppress("unused")

package org.jetbrains.kotlinx.spark.api

import org.apache.spark.sql.*
import org.apache.spark.sql.types.DataType
import scala.collection.Seq
import scala.collection.mutable.WrappedArray
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import org.apache.spark.sql.expressions.UserDefinedFunction as SparkUserDefinedFunction

/** Unwraps [DataTypeWithClass]. */
fun DataType.unWrap(): DataType =
    when (this) {
        is DataTypeWithClass -> DataType.fromJson(dt().json())
        else -> this
    }

/**
 * Checks if [this] is of a valid type for a UDF, otherwise it throws a [TypeOfUDFParameterNotSupportedException]
 */
@PublishedApi
internal fun KClass<*>.checkForValidType(parameterName: String) {
    if (this == String::class || isSubclassOf(WrappedArray::class) || isSubclassOf(Seq::class))
        return // Most of the time we need strings or WrappedArrays/Seqs

    if (isSubclassOf(Iterable::class)
        || java.isArray
        || isSubclassOf(Char::class)
        || isSubclassOf(Map::class)
        || isSubclassOf(Array::class)
        || isSubclassOf(ByteArray::class)
        || isSubclassOf(CharArray::class)
        || isSubclassOf(ShortArray::class)
        || isSubclassOf(IntArray::class)
        || isSubclassOf(LongArray::class)
        || isSubclassOf(FloatArray::class)
        || isSubclassOf(DoubleArray::class)
        || isSubclassOf(BooleanArray::class)
    ) throw TypeOfUDFParameterNotSupportedException(this, parameterName)
}

/**
 * An exception thrown when the UDF is generated with illegal types for the parameters
 */
class TypeOfUDFParameterNotSupportedException(kClass: KClass<*>, parameterName: String) : IllegalArgumentException(
    "Parameter $parameterName is subclass of ${kClass.qualifiedName}. If you need to process an array use ${Seq::class.qualifiedName}. You can convert any typed array/list-like column using [asSeq()]."
)

@JvmName("arrayColumnAsSeq")
fun <DsType, T> TypedColumn<DsType, Array<T>>.asSeq(): TypedColumn<DsType, WrappedArray<T>> = typed()
@JvmName("iterableColumnAsSeq")
fun <DsType, T, I : Iterable<T>> TypedColumn<DsType, I>.asSeq(): TypedColumn<DsType, WrappedArray<T>> = typed()
@JvmName("byteArrayColumnAsSeq")
fun <DsType> TypedColumn<DsType, ByteArray>.asSeq(): TypedColumn<DsType, WrappedArray<Byte>> = typed()
@JvmName("charArrayColumnAsSeq")
fun <DsType> TypedColumn<DsType, CharArray>.asSeq(): TypedColumn<DsType, WrappedArray<Char>> = typed()
@JvmName("shortArrayColumnAsSeq")
fun <DsType> TypedColumn<DsType, ShortArray>.asSeq(): TypedColumn<DsType, WrappedArray<Short>> = typed()
@JvmName("intArrayColumnAsSeq")
fun <DsType> TypedColumn<DsType, IntArray>.asSeq(): TypedColumn<DsType, WrappedArray<Int>> = typed()
@JvmName("longArrayColumnAsSeq")
fun <DsType> TypedColumn<DsType, LongArray>.asSeq(): TypedColumn<DsType, WrappedArray<Long>> = typed()
@JvmName("floatArrayColumnAsSeq")
fun <DsType> TypedColumn<DsType, FloatArray>.asSeq(): TypedColumn<DsType, WrappedArray<Float>> = typed()
@JvmName("doubleArrayColumnAsSeq")
fun <DsType> TypedColumn<DsType, DoubleArray>.asSeq(): TypedColumn<DsType, WrappedArray<Double>> = typed()
@JvmName("booleanArrayColumnAsSeq")
fun <DsType> TypedColumn<DsType, BooleanArray>.asSeq(): TypedColumn<DsType, WrappedArray<Boolean>> = typed()


/**
 * Registers a user-defined function (UDF) with name, for a UDF that's already defined using the Dataset
 * API (i.e. of type [NamedUserDefinedFunction]).
 * @see UDFRegistration.register
 */
inline fun <Return, reified NamedUdf : NamedUserDefinedFunction<Return, *>> UDFRegistration.register(
    namedUdf: NamedUdf,
): NamedUdf = namedUdf.copy(udf = register(namedUdf.name, namedUdf.udf))

inline fun <Return, reified NamedUdf : NamedUserDefinedFunction<Return, *>> UDFRegistration.register(
    name: String,
    udf: UserDefinedFunction<Return, NamedUdf>,
): NamedUdf = udf.withName(name).copy(udf = register(name, udf.udf))

/**
 * Typed wrapper around [SparkUserDefinedFunction] with defined encoder.
 *
 * @param Return the return type of the udf
 * @param NamedUdf a type reference to the named version of the [SparkUserDefinedFunction] implementing class
 */
sealed interface UserDefinedFunction<Return, NamedUdf> {
    val udf: SparkUserDefinedFunction
    val encoder: Encoder<Return>

    /** Returns true when the UDF can return a nullable value. */
    val nullable: Boolean get() = udf.nullable()

    /** Returns true iff the UDF is deterministic, i.e. the UDF produces the same output given the same input. */
    val deterministic: Boolean get() = udf.deterministic()

    /** Converts this [UserDefinedFunction] to a [NamedUserDefinedFunction]. */
    fun withName(name: String): NamedUdf

    /**
     * Converts this [UserDefinedFunction] to a [NamedUserDefinedFunction].
     * @see withName
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): NamedUdf
}

/**
 * Typed and named wrapper around [SparkUserDefinedFunction] with defined encoder.
 *
 * @param Return    the return type of the udf
 * @param This      a self reference to the named version of the [SparkUserDefinedFunction] implementing class.
 *                  Unfortunately needed to allow functions to treat any [NamedTypedUserDefinedFunction] as a normal [TypedUserDefinedFunction].
 */
sealed interface NamedUserDefinedFunction<Return, This> : UserDefinedFunction<Return, This> {
    val name: String
}

/** Copy method for all [NamedUserDefinedFunction] functions. */
inline fun <R, reified T : NamedUserDefinedFunction<R, *>> T.copy(
    name: String = this.name,
    udf: SparkUserDefinedFunction = this.udf,
    encoder: Encoder<R> = this.encoder,
): T = T::class.primaryConstructor!!.run {
    callBy(
        parameters.associateWith {
            when (it.name) {
                NamedUserDefinedFunction<*, *>::name.name -> name
                NamedUserDefinedFunction<*, *>::udf.name -> udf
                NamedUserDefinedFunction<*, *>::encoder.name -> encoder
                else -> error("Wrong arguments")
            }
        }
    )
}

