package org.jetbrains.kotlinx.spark.api

import org.apache.spark.sql.*
import org.apache.spark.sql.api.java.UDF0
import org.apache.spark.sql.api.java.UDF1
import org.apache.spark.sql.api.java.UDF2
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.types.DataType
import scala.collection.mutable.WrappedArray
import kotlin.reflect.*
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

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
    if (this == String::class || isSubclassOf(WrappedArray::class))
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
    "Parameter $parameterName is subclass of ${kClass.qualifiedName}. If you need to process an array use ${WrappedArray::class.qualifiedName}."
)

// TODO

inline fun <reified IN, reified OUT, reified AGG : Aggregator<IN, *, OUT>> udafUnnamed(
    agg: AGG,
    nondeterministic: Boolean = false,
): TypedUserDefinedFunction1<IN, OUT> {
    IN::class.checkForValidType("IN")
    OUT::class.checkForValidType("OUT")

    return TypedUserDefinedFunction1(
        udf = functions.udaf(agg, encoder<IN>())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<OUT>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder<OUT>(),
    )
}

inline fun <reified IN, reified OUT, reified AGG : Aggregator<IN, *, OUT>> udaf(
    agg: AGG,
    name: String = agg::class.simpleName
        ?: error("Could not obtain name from [agg], either supply a name or use [udafUnnamed()]"),
    nondeterministic: Boolean = false,
): NamedTypedUserDefinedFunction1<IN, OUT> = udafUnnamed(agg = agg, nondeterministic = nondeterministic).withName(name)

/**
 * @param udf The underlying UDF
 * @param encoder Encoder for the return type of the UDF
 */
sealed interface TypedUserDefinedFunction<RETURN, NAMED> {
    val udf: UserDefinedFunction
    val encoder: Encoder<RETURN>

    /** Returns true when the UDF can return a nullable value. */
    val nullable: Boolean get() = udf.nullable()

    /** Returns true iff the UDF is deterministic, i.e. the UDF produces the same output given the same input. */
    val deterministic: Boolean get() = udf.deterministic()

    fun invokeUntyped(vararg params: Column): Column = udf.apply(*params)

    operator fun invoke(vararg params: Column): TypedColumn<*, RETURN> = invokeUntyped(*params).`as`(encoder)

    fun withName(name: String): NAMED

    operator fun getValue(thisRef: Any?, property: KProperty<*>): NAMED
}

/**
 * @param name Will set (overwrite) the name in [udf] if specified
 * @param udf The underlying UDF
 * @param encoder Encoder for the return type of the UDF
 */
sealed interface NamedTypedUserDefinedFunction<RETURN, NAMED> : TypedUserDefinedFunction<RETURN, NAMED> {
    val name: String
}

///** Can be used to set the name of a UDF with a by-delegate. */
//inline operator fun <R, reified T : TypedUserDefinedFunction<R>> T.getValue(thisRef: Any?, property: KProperty<*>): T =
//    copy(name = property.name)

/** Copy method for all [NamedTypedUserDefinedFunction] functions. */
inline fun <R, reified T : NamedTypedUserDefinedFunction<R, *>> T.copy(
    name: String = this.name,
    udf: UserDefinedFunction = this.udf,
    encoder: Encoder<R> = this.encoder,
): T = T::class.primaryConstructor!!.run {
    callBy(
        parameters.associateWith {
            when (it.name) {
                NamedTypedUserDefinedFunction<*, *>::name.name -> name
                NamedTypedUserDefinedFunction<*, *>::udf.name -> udf
                NamedTypedUserDefinedFunction<*, *>::encoder.name -> encoder
                else -> error("Wrong arguments")
            }
        }
    )
}
//
///** Copy method for all [TypedUserDefinedFunction] functions. */
//inline fun <R, reified T : UnnamedTypedUserDefinedFunction<R>> T.withName(
//    udf: UserDefinedFunction = this.udf,
//    encoder: Encoder<R> = this.encoder,
//): T = T::class.primaryConstructor!!.run {
//    callBy(
//        parameters.associateWith {
//            when (it.name) {
//                UnnamedTypedUserDefinedFunction<*>::udf.name -> udf
//                UnnamedTypedUserDefinedFunction<*>::encoder.name -> encoder
//                else -> error("Wrong arguments")
//            }
//        }
//    )
//}

open class TypedUserDefinedFunction0<R>(
    override val udf: UserDefinedFunction,
    override val encoder: Encoder<R>,
) : TypedUserDefinedFunction<R, NamedTypedUserDefinedFunction0<R>> {

    /** Calls the [functions.callUDF] for the UDF with the [udfName]. */
    operator fun invoke(): TypedColumn<*, R> = super.invoke()

    /** Calls the [functions.callUDF] for the UDF with the [udfName]. */
    fun invokeUntyped(): Column = super.invokeUntyped()

    override fun withName(name: String) = NamedTypedUserDefinedFunction0(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        withName(property.name)
}

class NamedTypedUserDefinedFunction0<R>(
    override val name: String,
    udf: UserDefinedFunction,
    encoder: Encoder<R>,
) : NamedTypedUserDefinedFunction<R, NamedTypedUserDefinedFunction0<R>>,
    TypedUserDefinedFunction0<R>(udf.withName(name), encoder)

/** allows for functions as properties to me made into a udf */
inline fun <reified R> udf(
    func: KProperty0<() -> R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
): NamedTypedUserDefinedFunction0<R> = udf(name, nondeterministic, func.get())

/** allows for `::myFunction.toUdf()` */
inline fun <reified R> udf(
    func: KFunction0<R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
): NamedTypedUserDefinedFunction0<R> = udf(name, nondeterministic, func)

inline fun <reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF0<R>,
): NamedTypedUserDefinedFunction0<R> = udf(nondeterministic = nondeterministic, func = func).withName(name)

inline fun <reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF0<R>,
): TypedUserDefinedFunction0<R> {
    return TypedUserDefinedFunction0<R>(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder(),
    )
}

open class TypedUserDefinedFunction1<T1, R>(
    override val udf: UserDefinedFunction,
    override val encoder: Encoder<R>,
) : TypedUserDefinedFunction<R, NamedTypedUserDefinedFunction1<T1, R>> {

    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given column. */
    operator fun invoke(param1: TypedColumn<*, T1>): TypedColumn<*, R> = super.invoke(param1)

    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given column. */
    fun invokeUntyped(param1: Column): Column = super.invokeUntyped(param1)

    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
    operator fun invoke(param1: Column): Column = super.invokeUntyped(param1)
    override fun withName(name: String) = NamedTypedUserDefinedFunction1<T1, R>(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    override fun getValue(thisRef: Any?, property: KProperty<*>) = withName(property.name)
}

class NamedTypedUserDefinedFunction1<T1, R>(
    override val name: String,
    udf: UserDefinedFunction,
    encoder: Encoder<R>,
) : NamedTypedUserDefinedFunction<R, NamedTypedUserDefinedFunction1<T1, R>>, // TODO
    TypedUserDefinedFunction1<T1, R>(udf = udf.withName(name), encoder = encoder)



/** allows for functions as properties to me made into a udf */
inline fun <reified T1, reified R> udf(
    func: KProperty0<(T1) -> R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
): NamedTypedUserDefinedFunction1<T1, R> = udf(name, nondeterministic, func.get())

/** allows for `::myFunction.toUdf()` */
inline fun <reified T1, reified R> udf(
    func: KFunction1<T1, R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
): NamedTypedUserDefinedFunction1<T1, R> = udf(name, nondeterministic, func)

inline fun <reified T1, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF1<T1, R>,
): NamedTypedUserDefinedFunction1<T1, R> = udf(nondeterministic = nondeterministic, func = func).withName(name)

inline fun <reified T1, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF1<T1, R>,
): TypedUserDefinedFunction1<T1, R> {
    T1::class.checkForValidType("T1")

    return TypedUserDefinedFunction1(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder(),
    )
}


open class TypedUserDefinedFunction2<T1, T2, R>(
    override val udf: UserDefinedFunction,
    override val encoder: Encoder<R>,
) : TypedUserDefinedFunction<R> {

    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given column. */
    operator fun invoke(param1: TypedColumn<*, T1>, param2: TypedColumn<*, T2>): TypedColumn<*, R> =
        super.invoke(param1, param2)

    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given column. */
    fun invokeUntyped(param1: Column, param2: Column): Column = super.invokeUntyped(param1, param2)

    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
    operator fun invoke(param1: Column, param2: Column): Column = super.invokeUntyped(param1, param2)

    override fun withName(name: String) = NamedTypedUserDefinedFunction2<T1, T2, R>(
        name = name,
        udf = udf,
        encoder = encoder,
    )

    override fun getValue(thisRef: Any?, property: KProperty<*>) = withName(property.name)
}

class NamedTypedUserDefinedFunction2<T1, T2, R>(
    override val name: String,
    udf: UserDefinedFunction,
    encoder: Encoder<R>
) :
    TypedUserDefinedFunction2<T1, T2, R>(udf = udf.withName(name), encoder = encoder),
    NamedTypedUserDefinedFunction<R>

/** allows for functions as properties to me made into a udf */
inline fun <reified T1, reified T2, reified R> udf(
    func: KProperty0<(T1, T2) -> R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
): NamedTypedUserDefinedFunction2<T1, T2, R> = udf(name, nondeterministic, func.get())

/** allows for `::myFunction.toUdf()` */
inline fun <reified T1, reified T2, reified R> udf(
    func: KFunction2<T1, T2, R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
): NamedTypedUserDefinedFunction2<T1, T2, R> = udf(name, nondeterministic, func)

inline fun <reified T1, reified T2, reified R> udf(
    name: String,
    nondeterministic: Boolean = false,
    func: UDF2<T1, T2, R>,
): NamedTypedUserDefinedFunction2<T1, T2, R> = udf(nondeterministic = nondeterministic, func = func).withName(name)

inline fun <reified T1, reified T2, reified R> udf(
    nondeterministic: Boolean = false,
    func: UDF2<T1, T2, R>,
): TypedUserDefinedFunction2<T1, T2, R> {
    T1::class.checkForValidType("T1")
    T2::class.checkForValidType("T2")

    return TypedUserDefinedFunction2(
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder(),
    )
}