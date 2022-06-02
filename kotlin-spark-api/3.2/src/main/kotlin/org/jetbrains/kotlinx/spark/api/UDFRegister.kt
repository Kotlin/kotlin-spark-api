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

import org.apache.spark.sql.UDFRegistration
import org.apache.spark.sql.api.java.UDF0
import org.apache.spark.sql.api.java.UDF1
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty0


inline fun <R, reified T : TypedUserDefinedFunction<R>> UDFRegistration.register(
    typedUdf: T,
): T = register(
    name = typedUdf.name ?: error("This UDF has no name defined yet, please define one in this function call."),
    typedUdf = typedUdf,
)
inline fun <R, reified T : TypedUserDefinedFunction<R>> UDFRegistration.register(
    typedUdf: T,
    name: String,
): T = typedUdf.copy(
    name = name,
    udf = register(name, typedUdf.udf),
)

// 0
@JvmName("register0")
inline fun <reified R> UDFRegistration.register(
    func: KProperty0<() -> R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
) = register(udf(name = name, nondeterministic = nondeterministic, func = func))

@JvmName("register0")
inline fun <reified R> UDFRegistration.register(
    func: KFunction0<R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
) = register(udf(name = name, nondeterministic = nondeterministic, func = func))

inline fun <reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF0<R>,
) = register(udf(name = name, nondeterministic = nondeterministic, func = func))

// 1
@JvmName("register1")
inline fun <reified T1, reified R> UDFRegistration.register(
    func: KProperty0<(T1) -> R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
) = register(udf(name = name, nondeterministic = nondeterministic, func = func))

@JvmName("register1")
inline fun <reified T1, reified R> UDFRegistration.register(
    func: KFunction1<T1, R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
) = register(udf(name = name, nondeterministic = nondeterministic, func = func))

inline fun <reified T1, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF1<T1, R>,
) = register(udf(name = name, nondeterministic = nondeterministic, func = func))

///**
// * A wrapper for a UDF with 0 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF0<R>(private val udfName: String, private val encoder: Encoder<R>) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(): TypedColumn<*, R> = functions.callUDF(udfName).`as`(encoder)
//}
//
///** Registers the [func] with its [name] in [this].
// *
// * NOTE: use like
// * ```kotlin
// * udf.register("someName") { ->
// *     ...
// * }
// * ```
// * to avoid overload resolution ambiguity.
// */
//inline fun <reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    func: UDF0<R>,
//): RegisteredUDF0<R> {
//    register(
//        name,
//        udf(func, schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF0(name, encoder())
//}
//
///**
// * A wrapper for a UDF with 1 argument.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF1<T1, R>(private val udfName: String, private val encoder: Encoder<R>) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(param1: TypedColumn<*, T1>): TypedColumn<*, R> =
//        functions.callUDF(udfName, param1).`as`(encoder)
//
//    operator fun invoke(param1: Column): Column = functions.callUDF(udfName, param1)
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    func: UDF1<T1, R>,
//): RegisteredUDF1<T1, R> {
//    T1::class.checkForValidType("T1")
//    register(
//        name,
//        udf(func, schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF1(name, encoder())
//}
//
///**
// * A wrapper for a UDF with 2 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF2<T1, T2, R>(private val udfName: String, private val encoder: Encoder<R>) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(param1: TypedColumn<*, T1>, param2: TypedColumn<*, T2>): TypedColumn<*, R> =
//        functions.callUDF(udfName, param1, param2).`as`(encoder)
//
//    operator fun invoke(param1: Column, param2: Column): Column = functions.callUDF(udfName, param1, param2)
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    func: UDF2<T1, T2, R>,
//): RegisteredUDF2<T1, T2, R> {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    register(
//        name,
//        udf(func, schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF2(name, encoder())
//}
//
///**
// * A wrapper for a UDF with 3 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF3(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(param1: Column, param2: Column, param3: Column): Column =
//        functions.callUDF(udfName, param1, param2, param3)
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3) -> R,
//): RegisteredUDF3 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    register(
//        name,
//        udf(UDF3(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF3(name)
//}
//
///**
// * A wrapper for a UDF with 4 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF4(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(param1: Column, param2: Column, param3: Column, param4: Column): Column =
//        functions.callUDF(udfName, param1, param2, param3, param4)
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4) -> R,
//): RegisteredUDF4 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    register(
//        name,
//        udf(UDF4(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF4(name)
//}
//
///**
// * A wrapper for a UDF with 5 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF5(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(param1: Column, param2: Column, param3: Column, param4: Column, param5: Column): Column =
//        functions.callUDF(udfName, param1, param2, param3, param4, param5)
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5) -> R,
//): RegisteredUDF5 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    register(
//        name,
//        udf(UDF5(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF5(name)
//}
//
///**
// * A wrapper for a UDF with 6 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF6(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column
//    ): Column = functions.callUDF(udfName, param1, param2, param3, param4, param5, param6)
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6) -> R,
//): RegisteredUDF6 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    register(
//        name,
//        udf(UDF6(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF6(name)
//}
//
///**
// * A wrapper for a UDF with 7 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF7(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column
//    ): Column = functions.callUDF(udfName, param1, param2, param3, param4, param5, param6, param7)
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7) -> R,
//): RegisteredUDF7 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    register(
//        name,
//        udf(UDF7(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF7(name)
//}
//
///**
// * A wrapper for a UDF with 8 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF8(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column
//    ): Column = functions.callUDF(udfName, param1, param2, param3, param4, param5, param6, param7, param8)
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
//): RegisteredUDF8 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    register(
//        name,
//        udf(UDF8(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF8(name)
//}
//
///**
// * A wrapper for a UDF with 9 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF9(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column
//    ): Column = functions.callUDF(udfName, param1, param2, param3, param4, param5, param6, param7, param8, param9)
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
//): RegisteredUDF9 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    register(
//        name,
//        udf(UDF9(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF9(name)
//}
//
///**
// * A wrapper for a UDF with 10 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF10(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column
//    ): Column =
//        functions.callUDF(udfName, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10)
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
//): RegisteredUDF10 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    register(
//        name,
//        udf(UDF10(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF10(name)
//}
//
///**
// * A wrapper for a UDF with 11 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF11(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
//): RegisteredUDF11 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    register(
//        name,
//        udf(UDF11(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF11(name)
//}
//
///**
// * A wrapper for a UDF with 12 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF12(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
//): RegisteredUDF12 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    register(
//        name,
//        udf(UDF12(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF12(name)
//}
//
///**
// * A wrapper for a UDF with 13 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF13(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column,
//        param13: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12,
//        param13
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
//): RegisteredUDF13 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    T13::class.checkForValidType("T13")
//    register(
//        name,
//        udf(UDF13(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF13(name)
//}
//
///**
// * A wrapper for a UDF with 14 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF14(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column,
//        param13: Column,
//        param14: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12,
//        param13,
//        param14
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
//): RegisteredUDF14 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    T13::class.checkForValidType("T13")
//    T14::class.checkForValidType("T14")
//    register(
//        name,
//        udf(UDF14(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF14(name)
//}
//
///**
// * A wrapper for a UDF with 15 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF15(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column,
//        param13: Column,
//        param14: Column,
//        param15: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12,
//        param13,
//        param14,
//        param15
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
//): RegisteredUDF15 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    T13::class.checkForValidType("T13")
//    T14::class.checkForValidType("T14")
//    T15::class.checkForValidType("T15")
//    register(
//        name,
//        udf(UDF15(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF15(name)
//}
//
///**
// * A wrapper for a UDF with 16 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF16(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column,
//        param13: Column,
//        param14: Column,
//        param15: Column,
//        param16: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12,
//        param13,
//        param14,
//        param15,
//        param16
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
//): RegisteredUDF16 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    T13::class.checkForValidType("T13")
//    T14::class.checkForValidType("T14")
//    T15::class.checkForValidType("T15")
//    T16::class.checkForValidType("T16")
//    register(
//        name,
//        udf(UDF16(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF16(name)
//}
//
///**
// * A wrapper for a UDF with 17 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF17(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column,
//        param13: Column,
//        param14: Column,
//        param15: Column,
//        param16: Column,
//        param17: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12,
//        param13,
//        param14,
//        param15,
//        param16,
//        param17
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
//): RegisteredUDF17 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    T13::class.checkForValidType("T13")
//    T14::class.checkForValidType("T14")
//    T15::class.checkForValidType("T15")
//    T16::class.checkForValidType("T16")
//    T17::class.checkForValidType("T17")
//    register(
//        name,
//        udf(UDF17(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF17(name)
//}
//
///**
// * A wrapper for a UDF with 18 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF18(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column,
//        param13: Column,
//        param14: Column,
//        param15: Column,
//        param16: Column,
//        param17: Column,
//        param18: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12,
//        param13,
//        param14,
//        param15,
//        param16,
//        param17,
//        param18
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
//): RegisteredUDF18 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    T13::class.checkForValidType("T13")
//    T14::class.checkForValidType("T14")
//    T15::class.checkForValidType("T15")
//    T16::class.checkForValidType("T16")
//    T17::class.checkForValidType("T17")
//    T18::class.checkForValidType("T18")
//    register(
//        name,
//        udf(UDF18(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF18(name)
//}
//
///**
// * A wrapper for a UDF with 19 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF19(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column,
//        param13: Column,
//        param14: Column,
//        param15: Column,
//        param16: Column,
//        param17: Column,
//        param18: Column,
//        param19: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12,
//        param13,
//        param14,
//        param15,
//        param16,
//        param17,
//        param18,
//        param19
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
//): RegisteredUDF19 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    T13::class.checkForValidType("T13")
//    T14::class.checkForValidType("T14")
//    T15::class.checkForValidType("T15")
//    T16::class.checkForValidType("T16")
//    T17::class.checkForValidType("T17")
//    T18::class.checkForValidType("T18")
//    T19::class.checkForValidType("T19")
//    register(
//        name,
//        udf(UDF19(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF19(name)
//}
//
///**
// * A wrapper for a UDF with 20 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF20(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column,
//        param13: Column,
//        param14: Column,
//        param15: Column,
//        param16: Column,
//        param17: Column,
//        param18: Column,
//        param19: Column,
//        param20: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12,
//        param13,
//        param14,
//        param15,
//        param16,
//        param17,
//        param18,
//        param19,
//        param20
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
//): RegisteredUDF20 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    T13::class.checkForValidType("T13")
//    T14::class.checkForValidType("T14")
//    T15::class.checkForValidType("T15")
//    T16::class.checkForValidType("T16")
//    T17::class.checkForValidType("T17")
//    T18::class.checkForValidType("T18")
//    T19::class.checkForValidType("T19")
//    T20::class.checkForValidType("T20")
//    register(
//        name,
//        udf(UDF20(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF20(name)
//}
//
///**
// * A wrapper for a UDF with 21 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF21(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column,
//        param13: Column,
//        param14: Column,
//        param15: Column,
//        param16: Column,
//        param17: Column,
//        param18: Column,
//        param19: Column,
//        param20: Column,
//        param21: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12,
//        param13,
//        param14,
//        param15,
//        param16,
//        param17,
//        param18,
//        param19,
//        param20,
//        param21
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R,
//): RegisteredUDF21 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    T13::class.checkForValidType("T13")
//    T14::class.checkForValidType("T14")
//    T15::class.checkForValidType("T15")
//    T16::class.checkForValidType("T16")
//    T17::class.checkForValidType("T17")
//    T18::class.checkForValidType("T18")
//    T19::class.checkForValidType("T19")
//    T20::class.checkForValidType("T20")
//    T21::class.checkForValidType("T21")
//    register(
//        name,
//        udf(UDF21(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF21(name)
//}
//
///**
// * A wrapper for a UDF with 22 arguments.
// * @property udfName the name of the UDF
// */
//class RegisteredUDF22(private val udfName: String) {
//    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
//    operator fun invoke(
//        param1: Column,
//        param2: Column,
//        param3: Column,
//        param4: Column,
//        param5: Column,
//        param6: Column,
//        param7: Column,
//        param8: Column,
//        param9: Column,
//        param10: Column,
//        param11: Column,
//        param12: Column,
//        param13: Column,
//        param14: Column,
//        param15: Column,
//        param16: Column,
//        param17: Column,
//        param18: Column,
//        param19: Column,
//        param20: Column,
//        param21: Column,
//        param22: Column
//    ): Column = functions.callUDF(
//        udfName,
//        param1,
//        param2,
//        param3,
//        param4,
//        param5,
//        param6,
//        param7,
//        param8,
//        param9,
//        param10,
//        param11,
//        param12,
//        param13,
//        param14,
//        param15,
//        param16,
//        param17,
//        param18,
//        param19,
//        param20,
//        param21,
//        param22
//    )
//}
//
///** Registers the [func] with its [name] in [this]. */
//inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> UDFRegistration.register(
//    name: String,
//    asNondeterministic: Boolean = false,
//    noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R,
//): RegisteredUDF22 {
//    T1::class.checkForValidType("T1")
//    T2::class.checkForValidType("T2")
//    T3::class.checkForValidType("T3")
//    T4::class.checkForValidType("T4")
//    T5::class.checkForValidType("T5")
//    T6::class.checkForValidType("T6")
//    T7::class.checkForValidType("T7")
//    T8::class.checkForValidType("T8")
//    T9::class.checkForValidType("T9")
//    T10::class.checkForValidType("T10")
//    T11::class.checkForValidType("T11")
//    T12::class.checkForValidType("T12")
//    T13::class.checkForValidType("T13")
//    T14::class.checkForValidType("T14")
//    T15::class.checkForValidType("T15")
//    T16::class.checkForValidType("T16")
//    T17::class.checkForValidType("T17")
//    T18::class.checkForValidType("T18")
//    T19::class.checkForValidType("T19")
//    T20::class.checkForValidType("T20")
//    T21::class.checkForValidType("T21")
//    T22::class.checkForValidType("T22")
//    register(
//        name,
//        udf(UDF22(func), schema(typeOf<R>()).unWrap())
//            .let { if (asNondeterministic) it.asNondeterministic() else it }
//            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() }
//    )
//    return RegisteredUDF22(name)
//}
