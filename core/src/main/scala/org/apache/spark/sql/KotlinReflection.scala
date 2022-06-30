/*-
 * =LICENSE=
 * Kotlin Spark API: Examples
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

package org.apache.spark.sql

import org.apache.commons.lang3.reflect.ConstructorUtils
import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.DeserializerBuildHelper._
import org.apache.spark.sql.catalyst.ScalaReflection.{Schema, dataTypeFor, getClassFromType, isSubtype, javaBoxedType, localTypeOf, mirror, universe}
import org.apache.spark.sql.catalyst.SerializerBuildHelper._
import org.apache.spark.sql.catalyst.analysis.GetColumnByOrdinal
import org.apache.spark.sql.catalyst.expressions.objects._
import org.apache.spark.sql.catalyst.expressions.{Expression, _}
import org.apache.spark.sql.catalyst.util.ArrayBasedMapData
import org.apache.spark.sql.catalyst.{DefinedByConstructorParams, InternalRow, ScalaReflection, WalkedTypePath}
import org.apache.spark.sql.types._
import org.apache.spark.unsafe.types.{CalendarInterval, UTF8String}
import org.apache.spark.util.Utils

import java.beans.{Introspector, PropertyDescriptor}
import java.lang.Exception


/**
 * A helper trait to create [[org.apache.spark.sql.catalyst.encoders.ExpressionEncoder]]s
 * for classes whose fields are entirely defined by constructor params but should not be
 * case classes.
 */
//trait DefinedByConstructorParams

/**
 * KotlinReflection is heavily inspired by ScalaReflection and even extends it just to add several methods
 */
//noinspection RedundantBlock
object KotlinReflection extends KotlinReflection {
    /**
     * Returns the Spark SQL DataType for a given java class.  Where this is not an exact mapping
     * to a native type, an ObjectType is returned.
     *
     * Unlike `inferDataType`, this function doesn't do any massaging of types into the Spark SQL type
     * system.  As a result, ObjectType will be returned for things like boxed Integers.
     */
    private def inferExternalType(cls: Class[_]): DataType = cls match {
        case c if c == java.lang.Boolean.TYPE => BooleanType
        case c if c == java.lang.Byte.TYPE => ByteType
        case c if c == java.lang.Short.TYPE => ShortType
        case c if c == java.lang.Integer.TYPE => IntegerType
        case c if c == java.lang.Long.TYPE => LongType
        case c if c == java.lang.Float.TYPE => FloatType
        case c if c == java.lang.Double.TYPE => DoubleType
        case c if c == classOf[Array[Byte]] => BinaryType
        case c if c == classOf[Decimal] => DecimalType.SYSTEM_DEFAULT
        case c if c == classOf[CalendarInterval] => CalendarIntervalType
        case _ => ObjectType(cls)
    }

    val universe: scala.reflect.runtime.universe.type = scala.reflect.runtime.universe

    // Since we are creating a runtime mirror using the class loader of current thread,
    // we need to use def at here. So, every time we call mirror, it is using the
    // class loader of the current thread.
    override def mirror: universe.Mirror = {
        universe.runtimeMirror(Thread.currentThread().getContextClassLoader)
    }

    import universe._

    // The Predef.Map is scala.collection.immutable.Map.
    // Since the map values can be mutable, we explicitly import scala.collection.Map at here.
    import scala.collection.Map


    def isSubtype(t: universe.Type, t2: universe.Type): Boolean = t <:< t2

    /**
     * Synchronize to prevent concurrent usage of `<:<` operator.
     * This operator is not thread safe in any current version of scala; i.e.
     * (2.11.12, 2.12.10, 2.13.0-M5).
     *
     * See https://github.com/scala/bug/issues/10766
     */
    /*
      private[catalyst] def isSubtype(tpe1: `Type`, tpe2: `Type`): Boolean = {
        ScalaReflection.ScalaSubtypeLock.synchronized {
          tpe1 <:< tpe2
        }
      }
    */

    private def dataTypeFor(tpe: `Type`): DataType = cleanUpReflectionObjects {
        tpe.dealias match {
            case t if isSubtype(t, definitions.NullTpe) => NullType
            case t if isSubtype(t, definitions.IntTpe) => IntegerType
            case t if isSubtype(t, definitions.LongTpe) => LongType
            case t if isSubtype(t, definitions.DoubleTpe) => DoubleType
            case t if isSubtype(t, definitions.FloatTpe) => FloatType
            case t if isSubtype(t, definitions.ShortTpe) => ShortType
            case t if isSubtype(t, definitions.ByteTpe) => ByteType
            case t if isSubtype(t, definitions.BooleanTpe) => BooleanType
            case t if isSubtype(t, localTypeOf[Array[Byte]]) => BinaryType
            case t if isSubtype(t, localTypeOf[CalendarInterval]) => CalendarIntervalType
            case t if isSubtype(t, localTypeOf[Decimal]) => DecimalType.SYSTEM_DEFAULT
            case _ => {
                val className = getClassNameFromType(tpe)
                className match {
                    case "scala.Array" => {
                        val TypeRef(_, _, Seq(elementType)) = tpe.dealias
                        arrayClassFor(elementType)
                    }
                    case _ => {
                        val clazz = getClassFromType(tpe)
                        ObjectType(clazz)
                    }
                }
            }
        }
    }

    /**
     * Given a type `T` this function constructs `ObjectType` that holds a class of type
     * `Array[T]`.
     *
     * Special handling is performed for primitive types to map them back to their raw
     * JVM form instead of the Scala Array that handles auto boxing.
     */
    private def arrayClassFor(tpe: `Type`): ObjectType = cleanUpReflectionObjects {
        val cls = tpe.dealias match {
            case t if isSubtype(t, definitions.IntTpe) => classOf[Array[Int]]
            case t if isSubtype(t, definitions.LongTpe) => classOf[Array[Long]]
            case t if isSubtype(t, definitions.DoubleTpe) => classOf[Array[Double]]
            case t if isSubtype(t, definitions.FloatTpe) => classOf[Array[Float]]
            case t if isSubtype(t, definitions.ShortTpe) => classOf[Array[Short]]
            case t if isSubtype(t, definitions.ByteTpe) => classOf[Array[Byte]]
            case t if isSubtype(t, definitions.BooleanTpe) => classOf[Array[Boolean]]
            case t if isSubtype(t, localTypeOf[Array[Byte]]) => classOf[Array[Array[Byte]]]
            case t if isSubtype(t, localTypeOf[CalendarInterval]) => classOf[Array[CalendarInterval]]
            case t if isSubtype(t, localTypeOf[Decimal]) => classOf[Array[Decimal]]
            case other => {
                // There is probably a better way to do this, but I couldn't find it...
                val elementType = dataTypeFor(other).asInstanceOf[ObjectType].cls
                java.lang.reflect.Array.newInstance(elementType, 0).getClass
            }

        }
        ObjectType(cls)
    }

    /**
     * Returns true if the value of this data type is same between internal and external.
     */
    def isNativeType(dt: DataType): Boolean = dt match {
        case NullType | BooleanType | ByteType | ShortType | IntegerType | LongType |
             FloatType | DoubleType | BinaryType | CalendarIntervalType => {
            true
        }
        case _ => false
    }

    private def baseType(tpe: `Type`): `Type` = {
        tpe.dealias match {
            case annotatedType: AnnotatedType => annotatedType.underlying
            case other => other
        }
    }

    /**
     * Returns an expression that can be used to deserialize a Spark SQL representation to an object
     * of type `T` with a compatible schema. The Spark SQL representation is located at ordinal 0 of
     * a row, i.e., `GetColumnByOrdinal(0, _)`. Nested classes will have their fields accessed using
     * `UnresolvedExtractValue`.
     *
     * The returned expression is used by `ExpressionEncoder`. The encoder will resolve and bind this
     * deserializer expression when using it.
     */
    def deserializerForType(tpe: `Type`): Expression = {
        val clsName = getClassNameFromType(tpe)
        val walkedTypePath = WalkedTypePath().recordRoot(clsName)
        val Schema(dataType, nullable) = schemaFor(tpe)

        // Assumes we are deserializing the first column of a row.
        deserializerForWithNullSafetyAndUpcast(
            GetColumnByOrdinal(0, dataType), dataType,
            nullable = nullable, walkedTypePath,
            (casted, typePath) => deserializerFor(tpe, casted, typePath)
        )
    }


    /**
     * Returns an expression that can be used to deserialize an input expression to an object of type
     * `T` with a compatible schema.
     *
     * @param tpe            The `Type` of deserialized object.
     * @param path           The expression which can be used to extract serialized value.
     * @param walkedTypePath The paths from top to bottom to access current field when deserializing.
     */
    private def deserializerFor(
        tpe: `Type`,
        path: Expression,
        walkedTypePath: WalkedTypePath,
        predefinedDt: Option[DataTypeWithClass] = None
    ): Expression = cleanUpReflectionObjects {
        baseType(tpe) match {

            //<editor-fold desc="Description">
            case t if (
                try {
                    !dataTypeFor(t).isInstanceOf[ObjectType]
                } catch {
                    case _: Throwable => false
                }) && !predefinedDt.exists(_.isInstanceOf[ComplexWrapper]) => {
                path
            }

            case t if isSubtype(t, localTypeOf[java.lang.Integer]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Integer])
            }
            case t if isSubtype(t, localTypeOf[Int]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Integer])
            }
            case t if isSubtype(t, localTypeOf[java.lang.Long]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Long])
            }
            case t if isSubtype(t, localTypeOf[Long]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Long])
            }
            case t if isSubtype(t, localTypeOf[java.lang.Double]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Double])
            }
            case t if isSubtype(t, localTypeOf[Double]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Double])
            }
            case t if isSubtype(t, localTypeOf[java.lang.Float]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Float])
            }
            case t if isSubtype(t, localTypeOf[Float]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Float])
            }
            case t if isSubtype(t, localTypeOf[java.lang.Short]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Short])
            }
            case t if isSubtype(t, localTypeOf[Short]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Short])
            }
            case t if isSubtype(t, localTypeOf[java.lang.Byte]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Byte])
            }
            case t if isSubtype(t, localTypeOf[Byte]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Byte])
            }
            case t if isSubtype(t, localTypeOf[java.lang.Boolean]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Boolean])
            }
            case t if isSubtype(t, localTypeOf[Boolean]) => {
                createDeserializerForTypesSupportValueOf(path, classOf[java.lang.Boolean])
            }
            case t if isSubtype(t, localTypeOf[java.time.LocalDate]) => {
                createDeserializerForLocalDate(path)
            }
            case t if isSubtype(t, localTypeOf[java.sql.Date]) => {
                createDeserializerForSqlDate(path)
            } //</editor-fold>

            case t if isSubtype(t, localTypeOf[java.time.Instant]) => {
                createDeserializerForInstant(path)
            }
            case t if isSubtype(t, localTypeOf[java.lang.Enum[_]]) => {
                createDeserializerForTypesSupportValueOf(
                    Invoke(path, "toString", ObjectType(classOf[String]), returnNullable = false),
                    getClassFromType(t),
                )
            }
            case t if isSubtype(t, localTypeOf[java.sql.Timestamp]) => {
                createDeserializerForSqlTimestamp(path)
            }
            case t if isSubtype(t, localTypeOf[java.time.LocalDateTime]) => {
                //#if sparkMinor >= 3.2
                createDeserializerForLocalDateTime(path)
                //#else
                //$throw new IllegalArgumentException("TimestampNTZType is supported in spark 3.2+")
                //#endif
            }
            case t if isSubtype(t, localTypeOf[java.time.Duration]) => {
                //#if sparkMinor >= 3.2
                createDeserializerForDuration(path)
                //#else
                //$throw new IllegalArgumentException("java.time.Duration is supported in spark 3.2+")
                //#endif
            }
            case t if isSubtype(t, localTypeOf[java.time.Period]) => {
                //#if sparkMinor >= 3.2
                createDeserializerForPeriod(path)
                //#else
                //$throw new IllegalArgumentException("java.time.Period is supported in spark 3.2+")
                //#endif
            }
            case t if isSubtype(t, localTypeOf[java.lang.String]) => {
                createDeserializerForString(path, returnNullable = false)
            }
            case t if isSubtype(t, localTypeOf[java.math.BigDecimal]) => {
                createDeserializerForJavaBigDecimal(path, returnNullable = false)
            }
            case t if isSubtype(t, localTypeOf[BigDecimal]) => {
                createDeserializerForScalaBigDecimal(path, returnNullable = false)
            }
            case t if isSubtype(t, localTypeOf[java.math.BigInteger]) => {
                createDeserializerForJavaBigInteger(path, returnNullable = false)
            }
            case t if isSubtype(t, localTypeOf[scala.math.BigInt]) => {
                createDeserializerForScalaBigInt(path)
            }

            case t if isSubtype(t, localTypeOf[Array[_]]) => {
                var TypeRef(_, _, Seq(elementType)) = t
                if (predefinedDt.isDefined && !elementType.dealias.typeSymbol.isClass)
                    elementType = getType(predefinedDt.get.asInstanceOf[KComplexTypeWrapper].dt.asInstanceOf[ArrayType]
                        .elementType.asInstanceOf[DataTypeWithClass].cls
                    )
                val Schema(dataType, elementNullable) = predefinedDt.map { it =>
                    val elementInfo = it.asInstanceOf[KComplexTypeWrapper].dt.asInstanceOf[ArrayType].elementType
                        .asInstanceOf[DataTypeWithClass]
                    Schema(elementInfo.dt, elementInfo.nullable)
                }.getOrElse(schemaFor(elementType))
                val className = getClassNameFromType(elementType)
                val newTypePath = walkedTypePath.recordArray(className)

                val mapFunction: Expression => Expression = element => {
                    // upcast the array element to the data type the encoder expected.
                    deserializerForWithNullSafetyAndUpcast(
                        element,
                        dataType,
                        nullable = elementNullable,
                        newTypePath,
                        (casted, typePath) => deserializerFor(
                            tpe = elementType,
                            path = casted,
                            walkedTypePath = typePath,
                            predefinedDt = predefinedDt
                                .map(_.asInstanceOf[KComplexTypeWrapper].dt.asInstanceOf[ArrayType].elementType)
                                .filter(_.isInstanceOf[ComplexWrapper])
                                .map(_.asInstanceOf[ComplexWrapper])
                        )
                    )
                }

                val arrayData = UnresolvedMapObjects(mapFunction, path)
                val arrayCls = arrayClassFor(elementType)

                val methodName = elementType match {
                    case t if isSubtype(t, definitions.IntTpe) => "toIntArray"
                    case t if isSubtype(t, definitions.LongTpe) => "toLongArray"
                    case t if isSubtype(t, definitions.DoubleTpe) => "toDoubleArray"
                    case t if isSubtype(t, definitions.FloatTpe) => "toFloatArray"
                    case t if isSubtype(t, definitions.ShortTpe) => "toShortArray"
                    case t if isSubtype(t, definitions.ByteTpe) => "toByteArray"
                    case t if isSubtype(t, definitions.BooleanTpe) => "toBooleanArray"
                    // non-primitive
                    case _ => "array"
                }
                Invoke(arrayData, methodName, arrayCls, returnNullable = false)
            }

            // We serialize a `Set` to Catalyst array. When we deserialize a Catalyst array
            // to a `Set`, if there are duplicated elements, the elements will be de-duplicated.

            case t if isSubtype(t, localTypeOf[Map[_, _]]) => {
                val TypeRef(_, _, Seq(keyType, valueType)) = t

                val classNameForKey = getClassNameFromType(keyType)
                val classNameForValue = getClassNameFromType(valueType)

                val newTypePath = walkedTypePath.recordMap(classNameForKey, classNameForValue)

                UnresolvedCatalystToExternalMap(
                    path,
                    p => deserializerFor(keyType, p, newTypePath),
                    p => deserializerFor(valueType, p, newTypePath),
                    mirror.runtimeClass(t.typeSymbol.asClass)
                )
            }

            case t if isSubtype(t, localTypeOf[java.lang.Enum[_]]) => {
                createDeserializerForTypesSupportValueOf(
                    createDeserializerForString(path, returnNullable = false),
                    Class.forName(t.toString),
                )
            }
            case t if t.typeSymbol.annotations.exists(_.tree.tpe =:= typeOf[SQLUserDefinedType]) => {
                val udt = getClassFromType(t).getAnnotation(classOf[SQLUserDefinedType]).udt().
                    getConstructor().newInstance()
                val obj = NewInstance(
                    udt.userClass.getAnnotation(classOf[SQLUserDefinedType]).udt(),
                    Nil,
                    dataType = ObjectType(udt.userClass.getAnnotation(classOf[SQLUserDefinedType]).udt())
                )
                Invoke(obj, "deserialize", ObjectType(udt.userClass), path :: Nil)
            }

            case t if UDTRegistration.exists(getClassNameFromType(t)) => {
                val udt = UDTRegistration.getUDTFor(getClassNameFromType(t)).get.getConstructor().
                    newInstance().asInstanceOf[UserDefinedType[_]]
                val obj = NewInstance(
                    udt.getClass,
                    Nil,
                    dataType = ObjectType(udt.getClass)
                )
                Invoke(obj, "deserialize", ObjectType(udt.userClass), path :: Nil)
            }

            case _ if predefinedDt.isDefined => {
                predefinedDt.get match {

                    case wrapper: KDataTypeWrapper => {
                        val structType = wrapper.dt
                        val cls = wrapper.cls
                        val arguments = structType
                            .fields
                            .map { field =>
                                val dataType = field.dataType.asInstanceOf[DataTypeWithClass]
                                val nullable = dataType.nullable
                                val clsName = getClassNameFromType(getType(dataType.cls))
                                val newTypePath = walkedTypePath.recordField(clsName, field.name)

                                // For tuples, we based grab the inner fields by ordinal instead of name.
                                val newPath = deserializerFor(
                                    tpe = getType(dataType.cls),
                                    path = addToPath(path, field.name, dataType.dt, newTypePath),
                                    walkedTypePath = newTypePath,
                                    predefinedDt = Some(dataType).filter(_.isInstanceOf[ComplexWrapper])
                                )
                                expressionWithNullSafety(
                                    newPath,
                                    nullable = nullable,
                                    newTypePath
                                )
                            }
                        val newInstance = NewInstance(cls, arguments, ObjectType(cls), propagateNull = false)

                        org.apache.spark.sql.catalyst.expressions.If(
                            IsNull(path),
                            org.apache.spark.sql.catalyst.expressions.Literal.create(null, ObjectType(cls)),
                            newInstance
                        )
                    }

                    case t: ComplexWrapper => {

                        t.dt match {
                            case MapType(kt, vt, _) => {
                                val Seq(keyType, valueType) = Seq(kt, vt).map(_.asInstanceOf[DataTypeWithClass].cls)
                                    .map(getType(_))
                                val Seq(keyDT, valueDT) = Seq(kt, vt).map(_.asInstanceOf[DataTypeWithClass])
                                val classNameForKey = getClassNameFromType(keyType)
                                val classNameForValue = getClassNameFromType(valueType)

                                val newTypePath = walkedTypePath.recordMap(classNameForKey, classNameForValue)

                                val keyData =
                                    Invoke(
                                        UnresolvedMapObjects(
                                            p => deserializerFor(
                                                keyType, p, newTypePath, Some(keyDT)
                                                    .filter(_.isInstanceOf[ComplexWrapper])
                                            ),
                                            MapKeys(path)
                                        ),
                                        "array",
                                        ObjectType(classOf[Array[Any]])
                                    )

                                val valueData =
                                    Invoke(
                                        UnresolvedMapObjects(
                                            p => deserializerFor(
                                                valueType, p, newTypePath, Some(valueDT)
                                                    .filter(_.isInstanceOf[ComplexWrapper])
                                            ),
                                            MapValues(path)
                                        ),
                                        "array",
                                        ObjectType(classOf[Array[Any]])
                                    )

                                StaticInvoke(
                                    ArrayBasedMapData.getClass,
                                    ObjectType(classOf[java.util.Map[_, _]]),
                                    "toJavaMap",
                                    keyData :: valueData :: Nil,
                                    returnNullable = false
                                )
                            }

                            case ArrayType(elementType, containsNull) => {
                                val dataTypeWithClass = elementType.asInstanceOf[DataTypeWithClass]
                                val mapFunction: Expression => Expression = element => {
                                    // upcast the array element to the data type the encoder expected.
                                    val et = getType(dataTypeWithClass.cls)
                                    val className = getClassNameFromType(et)
                                    val newTypePath = walkedTypePath.recordArray(className)
                                    deserializerForWithNullSafetyAndUpcast(
                                        element,
                                        dataTypeWithClass.dt,
                                        nullable = dataTypeWithClass.nullable,
                                        newTypePath,
                                        (casted, typePath) => {
                                            deserializerFor(
                                                et, casted, typePath, Some(dataTypeWithClass)
                                                    .filter(_.isInstanceOf[ComplexWrapper])
                                                    .map(_.asInstanceOf[ComplexWrapper])
                                            )
                                        }
                                    )
                                }

                                UnresolvedMapObjects(mapFunction, path, customCollectionCls = Some(t.cls))
                            }

                            case StructType(elementType: Array[StructField]) => {
                                val cls = t.cls

                                val arguments = elementType.map { field =>
                                    val dataType = field.dataType.asInstanceOf[DataTypeWithClass]
                                    val nullable = dataType.nullable
                                    val clsName = getClassNameFromType(getType(dataType.cls))
                                    val newTypePath = walkedTypePath.recordField(clsName, field.name)

                                    // For tuples, we based grab the inner fields by ordinal instead of name.
                                    val newPath = deserializerFor(
                                        getType(dataType.cls),
                                        addToPath(path, field.name, dataType.dt, newTypePath),
                                        newTypePath,
                                        Some(dataType).filter(_.isInstanceOf[ComplexWrapper])
                                    )
                                    expressionWithNullSafety(
                                        newPath,
                                        nullable = nullable,
                                        newTypePath
                                    )
                                }
                                val newInstance = NewInstance(cls, arguments, ObjectType(cls), propagateNull = false)

                                org.apache.spark.sql.catalyst.expressions.If(
                                    IsNull(path),
                                    org.apache.spark.sql.catalyst.expressions.Literal.create(null, ObjectType(cls)),
                                    newInstance
                                )
                            }

                            case _ => {
                                throw new UnsupportedOperationException(
                                    s"No Encoder found for $tpe\n" + walkedTypePath
                                )
                            }
                        }
                    }
                }
            }

            case t if definedByConstructorParams(t) => {
                val params = getConstructorParameters(t)

                val cls = getClassFromType(tpe)

                val arguments = params.zipWithIndex.map { case ((fieldName, fieldType), i) =>
                    val Schema(dataType, nullable) = schemaFor(fieldType)
                    val clsName = getClassNameFromType(fieldType)
                    val newTypePath = walkedTypePath.recordField(clsName, fieldName)

                    // For tuples, we based grab the inner fields by ordinal instead of name.
                    val newPath = if (cls.getName startsWith "scala.Tuple") {
                        deserializerFor(
                            fieldType,
                            addToPathOrdinal(path, i, dataType, newTypePath),
                            newTypePath
                        )
                    } else {
                        deserializerFor(
                            fieldType,
                            addToPath(path, fieldName, dataType, newTypePath),
                            newTypePath
                        )
                    }
                    expressionWithNullSafety(
                        newPath,
                        nullable = nullable,
                        newTypePath
                    )
                }

                val newInstance = NewInstance(cls, arguments, ObjectType(cls), propagateNull = false)

                org.apache.spark.sql.catalyst.expressions.If(
                    IsNull(path),
                    org.apache.spark.sql.catalyst.expressions.Literal.create(null, ObjectType(cls)),
                    newInstance
                )
            }

            case _ => {
                throw new UnsupportedOperationException(
                    s"No Encoder found for $tpe\n" + walkedTypePath
                )
            }
        }
    }

    /**
     * Returns an expression for serializing an object of type T to Spark SQL representation. The
     * input object is located at ordinal 0 of a row, i.e., `BoundReference(0, _)`.
     *
     * If the given type is not supported, i.e. there is no encoder can be built for this type,
     * an [[UnsupportedOperationException]] will be thrown with detailed error message to explain
     * the type path walked so far and which class we are not supporting.
     * There are 4 kinds of type path:
     * * the root type: `root class: "abc.xyz.MyClass"`
     * * the value type of [[Option]]: `option value class: "abc.xyz.MyClass"`
     * * the element type of [[Array]] or [[Seq]]: `array element class: "abc.xyz.MyClass"`
     * * the field of [[Product]]: `field (class: "abc.xyz.MyClass", name: "myField")`
     */
    def serializerForType(tpe: `Type`): Expression = ScalaReflection.cleanUpReflectionObjects {
        val clsName = getClassNameFromType(tpe)
        val walkedTypePath = WalkedTypePath().recordRoot(clsName)

        // The input object to `ExpressionEncoder` is located at first column of an row.
        val isPrimitive = tpe.typeSymbol.asClass.isPrimitive
        val inputObject = BoundReference(0, dataTypeFor(tpe), nullable = !isPrimitive)

        serializerFor(inputObject, tpe, walkedTypePath)
    }

    def getType[T](clazz: Class[T]): universe.Type = {
        clazz match {
            case _ if clazz == classOf[Array[Byte]] => localTypeOf[Array[Byte]]
            case _ => {
                val mir = runtimeMirror(clazz.getClassLoader)
                mir.classSymbol(clazz).toType
            }
        }

    }

    def deserializerFor(cls: java.lang.Class[_], dt: DataTypeWithClass): Expression = {
        val tpe = getType(cls)
        val clsName = getClassNameFromType(tpe)
        val walkedTypePath = WalkedTypePath().recordRoot(clsName)

        // Assumes we are deserializing the first column of a row.
        deserializerForWithNullSafetyAndUpcast(
            GetColumnByOrdinal(0, dt.dt),
            dt.dt,
            nullable = dt.nullable,
            walkedTypePath,
            (casted, typePath) => deserializerFor(tpe, casted, typePath, Some(dt))
        )
    }


    def serializerFor(cls: java.lang.Class[_], dt: DataTypeWithClass): Expression = {
        val tpe = getType(cls)
        val clsName = getClassNameFromType(tpe)
        val walkedTypePath = WalkedTypePath().recordRoot(clsName)
        val inputObject = BoundReference(0, ObjectType(cls), nullable = true)
        serializerFor(inputObject, tpe, walkedTypePath, predefinedDt = Some(dt))
    }

    /**
     * Returns an expression for serializing the value of an input expression into Spark SQL
     * internal representation.
     */
    private def serializerFor(
        inputObject: Expression,
        tpe: `Type`,
        walkedTypePath: WalkedTypePath,
        seenTypeSet: Set[`Type`] = Set.empty,
        predefinedDt: Option[DataTypeWithClass] = None,
    ): Expression = cleanUpReflectionObjects {

        def toCatalystArray(
            input: Expression,
            elementType: `Type`,
            predefinedDt: Option[DataTypeWithClass] = None,
        ): Expression = {
            val dataType = predefinedDt
                .map(_.dt)
                .getOrElse {
                    dataTypeFor(elementType)
                }

            dataType match {

                case dt @ (MapType(_, _, _) | ArrayType(_, _) | StructType(_)) => {
                    val clsName = getClassNameFromType(elementType)
                    val newPath = walkedTypePath.recordArray(clsName)
                    createSerializerForMapObjects(
                        input, ObjectType(predefinedDt.get.cls),
                        serializerFor(_, elementType, newPath, seenTypeSet, predefinedDt)
                    )
                }

                case dt: ObjectType => {
                    val clsName = getClassNameFromType(elementType)
                    val newPath = walkedTypePath.recordArray(clsName)
                    createSerializerForMapObjects(
                        input, dt,
                        serializerFor(_, elementType, newPath, seenTypeSet)
                    )
                }

                //                case dt: ByteType =>
                //                    createSerializerForPrimitiveArray(input, dt)

                case dt @ (BooleanType | ByteType | ShortType | IntegerType | LongType | FloatType | DoubleType) => {
                    val cls = input.dataType.asInstanceOf[ObjectType].cls
                    if (cls.isArray && cls.getComponentType.isPrimitive) {
                        createSerializerForPrimitiveArray(input, dt)
                    } else {
                        createSerializerForGenericArray(
                            inputObject = input,
                            dataType = dt,
                            nullable = predefinedDt
                                .map(_.nullable)
                                .getOrElse(
                                    schemaFor(elementType).nullable
                                ),
                        )
                    }
                }

                case _: StringType => {
                    val clsName = getClassNameFromType(typeOf[String])
                    val newPath = walkedTypePath.recordArray(clsName)
                    createSerializerForMapObjects(
                        input, ObjectType(Class.forName(getClassNameFromType(elementType))),
                        serializerFor(_, elementType, newPath, seenTypeSet)
                    )
                }

                case dt => {
                    createSerializerForGenericArray(
                        inputObject = input,
                        dataType = dt,
                        nullable = predefinedDt
                            .map(_.nullable)
                            .getOrElse {
                                schemaFor(elementType).nullable
                            },
                    )
                }
            }
        }

        baseType(tpe) match {

            //<editor-fold desc="scala-like">
            case _ if !inputObject.dataType.isInstanceOf[ObjectType] &&
                !predefinedDt.exists(_.isInstanceOf[ComplexWrapper]) => {
                inputObject
            }
            case t if isSubtype(t, localTypeOf[Option[_]]) => {
                val TypeRef(_, _, Seq(optType)) = t
                val className = getClassNameFromType(optType)
                val newPath = walkedTypePath.recordOption(className)
                val unwrapped = UnwrapOption(dataTypeFor(optType), inputObject)
                serializerFor(unwrapped, optType, newPath, seenTypeSet)
            }

            // Since List[_] also belongs to localTypeOf[Product], we put this case before
            // "case t if definedByConstructorParams(t)" to make sure it will match to the
            // case "localTypeOf[Seq[_]]"
            case t if isSubtype(t, localTypeOf[Seq[_]]) => {
                val TypeRef(_, _, Seq(elementType)) = t
                toCatalystArray(inputObject, elementType)
            }

            case t if isSubtype(t, localTypeOf[Array[_]]) && predefinedDt.isEmpty => {
                val TypeRef(_, _, Seq(elementType)) = t
                toCatalystArray(inputObject, elementType)
            }

            case t if isSubtype(t, localTypeOf[Map[_, _]]) => {
                val TypeRef(_, _, Seq(keyType, valueType)) = t
                val keyClsName = getClassNameFromType(keyType)
                val valueClsName = getClassNameFromType(valueType)
                val keyPath = walkedTypePath.recordKeyForMap(keyClsName)
                val valuePath = walkedTypePath.recordValueForMap(valueClsName)

                createSerializerForMap(
                    inputObject,
                    MapElementInformation(
                        dataTypeFor(keyType),
                        nullable = !keyType.typeSymbol.asClass.isPrimitive,
                        serializerFor(_, keyType, keyPath, seenTypeSet)
                    ),
                    MapElementInformation(
                        dataTypeFor(valueType),
                        nullable = !valueType.typeSymbol.asClass.isPrimitive,
                        serializerFor(_, valueType, valuePath, seenTypeSet)
                    )
                )
            }

            case t if isSubtype(t, localTypeOf[scala.collection.Set[_]]) => {
                val TypeRef(_, _, Seq(elementType)) = t

                // There's no corresponding Catalyst type for `Set`, we serialize a `Set` to Catalyst array.
                // Note that the property of `Set` is only kept when manipulating the data as domain object.
                val newInput =
                Invoke(
                    inputObject,
                    "toSeq",
                    ObjectType(classOf[Seq[_]])
                )

                toCatalystArray(newInput, elementType)
            }

            case t if isSubtype(t, localTypeOf[String]) => {
                createSerializerForString(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.time.Instant]) => {
                createSerializerForJavaInstant(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.sql.Timestamp]) => {
                createSerializerForSqlTimestamp(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.time.LocalDateTime]) => {
                //#if sparkMinor >= 3.2
                createSerializerForLocalDateTime(inputObject)
                //#else
                //$throw new IllegalArgumentException("TimestampNTZType is supported in spark 3.2+")
                //#endif
            }
            case t if isSubtype(t, localTypeOf[java.time.LocalDate]) => {
                createSerializerForJavaLocalDate(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.sql.Date]) => {
                createSerializerForSqlDate(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.time.Duration]) => {
                //#if sparkMinor >= 3.2
                createSerializerForJavaDuration(inputObject)
                //#else
                //$throw new IllegalArgumentException("java.time.Duration is supported in spark 3.2+")
                //#endif
            }
            case t if isSubtype(t, localTypeOf[java.time.Period]) => {
                //#if sparkMinor >= 3.2
                createSerializerForJavaPeriod(inputObject)
                //#else
                //$throw new IllegalArgumentException("java.time.Period is supported in spark 3.2+")
                //#endif
            }
            case t if isSubtype(t, localTypeOf[BigDecimal]) => {
                createSerializerForScalaBigDecimal(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.math.BigDecimal]) => {
                createSerializerForJavaBigDecimal(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.math.BigInteger]) => {
                createSerializerForJavaBigInteger(inputObject)
            }
            case t if isSubtype(t, localTypeOf[scala.math.BigInt]) => {
                createSerializerForScalaBigInt(inputObject)
            }

            case t if isSubtype(t, localTypeOf[java.lang.Integer]) => {
                createSerializerForInteger(inputObject)
            }
            case t if isSubtype(t, localTypeOf[Int]) => {
                createSerializerForInteger(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.lang.Long]) => {
                createSerializerForLong(inputObject)
            }
            case t if isSubtype(t, localTypeOf[Long]) => {
                createSerializerForLong(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.lang.Double]) => {
                createSerializerForDouble(inputObject)
            }
            case t if isSubtype(t, localTypeOf[Double]) => {
                createSerializerForDouble(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.lang.Float]) => {
                createSerializerForFloat(inputObject)
            }
            case t if isSubtype(t, localTypeOf[Float]) => {
                createSerializerForFloat(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.lang.Short]) => {
                createSerializerForShort(inputObject)
            }
            case t if isSubtype(t, localTypeOf[Short]) => {
                createSerializerForShort(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.lang.Byte]) => {
                createSerializerForByte(inputObject)
            }
            case t if isSubtype(t, localTypeOf[Byte]) => {
                createSerializerForByte(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.lang.Boolean]) => {
                createSerializerForBoolean(inputObject)
            }
            case t if isSubtype(t, localTypeOf[Boolean]) => {
                createSerializerForBoolean(inputObject)
            }
            case t if isSubtype(t, localTypeOf[java.lang.Enum[_]]) => {
                createSerializerForString(
                    Invoke(inputObject, "name", ObjectType(classOf[String]), returnNullable = false)
                )
            }
            case t if t.typeSymbol.annotations.exists(_.tree.tpe =:= typeOf[SQLUserDefinedType]) => {
                val udt = getClassFromType(t)
                    .getAnnotation(classOf[SQLUserDefinedType]).udt().getConstructor().newInstance()
                val udtClass = udt.userClass.getAnnotation(classOf[SQLUserDefinedType]).udt()
                createSerializerForUserDefinedType(inputObject, udt, udtClass)
            }

            case t if UDTRegistration.exists(getClassNameFromType(t)) => {
                val udt = UDTRegistration.getUDTFor(getClassNameFromType(t)).get.getConstructor().
                    newInstance().asInstanceOf[UserDefinedType[_]]
                val udtClass = udt.getClass
                createSerializerForUserDefinedType(inputObject, udt, udtClass)
            }
            //</editor-fold>

            // Kotlin specific cases
            case t if predefinedDt.isDefined => {

//                if (seenTypeSet.contains(t)) {
//                    throw new UnsupportedOperationException(
//                        s"cannot have circular references in class, but got the circular reference of class $t"
//                    )
//                }

                predefinedDt.get match {

                    // Kotlin data class
                    case dataType: KDataTypeWrapper => {
                        val cls = dataType.cls
                        val properties = getJavaBeanReadableProperties(cls)
                        val structFields = dataType.dt.fields.map(_.asInstanceOf[KStructField])
                        val fields: Array[(String, Expression)] = structFields.map { structField =>
                            val maybeProp = properties.find(it => it.getReadMethod.getName == structField.getterName)
                            if (maybeProp.isEmpty) throw new IllegalArgumentException(s"Field ${
                                structField.name
                            } is not found among available props, which are: ${properties.map(_.getName).mkString(", ")}"
                            )
                            val fieldName = structField.name
                            val propClass = structField.dataType.asInstanceOf[DataTypeWithClass].cls
                            val propDt = structField.dataType.asInstanceOf[DataTypeWithClass]

                            val fieldValue = Invoke(
                                inputObject,
                                maybeProp.get.getReadMethod.getName,
                                inferExternalType(propClass),
                                returnNullable = structField.nullable
                            )
                            val newPath = walkedTypePath.recordField(propClass.getName, fieldName)

                            val tpe = getType(propClass)

                            val serializer = serializerFor(
                                inputObject = fieldValue,
                                tpe = tpe,
                                walkedTypePath = newPath,
                                seenTypeSet = seenTypeSet,
                                predefinedDt = if (propDt.isInstanceOf[ComplexWrapper]) Some(propDt) else None
                            )

                            (fieldName, serializer)
                        }
                        createSerializerForObject(inputObject, fields)
                    }

                    case otherTypeWrapper: ComplexWrapper => {

                        otherTypeWrapper.dt match {

                            case MapType(kt, vt, _) => {
                                val Seq(keyType, valueType) = Seq(kt, vt).map(_.asInstanceOf[DataTypeWithClass].cls)
                                    .map(getType(_))
                                val Seq(keyDT, valueDT) = Seq(kt, vt).map(_.asInstanceOf[DataTypeWithClass])
                                val keyClsName = getClassNameFromType(keyType)
                                val valueClsName = getClassNameFromType(valueType)
                                val keyPath = walkedTypePath.recordKeyForMap(keyClsName)
                                val valuePath = walkedTypePath.recordValueForMap(valueClsName)

                                createSerializerForMap(
                                    inputObject,
                                    MapElementInformation(
                                        dataTypeFor(keyType),
                                        nullable = !keyType.typeSymbol.asClass.isPrimitive,
                                        serializerFor(
                                            _, keyType, keyPath, seenTypeSet, Some(keyDT)
                                                .filter(_.isInstanceOf[ComplexWrapper])
                                        )
                                    ),
                                    MapElementInformation(
                                        dataTypeFor(valueType),
                                        nullable = !valueType.typeSymbol.asClass.isPrimitive,
                                        serializerFor(
                                            _, valueType, valuePath, seenTypeSet, Some(valueDT)
                                                .filter(_.isInstanceOf[ComplexWrapper])
                                        )
                                    )
                                )
                            }

                            case ArrayType(elementType, _) => {
                                toCatalystArray(
                                    inputObject,
                                    getType(elementType.asInstanceOf[DataTypeWithClass].cls
                                    ), Some(elementType.asInstanceOf[DataTypeWithClass])
                                )
                            }

                            case StructType(elementType: Array[StructField]) => {
                                val cls = otherTypeWrapper.cls
                                val names = elementType.map(_.name)

                                val beanInfo = Introspector.getBeanInfo(cls)
                                val methods = beanInfo.getMethodDescriptors.filter(it => names.contains(it.getName))


                                val fields = elementType.map { structField =>

                                    val maybeProp = methods.find(it => it.getName == structField.name)
                                    if (maybeProp.isEmpty) throw new IllegalArgumentException(s"Field ${
                                        structField.name
                                    } is not found among available props, which are: ${
                                        methods.map(_.getName).mkString(", ")
                                    }"
                                    )
                                    val fieldName = structField.name
                                    val propClass = structField.dataType.asInstanceOf[DataTypeWithClass].cls
                                    val propDt = structField.dataType.asInstanceOf[DataTypeWithClass]
                                    val fieldValue = Invoke(
                                        inputObject,
                                        maybeProp.get.getName,
                                        inferExternalType(propClass),
                                        returnNullable = propDt.nullable
                                    )
                                    val newPath = walkedTypePath.recordField(propClass.getName, fieldName)
                                    (fieldName, serializerFor(
                                        fieldValue, getType(propClass), newPath, seenTypeSet, if (propDt
                                            .isInstanceOf[ComplexWrapper]) Some(propDt) else None
                                    ))

                                }
                                createSerializerForObject(inputObject, fields)
                            }

                            case _ => {
                                throw new UnsupportedOperationException(
                                    s"No Encoder found for $tpe\n" + walkedTypePath
                                )
                            }
                        }
                    }
                }
            }

            case t if definedByConstructorParams(t) => {
                if (seenTypeSet.contains(t)) {
                    throw new UnsupportedOperationException(
                        s"cannot have circular references in class, but got the circular reference of class $t"
                    )
                }

                val params = getConstructorParameters(t)
                val fields = params.map { case (fieldName, fieldType) =>
                    if (javaKeywords.contains(fieldName)) {
                        throw new UnsupportedOperationException(s"`$fieldName` is a reserved keyword and " +
                            "cannot be used as field name\n" + walkedTypePath
                        )
                    }

                    // SPARK-26730 inputObject won't be null with If's guard below. And KnownNotNul
                    // is necessary here. Because for a nullable nested inputObject with struct data
                    // type, e.g. StructType(IntegerType, StringType), it will return nullable=true
                    // for IntegerType without KnownNotNull. And that's what we do not expect to.
                    val fieldValue = Invoke(
                        KnownNotNull(inputObject), fieldName, dataTypeFor(fieldType),
                        returnNullable = !fieldType.typeSymbol.asClass.isPrimitive
                    )
                    val clsName = getClassNameFromType(fieldType)
                    val newPath = walkedTypePath.recordField(clsName, fieldName)
                    (fieldName, serializerFor(fieldValue, fieldType, newPath, seenTypeSet + t))
                }
                createSerializerForObject(inputObject, fields)
            }

            case _ => {
                throw new UnsupportedOperationException(
                    s"No Encoder found for $tpe\n" + walkedTypePath
                )
            }
        }
    }

    def createDeserializerForString(path: Expression, returnNullable: Boolean): Expression = {
        Invoke(
            path, "toString", ObjectType(classOf[java.lang.String]),
            returnNullable = returnNullable
        )
    }

    def getJavaBeanReadableProperties(beanClass: Class[_]): Array[PropertyDescriptor] = {
        val beanInfo = Introspector.getBeanInfo(beanClass)
        beanInfo.getPropertyDescriptors.filterNot(_.getName == "class")
            .filterNot(_.getName == "declaringClass")
            .filter(_.getReadMethod != null)
    }

    /*
     * Retrieves the runtime class corresponding to the provided type.
     */
    def getClassFromType(tpe: Type): Class[_] = mirror.runtimeClass(tpe.dealias.typeSymbol.asClass)

    case class Schema(dataType: DataType, nullable: Boolean)

    /** Returns a catalyst DataType and its nullability for the given Scala Type using reflection. */
    def schemaFor(tpe: `Type`): Schema = cleanUpReflectionObjects {

        baseType(tpe) match {
            // this must be the first case, since all objects in scala are instances of Null, therefore
            // Null type would wrongly match the first of them, which is Option as of now
            case t if isSubtype(t, definitions.NullTpe) => Schema(NullType, nullable = true)

            case t if t.typeSymbol.annotations.exists(_.tree.tpe =:= typeOf[SQLUserDefinedType]) => {
                val udt = getClassFromType(t).getAnnotation(classOf[SQLUserDefinedType]).udt().
                    getConstructor().newInstance()
                Schema(udt, nullable = true)
            }
            case t if UDTRegistration.exists(getClassNameFromType(t)) => {
                val udt = UDTRegistration
                    .getUDTFor(getClassNameFromType(t))
                    .get
                    .getConstructor()
                    .newInstance()
                    .asInstanceOf[UserDefinedType[_]]
                Schema(udt, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[Option[_]]) => {
                val TypeRef(_, _, Seq(optType)) = t
                Schema(schemaFor(optType).dataType, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[Array[Byte]]) => {
                Schema(BinaryType, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[Array[_]]) => {
                val TypeRef(_, _, Seq(elementType)) = t
                val Schema(dataType, nullable) = schemaFor(elementType)
                Schema(ArrayType(dataType, containsNull = nullable), nullable = true)
            }
            case t if isSubtype(t, localTypeOf[Seq[_]]) => {
                val TypeRef(_, _, Seq(elementType)) = t
                val Schema(dataType, nullable) = schemaFor(elementType)
                Schema(ArrayType(dataType, containsNull = nullable), nullable = true)
            }
            case t if isSubtype(t, localTypeOf[Map[_, _]]) => {
                val TypeRef(_, _, Seq(keyType, valueType)) = t
                val Schema(valueDataType, valueNullable) = schemaFor(valueType)
                Schema(
                    MapType(
                        schemaFor(keyType).dataType,
                        valueDataType, valueContainsNull = valueNullable
                    ), nullable = true
                )
            }
            case t if isSubtype(t, localTypeOf[Set[_]]) => {
                val TypeRef(_, _, Seq(elementType)) = t
                val Schema(dataType, nullable) = schemaFor(elementType)
                Schema(ArrayType(dataType, containsNull = nullable), nullable = true)
            }
            case t if isSubtype(t, localTypeOf[String]) => {
                Schema(StringType, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[java.time.Instant]) => {
                Schema(TimestampType, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[java.sql.Timestamp]) => {
                Schema(TimestampType, nullable = true)
            }
            // SPARK-36227: Remove TimestampNTZ type support in Spark 3.2 with minimal code changes.
            case t if isSubtype(t, localTypeOf[java.time.LocalDateTime]) && Utils.isTesting => {
                //#if sparkMinor >= 3.2
                Schema(TimestampNTZType, nullable = true)
                //#else
                //$throw new IllegalArgumentException("java.time.LocalDateTime is supported in Spark 3.2+")
                //#endif
            }
            case t if isSubtype(t, localTypeOf[java.time.LocalDate]) => {
                Schema(DateType, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[java.sql.Date]) => {
                Schema(DateType, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[CalendarInterval]) => {
                Schema(CalendarIntervalType, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[java.time.Duration]) => {
                //#if sparkMinor >= 3.2
                Schema(DayTimeIntervalType(), nullable = true)
                //#else
                //$throw new IllegalArgumentException("DayTimeIntervalType for java.time.Duration is supported in Spark 3.2+")
                //#endif
            }
            case t if isSubtype(t, localTypeOf[java.time.Period]) => {
                //#if sparkMinor >= 3.2
                Schema(YearMonthIntervalType(), nullable = true)
                //#else
                //$throw new IllegalArgumentException("YearMonthIntervalType for java.time.Period is supported in Spark 3.2+")
                //#endif
            }
            case t if isSubtype(t, localTypeOf[BigDecimal]) => {
                Schema(DecimalType.SYSTEM_DEFAULT, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[java.math.BigDecimal]) => {
                Schema(DecimalType.SYSTEM_DEFAULT, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[java.math.BigInteger]) => {
                Schema(DecimalType.BigIntDecimal, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[scala.math.BigInt]) => {
                Schema(DecimalType.BigIntDecimal, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[Decimal]) => {
                Schema(DecimalType.SYSTEM_DEFAULT, nullable = true)
            }
            case t if isSubtype(t, localTypeOf[java.lang.Integer]) => Schema(IntegerType, nullable = true)
            case t if isSubtype(t, localTypeOf[java.lang.Long]) => Schema(LongType, nullable = true)
            case t if isSubtype(t, localTypeOf[java.lang.Double]) => Schema(DoubleType, nullable = true)
            case t if isSubtype(t, localTypeOf[java.lang.Float]) => Schema(FloatType, nullable = true)
            case t if isSubtype(t, localTypeOf[java.lang.Short]) => Schema(ShortType, nullable = true)
            case t if isSubtype(t, localTypeOf[java.lang.Byte]) => Schema(ByteType, nullable = true)
            case t if isSubtype(t, localTypeOf[java.lang.Boolean]) => Schema(BooleanType, nullable = true)
            case t if isSubtype(t, definitions.IntTpe) => Schema(IntegerType, nullable = false)
            case t if isSubtype(t, definitions.LongTpe) => Schema(LongType, nullable = false)
            case t if isSubtype(t, definitions.DoubleTpe) => Schema(DoubleType, nullable = false)
            case t if isSubtype(t, definitions.FloatTpe) => Schema(FloatType, nullable = false)
            case t if isSubtype(t, definitions.ShortTpe) => Schema(ShortType, nullable = false)
            case t if isSubtype(t, definitions.ByteTpe) => Schema(ByteType, nullable = false)
            case t if isSubtype(t, definitions.BooleanTpe) => Schema(BooleanType, nullable = false)
            case t if definedByConstructorParams(t) => {
                val params = getConstructorParameters(t)
                Schema(
                    StructType(
                        params.map { case (fieldName, fieldType) =>
                            val Schema(dataType, nullable) = schemaFor(fieldType)
                            StructField(fieldName, dataType, nullable)
                        }
                    ), nullable = true
                )
            }
            case other => {
                throw new UnsupportedOperationException(s"Schema for type $other is not supported")
            }
        }
    }

    /**
     * Finds an accessible constructor with compatible parameters. This is a more flexible search than
     * the exact matching algorithm in `Class.getConstructor`. The first assignment-compatible
     * matching constructor is returned if it exists. Otherwise, we check for additional compatible
     * constructors defined in the companion object as `apply` methods. Otherwise, it returns `None`.
     */
    def findConstructor[T](cls: Class[T], paramTypes: Seq[Class[_]]): Option[Seq[AnyRef] => T] = {
        Option(ConstructorUtils.getMatchingAccessibleConstructor(cls, paramTypes: _*)) match {
            case Some(c) => Some(x => c.newInstance(x: _*))
            case None =>
                val companion = mirror.staticClass(cls.getName).companion
                val moduleMirror = mirror.reflectModule(companion.asModule)
                val applyMethods = companion.asTerm.typeSignature
                    .member(universe.TermName("apply")).asTerm.alternatives
                applyMethods.find { method =>
                    val params = method.typeSignature.paramLists.head
                    // Check that the needed params are the same length and of matching types
                    params.size == paramTypes.tail.size &&
                        params.zip(paramTypes.tail).forall { case(ps, pc) =>
                            ps.typeSignature.typeSymbol == mirror.classSymbol(pc)
                        }
                }.map { applyMethodSymbol =>
                    val expectedArgsCount = applyMethodSymbol.typeSignature.paramLists.head.size
                    val instanceMirror = mirror.reflect(moduleMirror.instance)
                    val method = instanceMirror.reflectMethod(applyMethodSymbol.asMethod)
                    (_args: Seq[AnyRef]) => {
                        // Drop the "outer" argument if it is provided
                        val args = if (_args.size == expectedArgsCount) _args else _args.tail
                        method.apply(args: _*).asInstanceOf[T]
                    }
                }
        }
    }

    /**
     * Whether the fields of the given type is defined entirely by its constructor parameters.
     */
    def definedByConstructorParams(tpe: Type): Boolean = cleanUpReflectionObjects {
        tpe.dealias match {
            // `Option` is a `Product`, but we don't wanna treat `Option[Int]` as a struct type.
            case t if isSubtype(t, localTypeOf[Option[_]]) => definedByConstructorParams(t.typeArgs.head)
            case _ => {
                isSubtype(tpe.dealias, localTypeOf[Product]) ||
                    isSubtype(tpe.dealias, localTypeOf[DefinedByConstructorParams])
            }
        }
    }

    private val javaKeywords = Set(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch",
        "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "false",
        "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
        "interface", "long", "native", "new", "null", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw",
        "throws", "transient", "true", "try", "void", "volatile", "while"
    )


    @scala.annotation.tailrec
    def javaBoxedType(dt: DataType): Class[_] = dt match {
        case _: DecimalType => classOf[Decimal]
        case _: DayTimeIntervalType => classOf[java.lang.Long]
        case _: YearMonthIntervalType => classOf[java.lang.Integer]
        case BinaryType => classOf[Array[Byte]]
        case StringType => classOf[UTF8String]
        case CalendarIntervalType => classOf[CalendarInterval]
        case _: StructType => classOf[InternalRow]
        case _: ArrayType => classOf[ArrayType]
        case _: MapType => classOf[MapType]
        case udt: UserDefinedType[_] => javaBoxedType(udt.sqlType)
        case ObjectType(cls) => cls
        case _ => ScalaReflection.typeBoxedJavaMapping.getOrElse(dt, classOf[java.lang.Object])
    }

}

/**
 * Support for generating catalyst schemas for scala objects.  Note that unlike its companion
 * object, this trait able to work in both the runtime and the compile time (macro) universe.
 */
trait KotlinReflection extends Logging {
    /** The universe we work in (runtime or macro) */
    val universe: scala.reflect.api.Universe

    /** The mirror used to access types in the universe */
    def mirror: universe.Mirror

    import universe._

    // The Predef.Map is scala.collection.immutable.Map.
    // Since the map values can be mutable, we explicitly import scala.collection.Map at here.

    /**
     * Any codes calling `scala.reflect.api.Types.TypeApi.<:<` should be wrapped by this method to
     * clean up the Scala reflection garbage automatically. Otherwise, it will leak some objects to
     * `scala.reflect.runtime.JavaUniverse.undoLog`.
     *
     * @see https://github.com/scala/bug/issues/8302
     */
    def cleanUpReflectionObjects[T](func: => T): T = {
        universe.asInstanceOf[scala.reflect.runtime.JavaUniverse].undoLog.undo(func)
    }

    /**
     * Return the Scala Type for `T` in the current classloader mirror.
     *
     * Use this method instead of the convenience method `universe.typeOf`, which
     * assumes that all types can be found in the classloader that loaded scala-reflect classes.
     * That's not necessarily the case when running using Eclipse launchers or even
     * Sbt console or test (without `fork := true`).
     *
     * @see SPARK-5281
     */
    def localTypeOf[T: TypeTag]: `Type` = {
        val tag = implicitly[TypeTag[T]]
        tag.in(mirror).tpe.dealias
    }

    private def isValueClass(tpe: Type): Boolean = {
        tpe.typeSymbol.isClass && tpe.typeSymbol.asClass.isDerivedValueClass
    }

    /** Returns the name and type of the underlying parameter of value class `tpe`. */
    private def getUnderlyingTypeOfValueClass(tpe: `Type`): Type = {
        getConstructorParameters(tpe).head._2
    }

    /**
     * Returns the full class name for a type. The returned name is the canonical
     * Scala name, where each component is separated by a period. It is NOT the
     * Java-equivalent runtime name (no dollar signs).
     *
     * In simple cases, both the Scala and Java names are the same, however when Scala
     * generates constructs that do not map to a Java equivalent, such as singleton objects
     * or nested classes in package objects, it uses the dollar sign ($) to create
     * synthetic classes, emulating behaviour in Java bytecode.
     */
    def getClassNameFromType(tpe: `Type`): String = {
        tpe.dealias.erasure.typeSymbol.asClass.fullName
    }

    /**
     * Returns the parameter names and types for the primary constructor of this type.
     *
     * Note that it only works for scala classes with primary constructor, and currently doesn't
     * support inner class.
     */
    def getConstructorParameters(tpe: Type): Seq[(String, Type)] = {
        val dealiasedTpe = tpe.dealias
        val formalTypeArgs = dealiasedTpe.typeSymbol.asClass.typeParams
        val TypeRef(_, _, actualTypeArgs) = dealiasedTpe
        val params = constructParams(dealiasedTpe)
        params.map { p =>
            val paramTpe = p.typeSignature
            if (isValueClass(paramTpe)) {
                // Replace value class with underlying type
                p.name.decodedName.toString -> getUnderlyingTypeOfValueClass(paramTpe)
            } else {
                p.name.decodedName.toString -> paramTpe.substituteTypes(formalTypeArgs, actualTypeArgs)
            }
        }
    }

    /**
     * If our type is a Scala trait it may have a companion object that
     * only defines a constructor via `apply` method.
     */
    private def getCompanionConstructor(tpe: Type): Symbol = {
        def throwUnsupportedOperation = {
            throw new UnsupportedOperationException(s"Unable to find constructor for $tpe. " +
                s"This could happen if $tpe is an interface, or a trait without companion object " +
                "constructor."
            )
        }

        tpe.typeSymbol.asClass.companion match {
            case NoSymbol => throwUnsupportedOperation
            case sym => {
                sym.asTerm.typeSignature.member(universe.TermName("apply")) match {
                    case NoSymbol => throwUnsupportedOperation
                    case constructorSym => constructorSym
                }
            }
        }
    }

    protected def constructParams(tpe: Type): Seq[Symbol] = {
        val constructorSymbol = tpe.member(termNames.CONSTRUCTOR) match {
            case NoSymbol => getCompanionConstructor(tpe)
            case sym => sym
        }
        val params = if (constructorSymbol.isMethod) {
            constructorSymbol.asMethod.paramLists
        } else {
            // Find the primary constructor, and use its parameter ordering.
            val primaryConstructorSymbol: Option[Symbol] = constructorSymbol.asTerm.alternatives.find(
                s => s.isMethod && s.asMethod.isPrimaryConstructor
            )
            if (primaryConstructorSymbol.isEmpty) {
                sys.error("Internal SQL error: Product object did not have a primary constructor.")
            } else {
                primaryConstructorSymbol.get.asMethod.paramLists
            }
        }
        params.flatten
    }

}

