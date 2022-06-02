package org.jetbrains.kotlinx.spark.api

import org.apache.spark.sql.*
import org.apache.spark.sql.api.java.UDF0
import org.apache.spark.sql.api.java.UDF1
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

sealed class TypedUserDefinedFunction<R>(
    val name: String? = null,
    udf: UserDefinedFunction,
    val encoder: Encoder<R>,
) {
    val udf: UserDefinedFunction = name?.let { udf.withName(it) } ?: udf

    /** Returns true when the UDF can return a nullable value. */
    val nullable: Boolean get() = udf.nullable()

    /** Returns true iff the UDF is deterministic, i.e. the UDF produces the same output given the same input. */
    val deterministic: Boolean get() = udf.deterministic()

    protected fun invokeUntyped(vararg params: Column): Column = udf.apply(*params)

    protected operator fun invoke(vararg params: Column): TypedColumn<*, R> = invokeUntyped(*params).`as`(encoder)
}

/** Can be used to set the name of a UDF with a by-delegate. */
inline operator fun <reified T : TypedUserDefinedFunction<*>> T.getValue(thisRef: Any?, property: KProperty<*>): T =
    withName(property.name)

/** Creates a copy of the original [TypedUserDefinedFunction] but with a new udf (with new name). */
inline fun <reified T : TypedUserDefinedFunction<*>> T.withName(name: String): T =
    T::class.primaryConstructor!!.run {
        callBy(
            parameters.associateWith {
                when (it.name) {
                    TypedUserDefinedFunction<*>::name.name -> name
                    TypedUserDefinedFunction<*>::udf.name -> udf
                    TypedUserDefinedFunction<*>::encoder.name -> encoder
                    else -> error("Wrong arguments")
                }
            }
        )
    }


class TypedUserDefinedFunction0<R>(name: String?, udf: UserDefinedFunction, encoder: Encoder<R>) :
    TypedUserDefinedFunction<R>(name, udf, encoder) {

    /** Calls the [functions.callUDF] for the UDF with the [udfName]. */
    operator fun invoke(): TypedColumn<*, R> = super.invoke()

    /** Calls the [functions.callUDF] for the UDF with the [udfName]. */
    fun invokeUntyped(): Column = super.invokeUntyped()
}

inline fun <reified R> udf(
    name: String? = null,
    nondeterministic: Boolean = false,
    func: UDF0<R>,
): TypedUserDefinedFunction0<R> {
    return TypedUserDefinedFunction0(
        name = name,
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder(),
    )
}

class TypedUserDefinedFunction1<T1, R>(name: String?, udf: UserDefinedFunction, encoder: Encoder<R>) :
    TypedUserDefinedFunction<R>(name, udf, encoder) {

    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given column. */
    operator fun invoke(param1: TypedColumn<*, T1>): TypedColumn<*, R> = super.invoke(param1)

    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given column. */
    fun invokeUntyped(param1: Column): Column = super.invokeUntyped(param1)

    /** Calls the [functions.callUDF] for the UDF with the [udfName] and the given columns. */
    operator fun invoke(param1: Column): Column = super.invokeUntyped(param1)
}

/** allows for functions as properties to me made into a udf */
inline fun <reified T1, reified R> udf(
    func: KProperty0<(T1) -> R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
): TypedUserDefinedFunction1<T1, R> = udf(name, nondeterministic, func.get())

/** allows for `::myFunction.toUdf()` */
inline fun <reified T1, reified R> udf(
    func: KFunction1<T1, R>,
    name: String = func.name,
    nondeterministic: Boolean = false,
): TypedUserDefinedFunction1<T1, R> = udf(name, nondeterministic, func)

inline fun <reified T1, reified R> udf(
    name: String? = null,
    nondeterministic: Boolean = false,
    func: UDF1<T1, R>,
): TypedUserDefinedFunction1<T1, R> {
    T1::class.checkForValidType("T1")

    return TypedUserDefinedFunction1(
        name = name,
        udf = functions.udf(func, schema(typeOf<R>()).unWrap())
            .let { if (nondeterministic) it.asNondeterministic() else it }
            .let { if (typeOf<R>().isMarkedNullable) it else it.asNonNullable() },
        encoder = encoder(),
    )
}