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

import org.apache.spark.sql.*
import org.apache.spark.sql.api.java.*
import kotlin.reflect.*
import org.apache.spark.sql.expressions.UserDefinedFunction as SparkUserDefinedFunction

/**
 * Instance of a UDF with 0 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction0
 * @see udf
 */
open class UserDefinedFunction0<R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction0<R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(): TypedColumn<DsType, R> = super.invoke().`as`(encoder) as TypedColumn<DsType, R>



    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(): Column = super.invokeUntyped()

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction0<R> = NamedUserDefinedFunction0(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction0<R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 0 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction0
 * @see udf
 */
class NamedUserDefinedFunction0<R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction0<R>>,
    UserDefinedFunction0<R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction0]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified R> udf(
    func: KProperty0<() -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction0<R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction0]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified R> udf(
    name: String,
    func: KProperty0<() -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction0<R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction0]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified R> UDFRegistration.register(
    func: KProperty0<() -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction0<R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction0]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<() -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction0<R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction0]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified R> udf(
    func: KFunction0<R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction0<R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction0]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified R> udf(
    name: String,
    func: KFunction0<R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction0<R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction0]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified R> UDFRegistration.register(
    func: KFunction0<R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction0<R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction0]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified R> UDFRegistration.register(
    name: String,
    func: KFunction0<R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction0<R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction0]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") {  -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf {  -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF0<R>,
): NamedUserDefinedFunction0<R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction0]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf {  -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF0<R>,
): UserDefinedFunction0<R> {


    return UserDefinedFunction0(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction0]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") {  -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF0<R>,
): NamedUserDefinedFunction0<R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 1 argument.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction1
 * @see udf
 * @see udaf
 */
open class UserDefinedFunction1<T1, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction1<T1, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>): TypedColumn<DsType, R> = super.invoke(param0).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column): Column = super.invokeUntyped(param0)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column): Column = super.invokeUntyped(param0)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction1<T1, R> = NamedUserDefinedFunction1(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction1<T1, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 1 argument with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction1
 * @see udf
 * @see udaf
 */
class NamedUserDefinedFunction1<T1, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction1<T1, R>>,
    UserDefinedFunction1<T1, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction1]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified R> udf(
    func: KProperty0<(T1) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<T1, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction1]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified R> udf(
    name: String,
    func: KProperty0<(T1) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<T1, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction1]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified R> UDFRegistration.register(
    func: KProperty0<(T1) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<T1, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction1]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<T1, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction1]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified R> udf(
    func: KFunction1<T1, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<T1, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction1]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified R> udf(
    name: String,
    func: KFunction1<T1, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<T1, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction1]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified R> UDFRegistration.register(
    func: KFunction1<T1, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<T1, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction1]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified R> UDFRegistration.register(
    name: String,
    func: KFunction1<T1, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction1<T1, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction1]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF1<T1, R>,
): NamedUserDefinedFunction1<T1, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction1]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF1<T1, R>,
): UserDefinedFunction1<T1, R> {
    T1::class.checkForValidType("T1")

    return UserDefinedFunction1(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction1]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF1<T1, R>,
): NamedUserDefinedFunction1<T1, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 2 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction2
 * @see udf
 */
open class UserDefinedFunction2<T1, T2, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction2<T1, T2, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>): TypedColumn<DsType, R> = super.invoke(param0, param1).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column): Column = super.invokeUntyped(param0, param1)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column): Column = super.invokeUntyped(param0, param1)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction2<T1, T2, R> = NamedUserDefinedFunction2(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction2<T1, T2, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 2 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction2
 * @see udf
 */
class NamedUserDefinedFunction2<T1, T2, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction2<T1, T2, R>>,
    UserDefinedFunction2<T1, T2, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction2]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified R> udf(
    func: KProperty0<(T1, T2) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction2<T1, T2, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction2]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction2<T1, T2, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction2]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction2<T1, T2, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction2]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction2<T1, T2, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction2]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified R> udf(
    func: KFunction2<T1, T2, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction2<T1, T2, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction2]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified R> udf(
    name: String,
    func: KFunction2<T1, T2, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction2<T1, T2, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction2]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified R> UDFRegistration.register(
    func: KFunction2<T1, T2, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction2<T1, T2, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction2]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified R> UDFRegistration.register(
    name: String,
    func: KFunction2<T1, T2, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction2<T1, T2, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction2]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF2<T1, T2, R>,
): NamedUserDefinedFunction2<T1, T2, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction2]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF2<T1, T2, R>,
): UserDefinedFunction2<T1, T2, R> {
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")

    return UserDefinedFunction2(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction2]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF2<T1, T2, R>,
): NamedUserDefinedFunction2<T1, T2, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 3 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction3
 * @see udf
 */
open class UserDefinedFunction3<T1, T2, T3, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction3<T1, T2, T3, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column): Column = super.invokeUntyped(param0, param1, param2)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column): Column = super.invokeUntyped(param0, param1, param2)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction3<T1, T2, T3, R> = NamedUserDefinedFunction3(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction3<T1, T2, T3, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 3 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction3
 * @see udf
 */
class NamedUserDefinedFunction3<T1, T2, T3, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction3<T1, T2, T3, R>>,
    UserDefinedFunction3<T1, T2, T3, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction3]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified R> udf(
    func: KProperty0<(T1, T2, T3) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction3<T1, T2, T3, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction3]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction3<T1, T2, T3, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction3]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction3<T1, T2, T3, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction3]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction3<T1, T2, T3, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction3]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified R> udf(
    func: KFunction3<T1, T2, T3, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction3<T1, T2, T3, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction3]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified R> udf(
    name: String,
    func: KFunction3<T1, T2, T3, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction3<T1, T2, T3, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction3]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified R> UDFRegistration.register(
    func: KFunction3<T1, T2, T3, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction3<T1, T2, T3, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction3]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified R> UDFRegistration.register(
    name: String,
    func: KFunction3<T1, T2, T3, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction3<T1, T2, T3, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction3]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF3<T1, T2, T3, R>,
): NamedUserDefinedFunction3<T1, T2, T3, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction3]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF3<T1, T2, T3, R>,
): UserDefinedFunction3<T1, T2, T3, R> {
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")

    return UserDefinedFunction3(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction3]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF3<T1, T2, T3, R>,
): NamedUserDefinedFunction3<T1, T2, T3, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 4 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction4
 * @see udf
 */
open class UserDefinedFunction4<T1, T2, T3, T4, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction4<T1, T2, T3, T4, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column): Column = super.invokeUntyped(param0, param1, param2, param3)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column): Column = super.invokeUntyped(param0, param1, param2, param3)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction4<T1, T2, T3, T4, R> = NamedUserDefinedFunction4(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction4<T1, T2, T3, T4, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 4 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction4
 * @see udf
 */
class NamedUserDefinedFunction4<T1, T2, T3, T4, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction4<T1, T2, T3, T4, R>>,
    UserDefinedFunction4<T1, T2, T3, T4, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction4]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction4<T1, T2, T3, T4, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction4]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction4<T1, T2, T3, T4, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction4]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction4<T1, T2, T3, T4, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction4]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction4<T1, T2, T3, T4, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction4]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> udf(
    func: KFunction4<T1, T2, T3, T4, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction4<T1, T2, T3, T4, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction4]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> udf(
    name: String,
    func: KFunction4<T1, T2, T3, T4, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction4<T1, T2, T3, T4, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction4]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> UDFRegistration.register(
    func: KFunction4<T1, T2, T3, T4, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction4<T1, T2, T3, T4, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction4]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> UDFRegistration.register(
    name: String,
    func: KFunction4<T1, T2, T3, T4, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction4<T1, T2, T3, T4, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction4]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF4<T1, T2, T3, T4, R>,
): NamedUserDefinedFunction4<T1, T2, T3, T4, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction4]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF4<T1, T2, T3, T4, R>,
): UserDefinedFunction4<T1, T2, T3, T4, R> {
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")

    return UserDefinedFunction4(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction4]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF4<T1, T2, T3, T4, R>,
): NamedUserDefinedFunction4<T1, T2, T3, T4, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 5 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction5
 * @see udf
 */
open class UserDefinedFunction5<T1, T2, T3, T4, T5, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> = NamedUserDefinedFunction5(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 5 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction5
 * @see udf
 */
class NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R>>,
    UserDefinedFunction5<T1, T2, T3, T4, T5, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction5]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction5]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction5]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction5]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction5]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> udf(
    func: KFunction5<T1, T2, T3, T4, T5, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction5]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> udf(
    name: String,
    func: KFunction5<T1, T2, T3, T4, T5, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction5]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> UDFRegistration.register(
    func: KFunction5<T1, T2, T3, T4, T5, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction5]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> UDFRegistration.register(
    name: String,
    func: KFunction5<T1, T2, T3, T4, T5, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction5]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF5<T1, T2, T3, T4, T5, R>,
): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction5]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF5<T1, T2, T3, T4, T5, R>,
): UserDefinedFunction5<T1, T2, T3, T4, T5, R> {
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")

    return UserDefinedFunction5(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction5]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF5<T1, T2, T3, T4, T5, R>,
): NamedUserDefinedFunction5<T1, T2, T3, T4, T5, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 6 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction6
 * @see udf
 */
open class UserDefinedFunction6<T1, T2, T3, T4, T5, T6, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> = NamedUserDefinedFunction6(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 6 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction6
 * @see udf
 */
class NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R>>,
    UserDefinedFunction6<T1, T2, T3, T4, T5, T6, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction6]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction6]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction6]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction6]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction6]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> udf(
    func: KFunction6<T1, T2, T3, T4, T5, T6, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction6]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> udf(
    name: String,
    func: KFunction6<T1, T2, T3, T4, T5, T6, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction6]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> UDFRegistration.register(
    func: KFunction6<T1, T2, T3, T4, T5, T6, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction6]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> UDFRegistration.register(
    name: String,
    func: KFunction6<T1, T2, T3, T4, T5, T6, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction6]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF6<T1, T2, T3, T4, T5, T6, R>,
): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction6]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF6<T1, T2, T3, T4, T5, T6, R>,
): UserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> {
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")

    return UserDefinedFunction6(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction6]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF6<T1, T2, T3, T4, T5, T6, R>,
): NamedUserDefinedFunction6<T1, T2, T3, T4, T5, T6, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 7 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction7
 * @see udf
 */
open class UserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> = NamedUserDefinedFunction7(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 7 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction7
 * @see udf
 */
class NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R>>,
    UserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction7]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction7]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction7]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction7]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction7]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> udf(
    func: KFunction7<T1, T2, T3, T4, T5, T6, T7, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction7]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> udf(
    name: String,
    func: KFunction7<T1, T2, T3, T4, T5, T6, T7, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction7]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> UDFRegistration.register(
    func: KFunction7<T1, T2, T3, T4, T5, T6, T7, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction7]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> UDFRegistration.register(
    name: String,
    func: KFunction7<T1, T2, T3, T4, T5, T6, T7, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction7]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF7<T1, T2, T3, T4, T5, T6, T7, R>,
): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction7]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF7<T1, T2, T3, T4, T5, T6, T7, R>,
): UserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> {
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")

    return UserDefinedFunction7(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction7]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF7<T1, T2, T3, T4, T5, T6, T7, R>,
): NamedUserDefinedFunction7<T1, T2, T3, T4, T5, T6, T7, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 8 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction8
 * @see udf
 */
open class UserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> = NamedUserDefinedFunction8(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 8 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction8
 * @see udf
 */
class NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R>>,
    UserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction8]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction8]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction8]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction8]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction8]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> udf(
    func: KFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction8]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> udf(
    name: String,
    func: KFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction8]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> UDFRegistration.register(
    func: KFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction8]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> UDFRegistration.register(
    name: String,
    func: KFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction8]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF8<T1, T2, T3, T4, T5, T6, T7, T8, R>,
): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction8]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF8<T1, T2, T3, T4, T5, T6, T7, T8, R>,
): UserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> {
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")

    return UserDefinedFunction8(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction8]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF8<T1, T2, T3, T4, T5, T6, T7, T8, R>,
): NamedUserDefinedFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 9 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction9
 * @see udf
 */
open class UserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> = NamedUserDefinedFunction9(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 9 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction9
 * @see udf
 */
class NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>>,
    UserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction9]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction9]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction9]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction9]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction9]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> udf(
    func: KFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction9]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> udf(
    name: String,
    func: KFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction9]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> UDFRegistration.register(
    func: KFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction9]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> UDFRegistration.register(
    name: String,
    func: KFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction9]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>,
): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction9]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>,
): UserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> {
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")
    T3::class.checkForValidType("T3")
    T4::class.checkForValidType("T4")
    T5::class.checkForValidType("T5")
    T6::class.checkForValidType("T6")
    T7::class.checkForValidType("T7")
    T8::class.checkForValidType("T8")
    T9::class.checkForValidType("T9")

    return UserDefinedFunction9(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction9]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>,
): NamedUserDefinedFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 10 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction10
 * @see udf
 */
open class UserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> = NamedUserDefinedFunction10(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 10 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction10
 * @see udf
 */
class NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>>,
    UserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction10]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction10]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction10]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction10]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction10]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> udf(
    func: KFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction10]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> udf(
    name: String,
    func: KFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction10]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> UDFRegistration.register(
    func: KFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction10]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> UDFRegistration.register(
    name: String,
    func: KFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction10]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>,
): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction10]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>,
): UserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> {
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

    return UserDefinedFunction10(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction10]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>,
): NamedUserDefinedFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 11 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction11
 * @see udf
 */
open class UserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> = NamedUserDefinedFunction11(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 11 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction11
 * @see udf
 */
class NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>>,
    UserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction11]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction11]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction11]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction11]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction11]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> udf(
    func: KFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction11]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> udf(
    name: String,
    func: KFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction11]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> UDFRegistration.register(
    func: KFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction11]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> UDFRegistration.register(
    name: String,
    func: KFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction11]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>,
): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction11]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>,
): UserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> {
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

    return UserDefinedFunction11(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction11]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>,
): NamedUserDefinedFunction11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 12 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction12
 * @see udf
 */
open class UserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> = NamedUserDefinedFunction12(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 12 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction12
 * @see udf
 */
class NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>>,
    UserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction12]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction12]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction12]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction12]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction12]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> udf(
    func: KFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction12]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> udf(
    name: String,
    func: KFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction12]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> UDFRegistration.register(
    func: KFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction12]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> UDFRegistration.register(
    name: String,
    func: KFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction12]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>,
): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction12]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>,
): UserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> {
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

    return UserDefinedFunction12(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction12]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>,
): NamedUserDefinedFunction12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 13 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction13
 * @see udf
 */
open class UserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>, param12: TypedColumn<DsType, T13>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> = NamedUserDefinedFunction13(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 13 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction13
 * @see udf
 */
class NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>>,
    UserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction13]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction13]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction13]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction13]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction13]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> udf(
    func: KFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction13]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> udf(
    name: String,
    func: KFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction13]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> UDFRegistration.register(
    func: KFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction13]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> UDFRegistration.register(
    name: String,
    func: KFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction13]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>,
): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction13]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>,
): UserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> {
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

    return UserDefinedFunction13(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction13]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>,
): NamedUserDefinedFunction13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 14 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction14
 * @see udf
 */
open class UserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>, param12: TypedColumn<DsType, T13>, param13: TypedColumn<DsType, T14>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> = NamedUserDefinedFunction14(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 14 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction14
 * @see udf
 */
class NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>>,
    UserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction14]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction14]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction14]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction14]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction14]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> udf(
    func: KFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction14]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> udf(
    name: String,
    func: KFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction14]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> UDFRegistration.register(
    func: KFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction14]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> UDFRegistration.register(
    name: String,
    func: KFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction14]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>,
): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction14]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>,
): UserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> {
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

    return UserDefinedFunction14(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction14]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>,
): NamedUserDefinedFunction14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 15 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction15
 * @see udf
 */
open class UserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>, param12: TypedColumn<DsType, T13>, param13: TypedColumn<DsType, T14>, param14: TypedColumn<DsType, T15>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> = NamedUserDefinedFunction15(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 15 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction15
 * @see udf
 */
class NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>>,
    UserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction15]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction15]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction15]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction15]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction15]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> udf(
    func: KFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction15]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> udf(
    name: String,
    func: KFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction15]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> UDFRegistration.register(
    func: KFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction15]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> UDFRegistration.register(
    name: String,
    func: KFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction15]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>,
): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction15]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>,
): UserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> {
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

    return UserDefinedFunction15(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction15]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>,
): NamedUserDefinedFunction15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 16 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction16
 * @see udf
 */
open class UserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>, param12: TypedColumn<DsType, T13>, param13: TypedColumn<DsType, T14>, param14: TypedColumn<DsType, T15>, param15: TypedColumn<DsType, T16>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> = NamedUserDefinedFunction16(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 16 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction16
 * @see udf
 */
class NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>>,
    UserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction16]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction16]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction16]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction16]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction16]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> udf(
    func: KFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction16]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> udf(
    name: String,
    func: KFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction16]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> UDFRegistration.register(
    func: KFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction16]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> UDFRegistration.register(
    name: String,
    func: KFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction16]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>,
): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction16]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>,
): UserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> {
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

    return UserDefinedFunction16(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction16]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>,
): NamedUserDefinedFunction16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 17 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction17
 * @see udf
 */
open class UserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>, param12: TypedColumn<DsType, T13>, param13: TypedColumn<DsType, T14>, param14: TypedColumn<DsType, T15>, param15: TypedColumn<DsType, T16>, param16: TypedColumn<DsType, T17>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> = NamedUserDefinedFunction17(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 17 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction17
 * @see udf
 */
class NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>>,
    UserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction17]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction17]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction17]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction17]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction17]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> udf(
    func: KFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction17]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> udf(
    name: String,
    func: KFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction17]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> UDFRegistration.register(
    func: KFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction17]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> UDFRegistration.register(
    name: String,
    func: KFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction17]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>,
): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction17]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>,
): UserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> {
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

    return UserDefinedFunction17(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction17]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>,
): NamedUserDefinedFunction17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 18 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction18
 * @see udf
 */
open class UserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>, param12: TypedColumn<DsType, T13>, param13: TypedColumn<DsType, T14>, param14: TypedColumn<DsType, T15>, param15: TypedColumn<DsType, T16>, param16: TypedColumn<DsType, T17>, param17: TypedColumn<DsType, T18>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column, param17: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column, param17: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> = NamedUserDefinedFunction18(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 18 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction18
 * @see udf
 */
class NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>>,
    UserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction18]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction18]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction18]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction18]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction18]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> udf(
    func: KFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction18]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> udf(
    name: String,
    func: KFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction18]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> UDFRegistration.register(
    func: KFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction18]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> UDFRegistration.register(
    name: String,
    func: KFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction18]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>,
): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction18]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>,
): UserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> {
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

    return UserDefinedFunction18(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction18]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>,
): NamedUserDefinedFunction18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 19 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction19
 * @see udf
 */
open class UserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>, param12: TypedColumn<DsType, T13>, param13: TypedColumn<DsType, T14>, param14: TypedColumn<DsType, T15>, param15: TypedColumn<DsType, T16>, param16: TypedColumn<DsType, T17>, param17: TypedColumn<DsType, T18>, param18: TypedColumn<DsType, T19>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column, param17: Column, param18: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column, param17: Column, param18: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> = NamedUserDefinedFunction19(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 19 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction19
 * @see udf
 */
class NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>>,
    UserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction19]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction19]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction19]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction19]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction19]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> udf(
    func: KFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction19]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> udf(
    name: String,
    func: KFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction19]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> UDFRegistration.register(
    func: KFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction19]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> UDFRegistration.register(
    name: String,
    func: KFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction19]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>,
): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction19]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>,
): UserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> {
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

    return UserDefinedFunction19(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction19]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>,
): NamedUserDefinedFunction19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 20 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction20
 * @see udf
 */
open class UserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>, param12: TypedColumn<DsType, T13>, param13: TypedColumn<DsType, T14>, param14: TypedColumn<DsType, T15>, param15: TypedColumn<DsType, T16>, param16: TypedColumn<DsType, T17>, param17: TypedColumn<DsType, T18>, param18: TypedColumn<DsType, T19>, param19: TypedColumn<DsType, T20>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column, param17: Column, param18: Column, param19: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column, param17: Column, param18: Column, param19: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> = NamedUserDefinedFunction20(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 20 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction20
 * @see udf
 */
class NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>>,
    UserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction20]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction20]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction20]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction20]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction20]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> udf(
    func: KFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction20]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> udf(
    name: String,
    func: KFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction20]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> UDFRegistration.register(
    func: KFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction20]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> UDFRegistration.register(
    name: String,
    func: KFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction20]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>,
): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction20]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>,
): UserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> {
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

    return UserDefinedFunction20(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction20]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>,
): NamedUserDefinedFunction20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 21 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction21
 * @see udf
 */
open class UserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>, param12: TypedColumn<DsType, T13>, param13: TypedColumn<DsType, T14>, param14: TypedColumn<DsType, T15>, param15: TypedColumn<DsType, T16>, param16: TypedColumn<DsType, T17>, param17: TypedColumn<DsType, T18>, param18: TypedColumn<DsType, T19>, param19: TypedColumn<DsType, T20>, param20: TypedColumn<DsType, T21>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19, param20).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column, param17: Column, param18: Column, param19: Column, param20: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19, param20)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column, param17: Column, param18: Column, param19: Column, param20: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19, param20)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> = NamedUserDefinedFunction21(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 21 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction21
 * @see udf
 */
class NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>>,
    UserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction21]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction21]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction21]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction21]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction21]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> udf(
    func: KFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction21]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> udf(
    name: String,
    func: KFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction21]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> UDFRegistration.register(
    func: KFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction21]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> UDFRegistration.register(
    name: String,
    func: KFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction21]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20, t21: T21 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20, t21: T21 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>,
): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction21]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20, t21: T21 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>,
): UserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> {
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

    return UserDefinedFunction21(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction21]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20, t21: T21 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>,
): NamedUserDefinedFunction21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> =
    register(udf(name, nondeterministic, func))

/**
 * Instance of a UDF with 22 arguments.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunction22
 * @see udf
 */
open class UserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun <DsType> invoke(param0: TypedColumn<DsType, T1>, param1: TypedColumn<DsType, T2>, param2: TypedColumn<DsType, T3>, param3: TypedColumn<DsType, T4>, param4: TypedColumn<DsType, T5>, param5: TypedColumn<DsType, T6>, param6: TypedColumn<DsType, T7>, param7: TypedColumn<DsType, T8>, param8: TypedColumn<DsType, T9>, param9: TypedColumn<DsType, T10>, param10: TypedColumn<DsType, T11>, param11: TypedColumn<DsType, T12>, param12: TypedColumn<DsType, T13>, param13: TypedColumn<DsType, T14>, param14: TypedColumn<DsType, T15>, param15: TypedColumn<DsType, T16>, param16: TypedColumn<DsType, T17>, param17: TypedColumn<DsType, T18>, param18: TypedColumn<DsType, T19>, param19: TypedColumn<DsType, T20>, param20: TypedColumn<DsType, T21>, param21: TypedColumn<DsType, T22>): TypedColumn<DsType, R> = super.invoke(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19, param20, param21).`as`(encoder) as TypedColumn<DsType, R>

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column, param17: Column, param18: Column, param19: Column, param20: Column, param21: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19, param20, param21)

    /**
     * Returns an expression that invokes the UDF in untyped manner, using the given arguments.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    fun invokeUntyped(param0: Column, param1: Column, param2: Column, param3: Column, param4: Column, param5: Column, param6: Column, param7: Column, param8: Column, param9: Column, param10: Column, param11: Column, param12: Column, param13: Column, param14: Column, param15: Column, param16: Column, param17: Column, param18: Column, param19: Column, param20: Column, param21: Column): Column = super.invokeUntyped(param0, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19, param20, param21)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> = NamedUserDefinedFunction22(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with 22 arguments with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunction22
 * @see udf
 */
class NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>>,
    UserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>(udf = udf.withName(name), encoder = encoder)

/**
 * Creates a UDF ([NamedUserDefinedFunction22]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> udf(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction22]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> udf(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> = udf(name, nondeterministic, func.get())

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction22]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> UDFRegistration.register(
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction22]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> UDFRegistration.register(
    name: String,
    func: KProperty0<(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> = udf(name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction22]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> udf(
    func: KFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> = udf(func.name, func, nondeterministic)

/**
 * Creates a UDF ([NamedUserDefinedFunction22]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> udf(
    name: String,
    func: KFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> = udf(name, nondeterministic, func)

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction22]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> UDFRegistration.register(
    func: KFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> = register(udf(func, nondeterministic))

/**
 * Creates and registers a UDF ([NamedUserDefinedFunction22]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 * @param name Optional. Name for the UDF.
 * @param func function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> UDFRegistration.register(
    name: String,
    func: KFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> = udf(name, func, nondeterministic)


/**
 * Defines a named UDF ([NamedUserDefinedFunction22]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20, t21: T21, t22: T22 -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20, t21: T21, t22: T22 -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>,
): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> =
    udf(nondeterministic, func).withName(name)

/**
 * Defines a UDF ([UserDefinedFunction22]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20, t21: T21, t22: T22 -> ... }`
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>,
): UserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> {
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
    T22::class.checkForValidType("T22")

    return UserDefinedFunction22(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<R>(),
    )
}

/**
 * Defines and registers a named UDF ([NamedUserDefinedFunction22]) instance based on the (lambda) function [func].
 * For example: `val myUdf = udf.register("myUdf") { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18, t19: T19, t20: T20, t21: T21, t22: T22 -> ... }`
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param func The function to convert to a UDF. Can be a lambda.
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R>,
): NamedUserDefinedFunction22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> =
    register(udf(name, nondeterministic, func))


