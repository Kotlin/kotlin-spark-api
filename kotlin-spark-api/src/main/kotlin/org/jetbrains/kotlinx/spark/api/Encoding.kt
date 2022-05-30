/*-
 * =LICENSE=
 * Kotlin Spark API
 * ----------
 * Copyright (C) 2019 - 2020 JetBrains
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
 * This file contains the encoding logic for the Kotlin Spark API.
 * It provides encoders for Spark, based on reflection, for functions that need it.
 * Aside from the normal Spark encoders, it also provides encoding for Kotlin data classes, Iterables,
 * Products, Arrays, Maps etc.
 */

@file:Suppress("HasPlatformType", "unused", "FunctionName")

package org.jetbrains.kotlinx.spark.api

import org.apache.spark.sql.*
import org.apache.spark.sql.Encoders.*
import org.apache.spark.sql.KotlinReflection.*
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.types.*
import org.apache.spark.unsafe.types.CalendarInterval
import scala.Product
import scala.reflect.ClassTag
import java.beans.PropertyDescriptor
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.BooleanArray
import kotlin.Byte
import kotlin.ByteArray
import kotlin.Double
import kotlin.DoubleArray
import kotlin.ExperimentalStdlibApi
import kotlin.Float
import kotlin.FloatArray
import kotlin.IllegalArgumentException
import kotlin.Int
import kotlin.IntArray
import kotlin.Long
import kotlin.LongArray
import kotlin.OptIn
import kotlin.Short
import kotlin.ShortArray
import kotlin.String
import kotlin.Suppress
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.to

@JvmField
val ENCODERS: Map<KClass<*>, Encoder<*>> = mapOf(
    Boolean::class to BOOLEAN(),
    Byte::class to BYTE(),
    Short::class to SHORT(),
    Int::class to INT(),
    Long::class to LONG(),
    Float::class to FLOAT(),
    Double::class to DOUBLE(),
    String::class to STRING(),
    BigDecimal::class to DECIMAL(),
    Date::class to DATE(),
    LocalDate::class to LOCALDATE(), // 3.0+
    Timestamp::class to TIMESTAMP(),
    Instant::class to INSTANT(), // 3.0+
    ByteArray::class to BINARY(),
    Duration::class to DURATION(), // 3.2+
    Period::class to PERIOD(), // 3.2+
)

private val knownDataTypes: Map<KClass<out Any>, DataType> = mapOf(
    Byte::class to DataTypes.ByteType,
    Short::class to DataTypes.ShortType,
    Int::class to DataTypes.IntegerType,
    Long::class to DataTypes.LongType,
    Boolean::class to DataTypes.BooleanType,
    Float::class to DataTypes.FloatType,
    Double::class to DataTypes.DoubleType,
    String::class to DataTypes.StringType,
    LocalDate::class to DataTypes.DateType,
    Date::class to DataTypes.DateType,
    Timestamp::class to DataTypes.TimestampType,
    Instant::class to DataTypes.TimestampType,
    ByteArray::class to DataTypes.BinaryType,
    Decimal::class to DecimalType.SYSTEM_DEFAULT(),
    BigDecimal::class to DecimalType.SYSTEM_DEFAULT(),
    CalendarInterval::class to DataTypes.CalendarIntervalType,
)

/**
 * Main method of API, which gives you seamless integration with Spark:
 * It creates encoder for any given supported type T
 *
 * Supported types are data classes, primitives, and Lists, Maps and Arrays containing them
 *
 * @param T type, supported by Spark
 * @return generated encoder
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> encoder(): Encoder<T> = generateEncoder(typeOf<T>(), T::class)

/**
 * @see encoder
 */
@Suppress("UNCHECKED_CAST")
fun <T> generateEncoder(type: KType, cls: KClass<*>): Encoder<T> =
    when {
        isSupportedByKotlinClassEncoder(cls) -> kotlinClassEncoder(schema = memoizedSchema(type), kClass = cls)
        else -> ENCODERS[cls] as? Encoder<T>? ?: bean(cls.java)
    } as Encoder<T>

private fun isSupportedByKotlinClassEncoder(cls: KClass<*>): Boolean =
    when {
        cls == ByteArray::class -> false // uses binary encoder
        cls.isData -> true
        cls.isSubclassOf(Map::class) -> true
        cls.isSubclassOf(Iterable::class) -> true
        cls.isSubclassOf(Product::class) -> true
        cls.java.isArray -> true
        else -> false
    }


private fun <T> kotlinClassEncoder(schema: DataType, kClass: KClass<*>): Encoder<T> {
    val serializer =
        if (schema is DataTypeWithClass) serializerFor(kClass.java, schema)
        else serializerForType(getType(kClass.java))

    val deserializer =
        if (schema is DataTypeWithClass) deserializerFor(kClass.java, schema)
        else deserializerForType(getType(kClass.java))

    return ExpressionEncoder(serializer, deserializer, ClassTag.apply(kClass.java))
}

/**
 * Not meant to be used by the user explicitly.
 *
 * This function generates the DataType schema for supported classes, including Kotlin data classes, [Map],
 * [Iterable], [Product], [Array], and combinations of those.
 *
 * It's mainly used by [generateEncoder]/[encoder].
 */
@OptIn(ExperimentalStdlibApi::class)
fun schema(type: KType, map: Map<String, KType> = mapOf()): DataType {
    val primitiveSchema = knownDataTypes[type.classifier]
    if (primitiveSchema != null)
        return KSimpleTypeWrapper(
            /* dt = */ primitiveSchema,
            /* cls = */ (type.classifier!! as KClass<*>).java,
            /* nullable = */ type.isMarkedNullable
        )

    val klass = type.classifier as? KClass<*> ?: throw IllegalArgumentException("Unsupported type $type")
    val args = type.arguments

    val types = transitiveMerge(
        map,
        klass.typeParameters.zip(args).associate {
            it.first.name to it.second.type!!
        },
    )

    return when {
        klass.isSubclassOf(Enum::class) ->
            KSimpleTypeWrapper(
                /* dt = */ DataTypes.StringType,
                /* cls = */ klass.java,
                /* nullable = */ type.isMarkedNullable
            )

        klass.isSubclassOf(Iterable::class) || klass.java.isArray -> {
            val listParam = if (klass.java.isArray) {
                when (klass) {
                    IntArray::class -> typeOf<Int>()
                    LongArray::class -> typeOf<Long>()
                    FloatArray::class -> typeOf<Float>()
                    DoubleArray::class -> typeOf<Double>()
                    BooleanArray::class -> typeOf<Boolean>()
                    ShortArray::class -> typeOf<Short>()
                    /* ByteArray handled by BinaryType */
                    else -> types.getValue(klass.typeParameters[0].name)
                }
            } else types.getValue(klass.typeParameters[0].name)

            val dataType = DataTypes.createArrayType(
                /* elementType = */ schema(listParam, types),
                /* containsNull = */ listParam.isMarkedNullable
            )

            KComplexTypeWrapper(
                /* dt = */ dataType,
                /* cls = */ klass.java,
                /* nullable = */ type.isMarkedNullable
            )
        }

        klass.isSubclassOf(Map::class) -> {
            val mapKeyParam = types.getValue(klass.typeParameters[0].name)
            val mapValueParam = types.getValue(klass.typeParameters[1].name)

            val dataType = DataTypes.createMapType(
                /* keyType = */ schema(mapKeyParam, types),
                /* valueType = */ schema(mapValueParam, types),
                /* valueContainsNull = */ true
            )

            KComplexTypeWrapper(
                /* dt = */ dataType,
                /* cls = */ klass.java,
                /* nullable = */ type.isMarkedNullable
            )
        }

        klass.isData -> {
            val structType = StructType(
                klass
                    .primaryConstructor!!
                    .parameters
                    .filter { it.findAnnotation<Transient>() == null }
                    .map {
                        val projectedType = types[it.type.toString()] ?: it.type
                        val propertyDescriptor = PropertyDescriptor(
                            /* propertyName = */ it.name,
                            /* beanClass = */ klass.java,
                            /* readMethodName = */ "is" + it.name?.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                                else it.toString()
                            },
                            /* writeMethodName = */ null
                        )

                        KStructField(
                            /* getterName = */ propertyDescriptor.readMethod.name,
                            /* delegate = */ StructField(
                                /* name = */ it.name,
                                /* dataType = */ schema(projectedType, types),
                                /* nullable = */ projectedType.isMarkedNullable,
                                /* metadata = */ Metadata.empty()
                            )
                        )
                    }
                    .toTypedArray()
            )
            KDataTypeWrapper(structType, klass.java, true)
        }
        klass.isSubclassOf(Product::class) -> {

            // create map from T1, T2 to Int, String etc.
            val typeMap = klass.constructors.first().typeParameters.map { it.name }
                .zip(
                    type.arguments.map { it.type }
                )
                .toMap()

            // collect params by name and actual type
            val params = klass.constructors.first().parameters.map {
                val typeName = it.type.toString().replace("!", "")
                it.name to (typeMap[typeName] ?: it.type)
            }

            val structType = DataTypes.createStructType(
                params.map { (fieldName, fieldType) ->
                    val dataType = schema(fieldType, types)

                    KStructField(
                        /* getterName = */ fieldName,
                        /* delegate = */ StructField(
                            /* name = */ fieldName,
                            /* dataType = */ dataType,
                            /* nullable = */ fieldType.isMarkedNullable,
                            /* metadata = */Metadata.empty()
                        )
                    )
                }.toTypedArray()
            )

            KComplexTypeWrapper(
                /* dt = */ structType,
                /* cls = */ klass.java,
                /* nullable = */ true
            )
        }

        else -> throw IllegalArgumentException("$type is unsupported")
    }
}

/**
 * Memoized version of [schema]. This ensures the [DataType] of given `type` only
 * has to be calculated once.
 */
private val memoizedSchema: (type: KType) -> DataType = memoize {
    schema(it)
}

private fun transitiveMerge(a: Map<String, KType>, b: Map<String, KType>): Map<String, KType> =
    a + b.mapValues { a.getOrDefault(it.value.toString(), it.value) }

/** Wrapper around function with 1 argument to avoid recalculation when a certain argument is queried again. */
private class Memoize1<in T, out R>(private val function: (T) -> R) : (T) -> R {
    private val values = ConcurrentHashMap<T, R>()
    override fun invoke(x: T): R = values.getOrPut(x) { function(x) }
}

/** Wrapper around function to avoid recalculation when a certain argument is queried again. */
private fun <T, R> ((T) -> R).memoized(): (T) -> R = Memoize1(this)

/** Wrapper around function to avoid recalculation when a certain argument is queried again. */
private fun <T, R> memoize(function: (T) -> R): (T) -> R = Memoize1(function)

