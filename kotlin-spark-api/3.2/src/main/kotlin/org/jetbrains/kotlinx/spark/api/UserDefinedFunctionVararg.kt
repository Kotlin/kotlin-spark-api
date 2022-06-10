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
import org.jetbrains.kotlinx.spark.extensions.VarargUnwrapper
import org.apache.spark.sql.api.java.*
import org.apache.spark.sql.internal.SQLConf
import kotlin.reflect.*
import org.apache.spark.sql.expressions.UserDefinedFunction as SparkUserDefinedFunction

/**
 * Instance of a UDF with vararg arguments of the same type.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see NamedUserDefinedFunctionVararg
 * @see udf
 */
open class UserDefinedFunctionVararg<T, R>(
    override val udf: SparkUserDefinedFunction,
    override val encoder: Encoder<R>,
): UserDefinedFunction<R, NamedUserDefinedFunctionVararg<T, R>> {

    /**
     * Allows this UDF to be called in typed manner using columns in a [Dataset.selectTyped] call.
     * @see typedCol to create typed columns.
     * @see org.apache.spark.sql.expressions.UserDefinedFunction.apply
     */
    operator fun invoke(vararg params: TypedColumn<*, T>): TypedColumn<*, R> = super.invoke(*params).`as`(encoder)

    /** Returns named variant of this UDF. */
    override fun withName(name: String): NamedUserDefinedFunctionVararg<T, R> = NamedUserDefinedFunctionVararg(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    /**
     * Returns named variant of this UDF.
     * @see withName
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): NamedUserDefinedFunctionVararg<T, R> =
        withName(property.name)
}

/**
 * Instance of a UDF with vararg arguments of the same type with name.
 * This UDF can be invoked with (typed) columns in a [Dataset.select] or [selectTyped] call.
 * Alternatively it can be registered for SQL calls using [register].
 *
 * @see org.apache.spark.sql.expressions.UserDefinedFunction
 * @see UserDefinedFunctionVararg
 * @see udf
 */
class NamedUserDefinedFunctionVararg<T, R>(
    override val name: String,
    udf: SparkUserDefinedFunction,
    encoder: Encoder<R>,
): NamedUserDefinedFunction<R, NamedUserDefinedFunctionVararg<T, R>>,
    UserDefinedFunctionVararg<T, R>(udf = udf.withName(name), encoder = encoder)

@PublishedApi
internal inline fun <R> withAllowUntypedScalaUDF(block: () -> R): R {
    val sqlConf = SQLConf.get()
    val confString = "spark.sql.legacy.allowUntypedScalaUDF"
    val prev = sqlConf.getConfString(confString, "false")
    sqlConf.setConfString(confString, "true")
    return try {
        block()
    } finally {
        sqlConf.setConfString(confString, prev)
    }
}




/**
 * Defines a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf("myUdf") { t1: ByteArray -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: ByteArray -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargByte")
inline fun <reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<ByteArray, R>,
): NamedUserDefinedFunctionVararg<Byte, R> =
    udf(nondeterministic, varargFunc).withName(name)

/**
 * Defines a vararg UDF ([UserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf { t1: ByteArray -> ... }`
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargByte")
inline fun <reified R> udf(
    nondeterministic: Boolean = false,
    varargFunc: UDF1<ByteArray, R>,
): UserDefinedFunctionVararg<Byte, R> {


    return withAllowUntypedScalaUDF {
        UserDefinedFunctionVararg(
            udf = functions.udf(VarargUnwrapper(varargFunc) { i, init -> ByteArray(i, init::call) }, schema(typeOf<R>()).unWrap())
                .let { if (nondeterministic) it.asNondeterministic() else it }
                .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
            encoder = encoder<R>(),
        )
    }
}
/**
 * Defines and registers a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf.register("myUdf") { t1: ByteArray -> ... }`
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("registerVarargByte")
inline fun <reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<ByteArray, R>,
): NamedUserDefinedFunctionVararg<Byte, R> =
    register(udf(name, nondeterministic, varargFunc))
/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargByte")
inline fun <reified R> udf(
    varargFunc: KProperty0<(ByteArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Byte, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargByte")
inline fun <reified R> udf(
    name: String,
    varargFunc: KProperty0<(ByteArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Byte, R> = udf(name, nondeterministic, varargFunc.get())

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargByte")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KProperty0<(ByteArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Byte, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargByte")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KProperty0<(ByteArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Byte, R> = register(udf(name, varargFunc, nondeterministic))

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargByte")
inline fun <reified R> udf(
    varargFunc: KFunction1<ByteArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Byte, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargByte")
inline fun <reified R> udf(
    name: String,
    varargFunc: KFunction1<ByteArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Byte, R> = udf(name, nondeterministic, varargFunc)

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargByte")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KFunction1<ByteArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Byte, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an ByteArray instead, use WrappedArray<Byte>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargByte")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KFunction1<ByteArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Byte, R> = register(udf(name, varargFunc, nondeterministic))


/**
 * Defines a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf("myUdf") { t1: CharArray -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: CharArray -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargChar")
inline fun <reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<CharArray, R>,
): NamedUserDefinedFunctionVararg<Char, R> =
    udf(nondeterministic, varargFunc).withName(name)

/**
 * Defines a vararg UDF ([UserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf { t1: CharArray -> ... }`
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargChar")
inline fun <reified R> udf(
    nondeterministic: Boolean = false,
    varargFunc: UDF1<CharArray, R>,
): UserDefinedFunctionVararg<Char, R> {


    return withAllowUntypedScalaUDF {
        UserDefinedFunctionVararg(
            udf = functions.udf(VarargUnwrapper(varargFunc) { i, init -> CharArray(i, init::call) }, schema(typeOf<R>()).unWrap())
                .let { if (nondeterministic) it.asNondeterministic() else it }
                .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
            encoder = encoder<R>(),
        )
    }
}
/**
 * Defines and registers a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf.register("myUdf") { t1: CharArray -> ... }`
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("registerVarargChar")
inline fun <reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<CharArray, R>,
): NamedUserDefinedFunctionVararg<Char, R> =
    register(udf(name, nondeterministic, varargFunc))
/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargChar")
inline fun <reified R> udf(
    varargFunc: KProperty0<(CharArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Char, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargChar")
inline fun <reified R> udf(
    name: String,
    varargFunc: KProperty0<(CharArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Char, R> = udf(name, nondeterministic, varargFunc.get())

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargChar")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KProperty0<(CharArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Char, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargChar")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KProperty0<(CharArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Char, R> = register(udf(name, varargFunc, nondeterministic))

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargChar")
inline fun <reified R> udf(
    varargFunc: KFunction1<CharArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Char, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargChar")
inline fun <reified R> udf(
    name: String,
    varargFunc: KFunction1<CharArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Char, R> = udf(name, nondeterministic, varargFunc)

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargChar")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KFunction1<CharArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Char, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an CharArray instead, use WrappedArray<Char>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargChar")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KFunction1<CharArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Char, R> = register(udf(name, varargFunc, nondeterministic))


/**
 * Defines a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf("myUdf") { t1: ShortArray -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: ShortArray -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargShort")
inline fun <reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<ShortArray, R>,
): NamedUserDefinedFunctionVararg<Short, R> =
    udf(nondeterministic, varargFunc).withName(name)

/**
 * Defines a vararg UDF ([UserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf { t1: ShortArray -> ... }`
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargShort")
inline fun <reified R> udf(
    nondeterministic: Boolean = false,
    varargFunc: UDF1<ShortArray, R>,
): UserDefinedFunctionVararg<Short, R> {


    return withAllowUntypedScalaUDF {
        UserDefinedFunctionVararg(
            udf = functions.udf(VarargUnwrapper(varargFunc) { i, init -> ShortArray(i, init::call) }, schema(typeOf<R>()).unWrap())
                .let { if (nondeterministic) it.asNondeterministic() else it }
                .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
            encoder = encoder<R>(),
        )
    }
}
/**
 * Defines and registers a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf.register("myUdf") { t1: ShortArray -> ... }`
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("registerVarargShort")
inline fun <reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<ShortArray, R>,
): NamedUserDefinedFunctionVararg<Short, R> =
    register(udf(name, nondeterministic, varargFunc))
/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargShort")
inline fun <reified R> udf(
    varargFunc: KProperty0<(ShortArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Short, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargShort")
inline fun <reified R> udf(
    name: String,
    varargFunc: KProperty0<(ShortArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Short, R> = udf(name, nondeterministic, varargFunc.get())

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargShort")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KProperty0<(ShortArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Short, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargShort")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KProperty0<(ShortArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Short, R> = register(udf(name, varargFunc, nondeterministic))

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargShort")
inline fun <reified R> udf(
    varargFunc: KFunction1<ShortArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Short, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargShort")
inline fun <reified R> udf(
    name: String,
    varargFunc: KFunction1<ShortArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Short, R> = udf(name, nondeterministic, varargFunc)

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargShort")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KFunction1<ShortArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Short, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an ShortArray instead, use WrappedArray<Short>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargShort")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KFunction1<ShortArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Short, R> = register(udf(name, varargFunc, nondeterministic))


/**
 * Defines a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf("myUdf") { t1: IntArray -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: IntArray -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargInt")
inline fun <reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<IntArray, R>,
): NamedUserDefinedFunctionVararg<Int, R> =
    udf(nondeterministic, varargFunc).withName(name)

/**
 * Defines a vararg UDF ([UserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf { t1: IntArray -> ... }`
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargInt")
inline fun <reified R> udf(
    nondeterministic: Boolean = false,
    varargFunc: UDF1<IntArray, R>,
): UserDefinedFunctionVararg<Int, R> {


    return withAllowUntypedScalaUDF {
        UserDefinedFunctionVararg(
            udf = functions.udf(VarargUnwrapper(varargFunc) { i, init -> IntArray(i, init::call) }, schema(typeOf<R>()).unWrap())
                .let { if (nondeterministic) it.asNondeterministic() else it }
                .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
            encoder = encoder<R>(),
        )
    }
}
/**
 * Defines and registers a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf.register("myUdf") { t1: IntArray -> ... }`
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("registerVarargInt")
inline fun <reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<IntArray, R>,
): NamedUserDefinedFunctionVararg<Int, R> =
    register(udf(name, nondeterministic, varargFunc))
/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargInt")
inline fun <reified R> udf(
    varargFunc: KProperty0<(IntArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Int, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargInt")
inline fun <reified R> udf(
    name: String,
    varargFunc: KProperty0<(IntArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Int, R> = udf(name, nondeterministic, varargFunc.get())

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargInt")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KProperty0<(IntArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Int, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargInt")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KProperty0<(IntArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Int, R> = register(udf(name, varargFunc, nondeterministic))

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargInt")
inline fun <reified R> udf(
    varargFunc: KFunction1<IntArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Int, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargInt")
inline fun <reified R> udf(
    name: String,
    varargFunc: KFunction1<IntArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Int, R> = udf(name, nondeterministic, varargFunc)

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargInt")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KFunction1<IntArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Int, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an IntArray instead, use WrappedArray<Int>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargInt")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KFunction1<IntArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Int, R> = register(udf(name, varargFunc, nondeterministic))


/**
 * Defines a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf("myUdf") { t1: LongArray -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: LongArray -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargLong")
inline fun <reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<LongArray, R>,
): NamedUserDefinedFunctionVararg<Long, R> =
    udf(nondeterministic, varargFunc).withName(name)

/**
 * Defines a vararg UDF ([UserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf { t1: LongArray -> ... }`
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargLong")
inline fun <reified R> udf(
    nondeterministic: Boolean = false,
    varargFunc: UDF1<LongArray, R>,
): UserDefinedFunctionVararg<Long, R> {


    return withAllowUntypedScalaUDF {
        UserDefinedFunctionVararg(
            udf = functions.udf(VarargUnwrapper(varargFunc) { i, init -> LongArray(i, init::call) }, schema(typeOf<R>()).unWrap())
                .let { if (nondeterministic) it.asNondeterministic() else it }
                .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
            encoder = encoder<R>(),
        )
    }
}
/**
 * Defines and registers a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf.register("myUdf") { t1: LongArray -> ... }`
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("registerVarargLong")
inline fun <reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<LongArray, R>,
): NamedUserDefinedFunctionVararg<Long, R> =
    register(udf(name, nondeterministic, varargFunc))
/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargLong")
inline fun <reified R> udf(
    varargFunc: KProperty0<(LongArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Long, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargLong")
inline fun <reified R> udf(
    name: String,
    varargFunc: KProperty0<(LongArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Long, R> = udf(name, nondeterministic, varargFunc.get())

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargLong")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KProperty0<(LongArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Long, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargLong")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KProperty0<(LongArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Long, R> = register(udf(name, varargFunc, nondeterministic))

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargLong")
inline fun <reified R> udf(
    varargFunc: KFunction1<LongArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Long, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargLong")
inline fun <reified R> udf(
    name: String,
    varargFunc: KFunction1<LongArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Long, R> = udf(name, nondeterministic, varargFunc)

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargLong")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KFunction1<LongArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Long, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an LongArray instead, use WrappedArray<Long>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargLong")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KFunction1<LongArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Long, R> = register(udf(name, varargFunc, nondeterministic))


/**
 * Defines a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf("myUdf") { t1: FloatArray -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: FloatArray -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargFloat")
inline fun <reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<FloatArray, R>,
): NamedUserDefinedFunctionVararg<Float, R> =
    udf(nondeterministic, varargFunc).withName(name)

/**
 * Defines a vararg UDF ([UserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf { t1: FloatArray -> ... }`
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargFloat")
inline fun <reified R> udf(
    nondeterministic: Boolean = false,
    varargFunc: UDF1<FloatArray, R>,
): UserDefinedFunctionVararg<Float, R> {


    return withAllowUntypedScalaUDF {
        UserDefinedFunctionVararg(
            udf = functions.udf(VarargUnwrapper(varargFunc) { i, init -> FloatArray(i, init::call) }, schema(typeOf<R>()).unWrap())
                .let { if (nondeterministic) it.asNondeterministic() else it }
                .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
            encoder = encoder<R>(),
        )
    }
}
/**
 * Defines and registers a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf.register("myUdf") { t1: FloatArray -> ... }`
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("registerVarargFloat")
inline fun <reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<FloatArray, R>,
): NamedUserDefinedFunctionVararg<Float, R> =
    register(udf(name, nondeterministic, varargFunc))
/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargFloat")
inline fun <reified R> udf(
    varargFunc: KProperty0<(FloatArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Float, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargFloat")
inline fun <reified R> udf(
    name: String,
    varargFunc: KProperty0<(FloatArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Float, R> = udf(name, nondeterministic, varargFunc.get())

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargFloat")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KProperty0<(FloatArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Float, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargFloat")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KProperty0<(FloatArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Float, R> = register(udf(name, varargFunc, nondeterministic))

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargFloat")
inline fun <reified R> udf(
    varargFunc: KFunction1<FloatArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Float, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargFloat")
inline fun <reified R> udf(
    name: String,
    varargFunc: KFunction1<FloatArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Float, R> = udf(name, nondeterministic, varargFunc)

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargFloat")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KFunction1<FloatArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Float, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an FloatArray instead, use WrappedArray<Float>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargFloat")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KFunction1<FloatArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Float, R> = register(udf(name, varargFunc, nondeterministic))


/**
 * Defines a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf("myUdf") { t1: DoubleArray -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: DoubleArray -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargDouble")
inline fun <reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<DoubleArray, R>,
): NamedUserDefinedFunctionVararg<Double, R> =
    udf(nondeterministic, varargFunc).withName(name)

/**
 * Defines a vararg UDF ([UserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf { t1: DoubleArray -> ... }`
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargDouble")
inline fun <reified R> udf(
    nondeterministic: Boolean = false,
    varargFunc: UDF1<DoubleArray, R>,
): UserDefinedFunctionVararg<Double, R> {


    return withAllowUntypedScalaUDF {
        UserDefinedFunctionVararg(
            udf = functions.udf(VarargUnwrapper(varargFunc) { i, init -> DoubleArray(i, init::call) }, schema(typeOf<R>()).unWrap())
                .let { if (nondeterministic) it.asNondeterministic() else it }
                .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
            encoder = encoder<R>(),
        )
    }
}
/**
 * Defines and registers a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf.register("myUdf") { t1: DoubleArray -> ... }`
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("registerVarargDouble")
inline fun <reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<DoubleArray, R>,
): NamedUserDefinedFunctionVararg<Double, R> =
    register(udf(name, nondeterministic, varargFunc))
/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargDouble")
inline fun <reified R> udf(
    varargFunc: KProperty0<(DoubleArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Double, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargDouble")
inline fun <reified R> udf(
    name: String,
    varargFunc: KProperty0<(DoubleArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Double, R> = udf(name, nondeterministic, varargFunc.get())

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargDouble")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KProperty0<(DoubleArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Double, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargDouble")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KProperty0<(DoubleArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Double, R> = register(udf(name, varargFunc, nondeterministic))

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargDouble")
inline fun <reified R> udf(
    varargFunc: KFunction1<DoubleArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Double, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargDouble")
inline fun <reified R> udf(
    name: String,
    varargFunc: KFunction1<DoubleArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Double, R> = udf(name, nondeterministic, varargFunc)

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargDouble")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KFunction1<DoubleArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Double, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an DoubleArray instead, use WrappedArray<Double>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargDouble")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KFunction1<DoubleArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Double, R> = register(udf(name, varargFunc, nondeterministic))


/**
 * Defines a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf("myUdf") { t1: BooleanArray -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: BooleanArray -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargBoolean")
inline fun <reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<BooleanArray, R>,
): NamedUserDefinedFunctionVararg<Boolean, R> =
    udf(nondeterministic, varargFunc).withName(name)

/**
 * Defines a vararg UDF ([UserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf { t1: BooleanArray -> ... }`
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargBoolean")
inline fun <reified R> udf(
    nondeterministic: Boolean = false,
    varargFunc: UDF1<BooleanArray, R>,
): UserDefinedFunctionVararg<Boolean, R> {


    return withAllowUntypedScalaUDF {
        UserDefinedFunctionVararg(
            udf = functions.udf(VarargUnwrapper(varargFunc) { i, init -> BooleanArray(i, init::call) }, schema(typeOf<R>()).unWrap())
                .let { if (nondeterministic) it.asNondeterministic() else it }
                .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
            encoder = encoder<R>(),
        )
    }
}
/**
 * Defines and registers a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf.register("myUdf") { t1: BooleanArray -> ... }`
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("registerVarargBoolean")
inline fun <reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<BooleanArray, R>,
): NamedUserDefinedFunctionVararg<Boolean, R> =
    register(udf(name, nondeterministic, varargFunc))
/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargBoolean")
inline fun <reified R> udf(
    varargFunc: KProperty0<(BooleanArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Boolean, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargBoolean")
inline fun <reified R> udf(
    name: String,
    varargFunc: KProperty0<(BooleanArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Boolean, R> = udf(name, nondeterministic, varargFunc.get())

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargBoolean")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KProperty0<(BooleanArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Boolean, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargBoolean")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KProperty0<(BooleanArray) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Boolean, R> = register(udf(name, varargFunc, nondeterministic))

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargBoolean")
inline fun <reified R> udf(
    varargFunc: KFunction1<BooleanArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Boolean, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargBoolean")
inline fun <reified R> udf(
    name: String,
    varargFunc: KFunction1<BooleanArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Boolean, R> = udf(name, nondeterministic, varargFunc)

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargBoolean")
inline fun <reified R> UDFRegistration.register(
    varargFunc: KFunction1<BooleanArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Boolean, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an BooleanArray instead, use WrappedArray<Boolean>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargBoolean")
inline fun <reified R> UDFRegistration.register(
    name: String,
    varargFunc: KFunction1<BooleanArray, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<Boolean, R> = register(udf(name, varargFunc, nondeterministic))


/**
 * Defines a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf("myUdf") { t1: Array<T> -> ... }`
 * Name can also be supplied using delegate: `val myUdf by udf { t1: Array<T> -> ... }`
 * @see UserDefinedFunction.getValue
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargT")
inline fun <reified T, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<Array<T>, R>,
): NamedUserDefinedFunctionVararg<T, R> =
    udf(nondeterministic, varargFunc).withName(name)

/**
 * Defines a vararg UDF ([UserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf { t1: Array<T> -> ... }`
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("udfVarargT")
inline fun <reified T, reified R> udf(
    nondeterministic: Boolean = false,
    varargFunc: UDF1<Array<T>, R>,
): UserDefinedFunctionVararg<T, R> {
    T::class.checkForValidType("T")

    return withAllowUntypedScalaUDF {
        UserDefinedFunctionVararg(
            udf = functions.udf(VarargUnwrapper(varargFunc) { i, init -> Array<T>(i, init::call) }, schema(typeOf<R>()).unWrap())
                .let { if (nondeterministic) it.asNondeterministic() else it }
                .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
            encoder = encoder<R>(),
        )
    }
}
/**
 * Defines and registers a named vararg UDF ([NamedUserDefinedFunctionVararg]) instance based on the (lambda) function [varargFunc].
 * For example: `val myUdf = udf.register("myUdf") { t1: Array<T> -> ... }`
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param name The name for this UDF.
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @param varargFunc The function to convert to a UDF. Can be a lambda.
 */
@JvmName("registerVarargT")
inline fun <reified T, reified R> UDFRegistration.register(
    name: String,
    nondeterministic: Boolean = false,
    varargFunc: UDF1<Array<T>, R>,
): NamedUserDefinedFunctionVararg<T, R> =
    register(udf(name, nondeterministic, varargFunc))
/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargT")
inline fun <reified T, reified R> udf(
    varargFunc: KProperty0<(Array<T>) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<T, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargT")
inline fun <reified T, reified R> udf(
    name: String,
    varargFunc: KProperty0<(Array<T>) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<T, R> = udf(name, nondeterministic, varargFunc.get())

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargT")
inline fun <reified T, reified R> UDFRegistration.register(
    varargFunc: KProperty0<(Array<T>) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<T, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargT")
inline fun <reified T, reified R> UDFRegistration.register(
    name: String,
    varargFunc: KProperty0<(Array<T>) -> R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<T, R> = register(udf(name, varargFunc, nondeterministic))

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf(::myFunction)`
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargT")
inline fun <reified T, reified R> udf(
    varargFunc: KFunction1<Array<T>, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<T, R> = udf(varargFunc.name, varargFunc, nondeterministic)

/**
 * Creates a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("udfVarargT")
inline fun <reified T, reified R> udf(
    name: String,
    varargFunc: KFunction1<Array<T>, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<T, R> = udf(name, nondeterministic, varargFunc)

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference adapting its name by reflection.
 * For example: `val myUdf = udf.register(::myFunction)`
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargT")
inline fun <reified T, reified R> UDFRegistration.register(
    varargFunc: KFunction1<Array<T>, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<T, R> = register(udf(varargFunc, nondeterministic))

/**
 * Creates and registers a vararg UDF ([NamedUserDefinedFunctionVararg]) from a function reference.
 * For example: `val myUdf = udf.register("myFunction", ::myFunction)`
 *
 * If you want to process a column containing an Array<T> instead, use WrappedArray<T>.
 *
 * @param name Optional. Name for the UDF.
 * @param varargFunc function reference
 * @param nondeterministic Optional. If true, sets the UserDefinedFunction as nondeterministic.
 * @see udf
 */
@JvmName("registerVarargT")
inline fun <reified T, reified R> UDFRegistration.register(
    name: String,
    varargFunc: KFunction1<Array<T>, R>,
    nondeterministic: Boolean = false,
): NamedUserDefinedFunctionVararg<T, R> = register(udf(name, varargFunc, nondeterministic))
