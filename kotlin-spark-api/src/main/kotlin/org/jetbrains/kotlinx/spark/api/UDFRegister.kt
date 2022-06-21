/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 2.4+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2021 JetBrains
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
 * This file contains functions to register UDFs easily from Kotlin.
 */

@file:Suppress("DuplicatedCode")

package org.jetbrains.kotlinx.spark.api

import org.apache.spark.sql.Column
import org.apache.spark.sql.DataTypeWithClass
import org.apache.spark.sql.UDFRegistration
import org.apache.spark.sql.api.java.*
import org.apache.spark.sql.functions
import org.apache.spark.sql.types.DataType
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf
import scala.collection.Seq


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
    if (this == String::class || isSubclassOf(Seq::class)
    //#if scala.compat.version < 2.13
    //$|| isSubclassOf(scala.collection.mutable.WrappedArray::class)
    //#endif
    )
        return // Most of the time we need strings or WrappedArrays

    if (isSubclassOf(Iterable::class)
        || java.isArray
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
    "Parameter $parameterName is subclass of ${kClass.qualifiedName}. If you need to process an array use ${Seq::class.qualifiedName}."
)

/**
 * A wrapper for a UDF with 0 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper0(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(): Column {
        return functions.callUDF(udfName)
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified R> UDFRegistration.register(name: String, noinline func: () -> R): UDFWrapper0 {
    register(name, UDF0(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper0(name)
}

/**
 * A wrapper for a UDF with 1 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper1(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(param0: Column): Column {
        return functions.callUDF(udfName, param0)
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified R> UDFRegistration.register(name: String, noinline func: (T0) -> R): UDFWrapper1 {
    T0::class.checkForValidType("T0")
    register(name, UDF1(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper1(name)
}

/**
 * A wrapper for a UDF with 2 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper2(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(param0: Column, param1: Column): Column {
        return functions.callUDF(udfName, param0, param1)
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1) -> R,
): UDFWrapper2 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    register(name, UDF2(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper2(name)
}

/**
 * A wrapper for a UDF with 3 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper3(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column): Column {
        return functions.callUDF(udfName, param0, param1, param2)
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2) -> R,
): UDFWrapper3 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    register(name, UDF3(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper3(name)
}

/**
 * A wrapper for a UDF with 4 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper4(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column): Column {
        return functions.callUDF(udfName, param0, param1, param2, param3)
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3) -> R,
): UDFWrapper4 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    register(name, UDF4(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper4(name)
}

/**
 * A wrapper for a UDF with 5 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper5(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column): Column {
        return functions.callUDF(udfName, param0, param1, param2, param3, param4)
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4) -> R,
): UDFWrapper5 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    register(name, UDF5(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper5(name)
}

/**
 * A wrapper for a UDF with 6 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper6(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
    ): Column {
        return functions.callUDF(udfName, param0, param1, param2, param3, param4, param5)
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5) -> R,
): UDFWrapper6 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    register(name, UDF6(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper6(name)
}

/**
 * A wrapper for a UDF with 7 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper7(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
    ): Column {
        return functions.callUDF(udfName, param0, param1, param2, param3, param4, param5, param6)
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6) -> R,
): UDFWrapper7 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    register(name, UDF7(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper7(name)
}

/**
 * A wrapper for a UDF with 8 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper8(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
    ): Column {
        return functions.callUDF(udfName, param0, param1, param2, param3, param4, param5, param6, param7)
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7) -> R,
): UDFWrapper8 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    register(name, UDF8(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper8(name)
}

/**
 * A wrapper for a UDF with 9 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper9(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
    ): Column {
        return functions.callUDF(udfName, param0, param1, param2, param3, param4, param5, param6, param7, param8)
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8) -> R,
): UDFWrapper9 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    register(name, UDF9(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper9(name)
}

/**
 * A wrapper for a UDF with 10 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper10(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
): UDFWrapper10 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    register(name, UDF10(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper10(name)
}

/**
 * A wrapper for a UDF with 11 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper11(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
): UDFWrapper11 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    register(name, UDF11(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper11(name)
}

/**
 * A wrapper for a UDF with 12 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper12(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
): UDFWrapper12 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    register(name, UDF12(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper12(name)
}

/**
 * A wrapper for a UDF with 13 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper13(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
        param12: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11,
            param12
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
): UDFWrapper13 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    T12::class.checkForValidType("T12")
    register(name, UDF13(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper13(name)
}

/**
 * A wrapper for a UDF with 14 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper14(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
        param12: Column,
        param13: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11,
            param12,
            param13
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
): UDFWrapper14 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    T12::class.checkForValidType("T12")
    T13::class.checkForValidType("T13")
    register(name, UDF14(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper14(name)
}

/**
 * A wrapper for a UDF with 15 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper15(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
        param12: Column,
        param13: Column,
        param14: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11,
            param12,
            param13,
            param14
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
): UDFWrapper15 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    T12::class.checkForValidType("T12")
    T13::class.checkForValidType("T13")
    T14::class.checkForValidType("T14")
    register(name, UDF15(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper15(name)
}

/**
 * A wrapper for a UDF with 16 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper16(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
        param12: Column,
        param13: Column,
        param14: Column,
        param15: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11,
            param12,
            param13,
            param14,
            param15
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
): UDFWrapper16 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    T12::class.checkForValidType("T12")
    T13::class.checkForValidType("T13")
    T14::class.checkForValidType("T14")
    T15::class.checkForValidType("T15")
    register(name, UDF16(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper16(name)
}

/**
 * A wrapper for a UDF with 17 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper17(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
        param12: Column,
        param13: Column,
        param14: Column,
        param15: Column,
        param16: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11,
            param12,
            param13,
            param14,
            param15,
            param16
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
): UDFWrapper17 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    T12::class.checkForValidType("T12")
    T13::class.checkForValidType("T13")
    T14::class.checkForValidType("T14")
    T15::class.checkForValidType("T15")
    T16::class.checkForValidType("T16")
    register(name, UDF17(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper17(name)
}

/**
 * A wrapper for a UDF with 18 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper18(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
        param12: Column,
        param13: Column,
        param14: Column,
        param15: Column,
        param16: Column,
        param17: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11,
            param12,
            param13,
            param14,
            param15,
            param16,
            param17
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
): UDFWrapper18 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    T12::class.checkForValidType("T12")
    T13::class.checkForValidType("T13")
    T14::class.checkForValidType("T14")
    T15::class.checkForValidType("T15")
    T16::class.checkForValidType("T16")
    T17::class.checkForValidType("T17")
    register(name, UDF18(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper18(name)
}

/**
 * A wrapper for a UDF with 19 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper19(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
        param12: Column,
        param13: Column,
        param14: Column,
        param15: Column,
        param16: Column,
        param17: Column,
        param18: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11,
            param12,
            param13,
            param14,
            param15,
            param16,
            param17,
            param18
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
): UDFWrapper19 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    T12::class.checkForValidType("T12")
    T13::class.checkForValidType("T13")
    T14::class.checkForValidType("T14")
    T15::class.checkForValidType("T15")
    T16::class.checkForValidType("T16")
    T17::class.checkForValidType("T17")
    T18::class.checkForValidType("T18")
    register(name, UDF19(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper19(name)
}

/**
 * A wrapper for a UDF with 20 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper20(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
        param12: Column,
        param13: Column,
        param14: Column,
        param15: Column,
        param16: Column,
        param17: Column,
        param18: Column,
        param19: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11,
            param12,
            param13,
            param14,
            param15,
            param16,
            param17,
            param18,
            param19
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
): UDFWrapper20 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    T12::class.checkForValidType("T12")
    T13::class.checkForValidType("T13")
    T14::class.checkForValidType("T14")
    T15::class.checkForValidType("T15")
    T16::class.checkForValidType("T16")
    T17::class.checkForValidType("T17")
    T18::class.checkForValidType("T18")
    T19::class.checkForValidType("T19")
    register(name, UDF20(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper20(name)
}

/**
 * A wrapper for a UDF with 21 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper21(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
        param12: Column,
        param13: Column,
        param14: Column,
        param15: Column,
        param16: Column,
        param17: Column,
        param18: Column,
        param19: Column,
        param20: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11,
            param12,
            param13,
            param14,
            param15,
            param16,
            param17,
            param18,
            param19,
            param20
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
): UDFWrapper21 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    T12::class.checkForValidType("T12")
    T13::class.checkForValidType("T13")
    T14::class.checkForValidType("T14")
    T15::class.checkForValidType("T15")
    T16::class.checkForValidType("T16")
    T17::class.checkForValidType("T17")
    T18::class.checkForValidType("T18")
    T19::class.checkForValidType("T19")
    T20::class.checkForValidType("T20")
    register(name, UDF21(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper21(name)
}

/**
 * A wrapper for a UDF with 22 arguments.
 * @property udfName the name of the UDF
 */
class UDFWrapper22(private val udfName: String) {
    /**
     * Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns.
     */
    operator fun invoke(
        param0: Column,
        param1: Column,
        param2: Column,
        param3: Column,
        param4: Column,
        param5: Column,
        param6: Column,
        param7: Column,
        param8: Column,
        param9: Column,
        param10: Column,
        param11: Column,
        param12: Column,
        param13: Column,
        param14: Column,
        param15: Column,
        param16: Column,
        param17: Column,
        param18: Column,
        param19: Column,
        param20: Column,
        param21: Column,
    ): Column {
        return functions.callUDF(
            udfName,
            param0,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10,
            param11,
            param12,
            param13,
            param14,
            param15,
            param16,
            param17,
            param18,
            param19,
            param20,
            param21
        )
    }
}

/**
 * Registers the [func] with its [name] in [this].
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> UDFRegistration.register(
    name: String,
    noinline func: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R,
): UDFWrapper22 {
    T0::class.checkForValidType("T0")
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")
    T10::class.checkForValidType("T10")
    T11::class.checkForValidType("T11")
    T12::class.checkForValidType("T12")
    T13::class.checkForValidType("T13")
    T14::class.checkForValidType("T14")
    T15::class.checkForValidType("T15")
    T16::class.checkForValidType("T16")
    T17::class.checkForValidType("T17")
    T18::class.checkForValidType("T18")
    T19::class.checkForValidType("T19")
    T20::class.checkForValidType("T20")
    T21::class.checkForValidType("T21")
    register(name, UDF22(func), schema(typeOf<R>()).unWrap())
    return UDFWrapper22(name)
}
