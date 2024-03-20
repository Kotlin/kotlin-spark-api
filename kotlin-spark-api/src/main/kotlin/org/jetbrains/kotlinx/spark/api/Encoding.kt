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

import org.apache.spark.sql.Encoder
import org.apache.spark.sql.Row
import org.apache.spark.sql.catalyst.DefinedByConstructorParams
import org.apache.spark.sql.catalyst.SerializerBuildHelper
import org.apache.spark.sql.catalyst.encoders.AgnosticEncoder
import org.apache.spark.sql.catalyst.encoders.AgnosticEncoders
import org.apache.spark.sql.catalyst.encoders.AgnosticEncoders.EncoderField
import org.apache.spark.sql.catalyst.encoders.AgnosticEncoders.ProductEncoder
import org.apache.spark.sql.catalyst.encoders.OuterScopes
import org.apache.spark.sql.catalyst.expressions.objects.Invoke
import org.apache.spark.sql.types.DataType
import org.apache.spark.sql.types.Decimal
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.SQLUserDefinedType
import org.apache.spark.sql.types.UDTRegistration
import org.apache.spark.sql.types.UserDefinedType
import org.apache.spark.unsafe.types.CalendarInterval
import scala.reflect.ClassTag
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.typeOf

fun <T : Any> kotlinEncoderFor(
    kClass: KClass<T>,
    arguments: List<KTypeProjection> = emptyList(),
    nullable: Boolean = false,
    annotations: List<Annotation> = emptyList()
): Encoder<T> =
    applyEncoder(
        KotlinTypeInference.encoderFor(
            kClass = kClass,
            arguments = arguments,
            nullable = nullable,
            annotations = annotations,
        )
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
inline fun <reified T> kotlinEncoderFor(): Encoder<T> =
    kotlinEncoderFor(
        typeOf<T>()
    )

fun <T> kotlinEncoderFor(kType: KType): Encoder<T> =
    applyEncoder(
        KotlinTypeInference.encoderFor(kType)
    )

/**
 * For spark-connect, no ExpressionEncoder is needed, so we can just return the AgnosticEncoder.
 */
private fun <T> applyEncoder(agnosticEncoder: AgnosticEncoder<T>): Encoder<T> {
    //#if sparkConnect == false
    return org.apache.spark.sql.catalyst.encoders.ExpressionEncoder.apply(agnosticEncoder)
    //#else
    //$return agnosticEncoder
    //#endif
}


@Deprecated("Use kotlinEncoderFor instead", ReplaceWith("kotlinEncoderFor<T>()"))
inline fun <reified T> encoder(): Encoder<T> = kotlinEncoderFor(typeOf<T>())

@Deprecated("Use kotlinEncoderFor to get the schema.", ReplaceWith("kotlinEncoderFor<T>().schema()"))
inline fun <reified T> schema(): DataType = kotlinEncoderFor<T>().schema()

@Deprecated("Use kotlinEncoderFor to get the schema.", ReplaceWith("kotlinEncoderFor<Any?>(kType).schema()"))
fun schema(kType: KType): DataType = kotlinEncoderFor<Any?>(kType).schema()

object KotlinTypeInference {

    /**
     * @param kClass the class for which to infer the encoder.
     * @param arguments the generic type arguments for the class.
     * @param nullable whether the class is nullable.
     * @param annotations the annotations for the class.
     * @return an [AgnosticEncoder] for the given class arguments.
     */
    fun <T : Any> encoderFor(
        kClass: KClass<T>,
        arguments: List<KTypeProjection> = emptyList(),
        nullable: Boolean = false,
        annotations: List<Annotation> = emptyList()
    ): AgnosticEncoder<T> = encoderFor(
        kType = kClass.createType(
            arguments = arguments,
            nullable = nullable,
            annotations = annotations,
        )
    )

    /**
     * @return an [AgnosticEncoder] for the given type [T].
     */
    @JvmName("inlineEncoderFor")
    inline fun <reified T> encoderFor(): AgnosticEncoder<T> =
        encoderFor(kType = typeOf<T>())

    /**
     * Main entry function for the inference of encoders.
     *
     * @return an [AgnosticEncoder] for the given [kType].
     */
    fun <T> encoderFor(kType: KType): AgnosticEncoder<T> =
        encoderFor(
            currentType = kType,
            seenTypeSet = emptySet(),
            typeVariables = emptyMap(),
        ) as AgnosticEncoder<T>


    private inline fun <reified T> KType.isSubtypeOf(): Boolean = isSubtypeOf(typeOf<T>())

    private val KType.simpleName
        get() = toString().removeSuffix("?").removeSuffix("!")

    private fun KType.isDefinedByScalaConstructorParams(): Boolean = when {
        isSubtypeOf<scala.Option<*>?>() -> arguments.first().type!!.isDefinedByScalaConstructorParams()
        else -> isSubtypeOf<scala.Product?>() || isSubtypeOf<DefinedByConstructorParams?>()
    }

    private fun KType.getScalaConstructorParameters(
        genericTypeMap: Map<String, KType>,
        kClass: KClass<*> = classifier as KClass<*>,
    ): List<Pair<String, KType>> {
        val constructor =
            kClass.primaryConstructor
                ?: kClass.constructors.firstOrNull()
                ?: kClass.staticFunctions.firstOrNull {
                    it.name == "apply" && it.returnType.classifier == kClass
                }
                ?: error("couldn't find constructor for $this")

        val kParameters = constructor.parameters
        val params = kParameters.map { param ->
            val paramType = if (param.type.isSubtypeOf<scala.AnyVal>()) {
                // Replace value class with underlying type
                param.type.getScalaConstructorParameters(genericTypeMap).first().second
            } else {
                // check if the type was a filled-in generic type, otherwise just use the given type
                genericTypeMap[param.type.simpleName] ?: param.type
            }

            param.name!! to paramType
        }

        return params
    }

    /**
     * Can merge two maps transitively.
     * This means that given
     * ```
     * a: { A -> B, D -> E }
     * b: { B -> C, G -> F }
     * ```
     * it will return
     * ```
     * { A -> C, D -> E, G -> F }
     * ```
     * @param valueToKey a function that returns (an optional) key for a given value
     */
    private fun <K, V> transitiveMerge(a: Map<K, V>, b: Map<K, V>, valueToKey: (V) -> K?): Map<K, V> =
        a + b.mapValues { a.getOrDefault(valueToKey(it.value), it.value) }

    /**
     *
     */
    private fun encoderFor(
        currentType: KType,
        seenTypeSet: Set<KType>,

        // how the generic types of the data class (like T, S) are filled in for this instance of the class
        typeVariables: Map<String, KType>,
    ): AgnosticEncoder<*> {
        val kClass =
            currentType.classifier as? KClass<*> ?: throw IllegalArgumentException("Unsupported type $currentType")
        val jClass = kClass.java

        // given t == typeOf<Pair<Int, Pair<String, Any>>>(), these are [Int, Pair<String, Any>]
        val tArguments = currentType.arguments

        // the type arguments of the class, like T, S
        val expectedTypeParameters = kClass.typeParameters.map { it }

        @Suppress("NAME_SHADOWING")
        val typeVariables = transitiveMerge(
            a = typeVariables,
            b = (expectedTypeParameters zip tArguments).toMap()
                .mapValues { (expectedType, givenType) ->
                    if (givenType.type != null) return@mapValues givenType.type!! // fill in the type as is

                    // when givenType is *, use the upperbound
                    expectedType.upperBounds.first()
                }.mapKeys { it.key.name }
        ) { it.simpleName }

        return when {
            // primitives java / kotlin
            currentType == typeOf<Boolean>() -> AgnosticEncoders.`PrimitiveBooleanEncoder$`.`MODULE$`
            currentType == typeOf<Byte>() -> AgnosticEncoders.`PrimitiveByteEncoder$`.`MODULE$`
            currentType == typeOf<Short>() -> AgnosticEncoders.`PrimitiveShortEncoder$`.`MODULE$`
            currentType == typeOf<Int>() -> AgnosticEncoders.`PrimitiveIntEncoder$`.`MODULE$`
            currentType == typeOf<Long>() -> AgnosticEncoders.`PrimitiveLongEncoder$`.`MODULE$`
            currentType == typeOf<Float>() -> AgnosticEncoders.`PrimitiveFloatEncoder$`.`MODULE$`
            currentType == typeOf<Double>() -> AgnosticEncoders.`PrimitiveDoubleEncoder$`.`MODULE$`

            // primitives scala
            currentType == typeOf<scala.Boolean>() -> AgnosticEncoders.`PrimitiveBooleanEncoder$`.`MODULE$`
            currentType == typeOf<scala.Byte>() -> AgnosticEncoders.`PrimitiveByteEncoder$`.`MODULE$`
            currentType == typeOf<scala.Short>() -> AgnosticEncoders.`PrimitiveShortEncoder$`.`MODULE$`
            currentType == typeOf<scala.Int>() -> AgnosticEncoders.`PrimitiveIntEncoder$`.`MODULE$`
            currentType == typeOf<scala.Long>() -> AgnosticEncoders.`PrimitiveLongEncoder$`.`MODULE$`
            currentType == typeOf<scala.Float>() -> AgnosticEncoders.`PrimitiveFloatEncoder$`.`MODULE$`
            currentType == typeOf<scala.Double>() -> AgnosticEncoders.`PrimitiveDoubleEncoder$`.`MODULE$`

            // boxed primitives java / kotlin
            currentType.isSubtypeOf<Boolean?>() -> AgnosticEncoders.`BoxedBooleanEncoder$`.`MODULE$`
            currentType.isSubtypeOf<Byte?>() -> AgnosticEncoders.`BoxedByteEncoder$`.`MODULE$`
            currentType.isSubtypeOf<Short?>() -> AgnosticEncoders.`BoxedShortEncoder$`.`MODULE$`
            currentType.isSubtypeOf<Int?>() -> AgnosticEncoders.`BoxedIntEncoder$`.`MODULE$`
            currentType.isSubtypeOf<Long?>() -> AgnosticEncoders.`BoxedLongEncoder$`.`MODULE$`
            currentType.isSubtypeOf<Float?>() -> AgnosticEncoders.`BoxedFloatEncoder$`.`MODULE$`
            currentType.isSubtypeOf<Double?>() -> AgnosticEncoders.`BoxedDoubleEncoder$`.`MODULE$`

            // boxed primitives scala
            currentType.isSubtypeOf<scala.Boolean?>() -> AgnosticEncoders.`BoxedBooleanEncoder$`.`MODULE$`
            currentType.isSubtypeOf<scala.Byte?>() -> AgnosticEncoders.`BoxedByteEncoder$`.`MODULE$`
            currentType.isSubtypeOf<scala.Short?>() -> AgnosticEncoders.`BoxedShortEncoder$`.`MODULE$`
            currentType.isSubtypeOf<scala.Int?>() -> AgnosticEncoders.`BoxedIntEncoder$`.`MODULE$`
            currentType.isSubtypeOf<scala.Long?>() -> AgnosticEncoders.`BoxedLongEncoder$`.`MODULE$`
            currentType.isSubtypeOf<scala.Float?>() -> AgnosticEncoders.`BoxedFloatEncoder$`.`MODULE$`
            currentType.isSubtypeOf<scala.Double?>() -> AgnosticEncoders.`BoxedDoubleEncoder$`.`MODULE$`

            // leaf encoders
            currentType.isSubtypeOf<String?>() -> AgnosticEncoders.`StringEncoder$`.`MODULE$`
            currentType.isSubtypeOf<Decimal?>() -> AgnosticEncoders.DEFAULT_SPARK_DECIMAL_ENCODER()
            currentType.isSubtypeOf<scala.math.BigDecimal?>() -> AgnosticEncoders.DEFAULT_SCALA_DECIMAL_ENCODER()
            currentType.isSubtypeOf<scala.math.BigInt?>() -> AgnosticEncoders.`ScalaBigIntEncoder$`.`MODULE$`
            currentType.isSubtypeOf<ByteArray?>() -> AgnosticEncoders.`BinaryEncoder$`.`MODULE$`
            currentType.isSubtypeOf<java.math.BigDecimal?>() -> AgnosticEncoders.DEFAULT_JAVA_DECIMAL_ENCODER()
            currentType.isSubtypeOf<java.math.BigInteger?>() -> AgnosticEncoders.`JavaBigIntEncoder$`.`MODULE$`
            currentType.isSubtypeOf<CalendarInterval?>() -> AgnosticEncoders.`CalendarIntervalEncoder$`.`MODULE$`
            currentType.isSubtypeOf<java.time.LocalDate?>() -> AgnosticEncoders.STRICT_LOCAL_DATE_ENCODER()
            currentType.isSubtypeOf<kotlinx.datetime.LocalDate?>() -> TODO("User java.time.LocalDate for now. We'll create a UDT for this.")
            currentType.isSubtypeOf<java.sql.Date?>() -> AgnosticEncoders.STRICT_DATE_ENCODER()
            currentType.isSubtypeOf<java.time.Instant?>() -> AgnosticEncoders.STRICT_INSTANT_ENCODER()
            currentType.isSubtypeOf<kotlinx.datetime.Instant?>() -> TODO("Use java.time.Instant for now. We'll create a UDT for this.")
            currentType.isSubtypeOf<kotlin.time.TimeMark?>() -> TODO("Use java.time.Instant for now. We'll create a UDT for this.")
            currentType.isSubtypeOf<java.sql.Timestamp?>() -> AgnosticEncoders.STRICT_TIMESTAMP_ENCODER()
            currentType.isSubtypeOf<java.time.LocalDateTime?>() -> AgnosticEncoders.`LocalDateTimeEncoder$`.`MODULE$`
            currentType.isSubtypeOf<kotlinx.datetime.LocalDateTime?>() -> TODO("Use java.time.LocalDateTime for now. We'll create a UDT for this.")
            currentType.isSubtypeOf<java.time.Duration?>() -> AgnosticEncoders.`DayTimeIntervalEncoder$`.`MODULE$`
            currentType.isSubtypeOf<kotlin.time.Duration?>() -> TODO("Use java.time.Duration for now. We'll create a UDT for this.")
            currentType.isSubtypeOf<java.time.Period?>() -> AgnosticEncoders.`YearMonthIntervalEncoder$`.`MODULE$`
            currentType.isSubtypeOf<kotlinx.datetime.DateTimePeriod?>() -> TODO("Use java.time.Period for now. We'll create a UDT for this.")
            currentType.isSubtypeOf<kotlinx.datetime.DatePeriod?>() -> TODO("Use java.time.Period for now. We'll create a UDT for this.")
            currentType.isSubtypeOf<Row?>() -> AgnosticEncoders.`UnboundRowEncoder$`.`MODULE$`

            // enums
            kClass.isSubclassOf(Enum::class) -> AgnosticEncoders.JavaEnumEncoder(ClassTag.apply<Any?>(jClass))

            // TODO test
            kClass.isSubclassOf(scala.Enumeration.Value::class) ->
                AgnosticEncoders.ScalaEnumEncoder(jClass.superclass, ClassTag.apply<Any?>(jClass))

            // udts
            kClass.hasAnnotation<SQLUserDefinedType>() -> {
                val annotation = jClass.getAnnotation(SQLUserDefinedType::class.java)!!
                val udtClass = annotation.udt
                val udt = udtClass.primaryConstructor!!.call()
                AgnosticEncoders.UDTEncoder(udt, udtClass.java)
            }

            UDTRegistration.exists(kClass.jvmName) -> {
                val udt = UDTRegistration.getUDTFor(kClass.jvmName)!!
                    .get()!!
                    .getConstructor()
                    .newInstance() as UserDefinedType<*>

                AgnosticEncoders.UDTEncoder(udt, udt.javaClass)
            }

            currentType.isSubtypeOf<scala.Option<*>?>() -> {
                val elementEncoder = encoderFor(
                    currentType = tArguments.first().type!!,
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.OptionEncoder(elementEncoder)
            }

            // primitive arrays
            currentType.isSubtypeOf<IntArray?>() -> {
                val elementEncoder = encoderFor(
                    currentType = typeOf<Int>(),
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.ArrayEncoder(elementEncoder, false)
            }

            currentType.isSubtypeOf<DoubleArray?>() -> {
                val elementEncoder = encoderFor(
                    currentType = typeOf<Double>(),
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.ArrayEncoder(elementEncoder, false)
            }

            currentType.isSubtypeOf<FloatArray?>() -> {
                val elementEncoder = encoderFor(
                    currentType = typeOf<Float>(),
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.ArrayEncoder(elementEncoder, false)
            }

            currentType.isSubtypeOf<ShortArray?>() -> {
                val elementEncoder = encoderFor(
                    currentType = typeOf<Short>(),
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.ArrayEncoder(elementEncoder, false)
            }

            currentType.isSubtypeOf<LongArray?>() -> {
                val elementEncoder = encoderFor(
                    currentType = typeOf<Long>(),
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.ArrayEncoder(elementEncoder, false)
            }

            currentType.isSubtypeOf<BooleanArray?>() -> {
                val elementEncoder = encoderFor(
                    currentType = typeOf<Boolean>(),
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.ArrayEncoder(elementEncoder, false)
            }

            // boxed arrays
            jClass.isArray -> {
                val type = currentType.arguments.first().type!!
                val elementEncoder = encoderFor(
                    currentType = type.withNullability(true), // so we get a boxed array
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.ArrayEncoder(elementEncoder, true)
            }

            currentType.isSubtypeOf<List<*>?>() -> {
                val subType = tArguments.first().type!!
                val elementEncoder = encoderFor(
                    currentType = subType,
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.IterableEncoder<List<*>, _>(
                    /* clsTag = */ ClassTag.apply(jClass),
                    /* element = */ elementEncoder,
                    /* containsNull = */ subType.isMarkedNullable,
                    /* lenientSerialization = */ false,
                )
            }

            currentType.isSubtypeOf<scala.collection.Seq<*>?>() -> {
                val subType = tArguments.first().type!!
                val elementEncoder = encoderFor(
                    currentType = subType,
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.IterableEncoder<scala.collection.Seq<*>, _>(
                    /* clsTag = */ ClassTag.apply(jClass),
                    /* element = */ elementEncoder,
                    /* containsNull = */ subType.isMarkedNullable,
                    /* lenientSerialization = */ false,
                )
            }

            currentType.isSubtypeOf<Set<*>?>() -> {
                val subType = tArguments.first().type!!
                val elementEncoder = encoderFor(
                    currentType = subType,
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.IterableEncoder<Set<*>, _>(
                    /* clsTag = */ ClassTag.apply(jClass),
                    /* element = */ elementEncoder,
                    /* containsNull = */ subType.isMarkedNullable,
                    /* lenientSerialization = */ false,
                )
            }

            currentType.isSubtypeOf<scala.collection.Set<*>?>() -> {
                val subType = tArguments.first().type!!
                val elementEncoder = encoderFor(
                    currentType = subType,
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.IterableEncoder<scala.collection.Set<*>, _>(
                    /* clsTag = */ ClassTag.apply(jClass),
                    /* element = */ elementEncoder,
                    /* containsNull = */ subType.isMarkedNullable,
                    /* lenientSerialization = */ false,
                )
            }

            currentType.isSubtypeOf<Map<*, *>?>() || currentType.isSubtypeOf<scala.collection.Map<*, *>?>() -> {
                val keyEncoder = encoderFor(
                    currentType = tArguments[0].type!!,
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                val valueEncoder = encoderFor(
                    currentType = tArguments[1].type!!,
                    seenTypeSet = seenTypeSet,
                    typeVariables = typeVariables,
                )
                AgnosticEncoders.MapEncoder(
                    /* clsTag = */ ClassTag.apply<Map<*, *>>(jClass),
                    /* keyEncoder = */ keyEncoder,
                    /* valueEncoder = */ valueEncoder,
                    /* valueContainsNull = */ tArguments[1].type!!.isMarkedNullable,
                )
            }

            kClass.isData -> {
                if (currentType in seenTypeSet) throw IllegalStateException("Circular reference detected for type $currentType")
                val constructor = kClass.primaryConstructor!!
                val kParameters = constructor.parameters
                // todo filter for transient?

                val props = kParameters.map {
                    kClass.declaredMemberProperties.find { prop -> prop.name == it.name }!!
                }

                val params = (kParameters zip props).map { (param, prop) ->
                    // check if the type was a filled-in generic type, otherwise just use the given type
                    val paramType = typeVariables[param.type.simpleName] ?: param.type
                    val encoder = encoderFor(
                        currentType = paramType,
                        seenTypeSet = seenTypeSet + currentType,
                        typeVariables = typeVariables,
                    )

                    val paramName = param.name
                    val readMethodName = prop.getter.javaMethod!!.name
                    val writeMethodName = (prop as? KMutableProperty<*>)?.setter?.javaMethod?.name

                    EncoderField(
                        /* name = */ readMethodName,
                        /* enc = */ encoder,
                        /* nullable = */ paramType.isMarkedNullable,
                        /* metadata = */ Metadata.empty(),
                        /* readMethod = */ readMethodName.toOption(),
                        /* writeMethod = */ writeMethodName.toOption(),
                    )
                }
                ProductEncoder<Any>(
                    /* clsTag = */ ClassTag.apply(jClass),
                    /* fields = */ params.asScalaSeq(),
                    /* outerPointerGetter = */ OuterScopes.getOuterScope(jClass).toOption(),
                )
            }

            currentType.isDefinedByScalaConstructorParams() -> {
                if (currentType in seenTypeSet) throw IllegalStateException("Circular reference detected for type $currentType")
                val constructorParams = currentType.getScalaConstructorParameters(typeVariables, kClass)

                val params = constructorParams.map { (paramName, paramType) ->
                    val encoder = encoderFor(
                        currentType = paramType,
                        seenTypeSet = seenTypeSet + currentType,
                        typeVariables = typeVariables,
                    )
                    AgnosticEncoders.EncoderField(
                        /* name = */ paramName,
                        /* enc = */ encoder,
                        /* nullable = */ paramType.isMarkedNullable,
                        /* metadata = */ Metadata.empty(),
                        /* readMethod = */ paramName.toOption(),
                        /* writeMethod = */ null.toOption(),
                    )
                }
                ProductEncoder<Any>(
                    /* clsTag = */ ClassTag.apply(jClass),
                    /* fields = */ params.asScalaSeq(),
                    /* outerPointerGetter = */ OuterScopes.getOuterScope(jClass).toOption(),
                )
            }

            // java bean class
//            currentType.classifier is KClass<*> -> {
//                TODO()
//
//                 JavaBeanEncoder()
//            }

            else -> throw IllegalArgumentException("No encoder found for type $currentType")
        }
    }
}