package org.apache.spark.sql.catalyst.encoders

import org.apache.spark.SparkException
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.catalyst.analysis.GetColumnByOrdinal
import org.apache.spark.sql.catalyst.expressions.objects._
import org.apache.spark.sql.catalyst.expressions.{BoundReference, CheckOverflow, CreateNamedStruct, Expression, GetStructField, If, IsNull, Literal}
import org.apache.spark.sql.catalyst.util.{ArrayBasedMapData, ArrayData, DateTimeUtils}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataTypeWithClass, Row}
import org.apache.spark.unsafe.types.UTF8String

import scala.annotation.tailrec
import scala.collection.Map
import scala.reflect.ClassTag

/**
 * A factory for constructing encoders that convert external row to/from the Spark SQL
 * internal binary representation.
 *
 * The following is a mapping between Spark SQL types and its allowed external types:
 * {{{
 *   BooleanType -> java.lang.Boolean
 *   ByteType -> java.lang.Byte
 *   ShortType -> java.lang.Short
 *   IntegerType -> java.lang.Integer
 *   FloatType -> java.lang.Float
 *   DoubleType -> java.lang.Double
 *   StringType -> String
 *   DecimalType -> java.math.BigDecimal or scala.math.BigDecimal or Decimal
 *
 *   DateType -> java.sql.Date
 *   TimestampType -> java.sql.Timestamp
 *
 *   BinaryType -> byte array
 *   ArrayType -> scala.collection.Seq or Array
 *   MapType -> scala.collection.Map
 *   StructType -> org.apache.spark.sql.Row
 * }}}
 */
object RowEncoder {
  def apply(schema: StructType): ExpressionEncoder[Row] = {
    val cls = classOf[Row]
    val inputObject = BoundReference(0, ObjectType(cls), nullable = true)
    val updatedSchema = schema.copy(
      fields = schema.fields.map(f => f.dataType match {
        case kstw: DataTypeWithClass => f.copy(dataType = kstw.dt, nullable = kstw.nullable)
        case _ => f
      })
    )
    val serializer = serializerFor(AssertNotNull(inputObject, Seq("top level row object")), updatedSchema)
    val deserializer = deserializerFor(updatedSchema)
    new ExpressionEncoder[Row](
      updatedSchema,
      flat = false,
      serializer.asInstanceOf[CreateNamedStruct].flatten,
      deserializer,
      ClassTag(cls))
  }

  private def serializerFor(
                             inputObject: Expression,
                             inputType: DataType): Expression = inputType match {
    case dt if ScalaReflection.isNativeType(dt) => inputObject

    case p: PythonUserDefinedType => serializerFor(inputObject, p.sqlType)

    case udt: UserDefinedType[_] =>
      val annotation = udt.userClass.getAnnotation(classOf[SQLUserDefinedType])
      val udtClass: Class[_] = if (annotation != null) {
        annotation.udt()
      } else {
        UDTRegistration.getUDTFor(udt.userClass.getName).getOrElse {
          throw new SparkException(s"${udt.userClass.getName} is not annotated with " +
            "SQLUserDefinedType nor registered with UDTRegistration.}")
        }
      }
      val obj = NewInstance(
        udtClass,
        Nil,
        dataType = ObjectType(udtClass), false)
      Invoke(obj, "serialize", udt, inputObject :: Nil, returnNullable = false)

    case TimestampType =>
      StaticInvoke(
        DateTimeUtils.getClass,
        TimestampType,
        "fromJavaTimestamp",
        inputObject :: Nil,
        returnNullable = false)

    case DateType =>
      StaticInvoke(
        DateTimeUtils.getClass,
        DateType,
        "fromJavaDate",
        inputObject :: Nil,
        returnNullable = false)

    case d: DecimalType =>
      CheckOverflow(StaticInvoke(
        Decimal.getClass,
        d,
        "fromDecimal",
        inputObject :: Nil,
        returnNullable = false), d)

    case StringType =>
      StaticInvoke(
        classOf[UTF8String],
        StringType,
        "fromString",
        inputObject :: Nil,
        returnNullable = false)

    case t@ArrayType(et, containsNull) =>
      et match {
        case BooleanType | ByteType | ShortType | IntegerType | LongType | FloatType | DoubleType =>
          StaticInvoke(
            classOf[ArrayData],
            t,
            "toArrayData",
            inputObject :: Nil,
            returnNullable = false)

        case _ => MapObjects(
          element => {
            val value = serializerFor(ValidateExternalType(element, et), et)
            if (!containsNull) {
              AssertNotNull(value, Seq.empty)
            } else {
              value
            }
          },
          inputObject,
          ObjectType(classOf[Object]))
      }

    case t@MapType(kt, vt, valueNullable) =>
      val keys =
        Invoke(
          Invoke(inputObject, "keysIterator", ObjectType(classOf[scala.collection.Iterator[_]]),
            returnNullable = false),
          "toSeq",
          ObjectType(classOf[scala.collection.Seq[_]]), returnNullable = false)
      val convertedKeys = serializerFor(keys, ArrayType(kt, false))

      val values =
        Invoke(
          Invoke(inputObject, "valuesIterator", ObjectType(classOf[scala.collection.Iterator[_]]),
            returnNullable = false),
          "toSeq",
          ObjectType(classOf[scala.collection.Seq[_]]), returnNullable = false)
      val convertedValues = serializerFor(values, ArrayType(vt, valueNullable))

      val nonNullOutput = NewInstance(
        classOf[ArrayBasedMapData],
        convertedKeys :: convertedValues :: Nil,
        dataType = t,
        propagateNull = false)

      if (inputObject.nullable) {
        If(IsNull(inputObject),
          Literal.create(null, inputType),
          nonNullOutput)
      } else {
        nonNullOutput
      }

    case StructType(fields) =>
      val nonNullOutput = CreateNamedStruct(fields.zipWithIndex.flatMap { case (field, index) =>
        val dataType = field.dataType
        val fieldValue = serializerFor(
          ValidateExternalType(
            GetExternalRowField(inputObject, index, field.name),
            dataType),
          dataType)
        val convertedField = if (field.nullable) {
          If(
            Invoke(inputObject, "isNullAt", BooleanType, Literal(index) :: Nil),
            Literal.create(null, dataType),
            fieldValue
          )
        } else {
          fieldValue
        }
        Literal(field.name) :: convertedField :: Nil
      })

      if (inputObject.nullable) {
        If(IsNull(inputObject),
          Literal.create(null, inputType),
          nonNullOutput)
      } else {
        nonNullOutput
      }
  }

  /**
   * Returns the `DataType` that can be used when generating code that converts input data
   * into the Spark SQL internal format.  Unlike `externalDataTypeFor`, the `DataType` returned
   * by this function can be more permissive since multiple external types may map to a single
   * internal type.  For example, for an input with DecimalType in external row, its external types
   * can be `scala.math.BigDecimal`, `java.math.BigDecimal`, or
   * `org.apache.spark.sql.types.Decimal`.
   */
  def externalDataTypeForInput(dt: DataType): DataType = dt match {
    case _ => dt match {
      case dtwc: DataTypeWithClass => dtwc.dt
      // In order to support both Decimal and java/scala BigDecimal in external row, we make this
      // as java.lang.Object.
      case _: DecimalType => ObjectType(classOf[Object])
      // In order to support both Array and Seq in external row, we make this as java.lang.Object.
      case _: ArrayType => ObjectType(classOf[Object])
      case _ => externalDataTypeFor(dt)
    }
  }

  @tailrec
  def externalDataTypeFor(dt: DataType): DataType = dt match {
    case kstw: DataTypeWithClass => externalDataTypeFor(kstw.dt)
    case _ if ScalaReflection.isNativeType(dt) => dt
    case TimestampType => ObjectType(classOf[java.sql.Timestamp])
    case DateType => ObjectType(classOf[java.sql.Date])
    case _: DecimalType => ObjectType(classOf[java.math.BigDecimal])
    case StringType => ObjectType(classOf[java.lang.String])
    case _: ArrayType => ObjectType(classOf[scala.collection.Seq[_]])
    case _: MapType => ObjectType(classOf[scala.collection.Map[_, _]])
    case _: StructType => ObjectType(classOf[Row])
    case p: PythonUserDefinedType => externalDataTypeFor(p.sqlType)
    case udt: UserDefinedType[_] => ObjectType(udt.userClass)
  }

  private def deserializerFor(schema: StructType): Expression = {
    val fields = schema.zipWithIndex.map { case (f, i) =>
      val dt = f.dataType match {
        case p: PythonUserDefinedType => p.sqlType
        case other => other
      }
      deserializerFor(GetColumnByOrdinal(i, dt))
    }

    CreateExternalRow(fields, schema)
  }

  private def deserializerFor(input: Expression): Expression = {
    deserializerFor(input, input.dataType)
  }

  private def deserializerFor(input: Expression, dataType: DataType): Expression = dataType match {
    case dt if ScalaReflection.isNativeType(dt) => input
    case kstw: DataTypeWithClass => deserializerFor(input, kstw.dt)

    case p: PythonUserDefinedType => deserializerFor(input, p.sqlType)

    case udt: UserDefinedType[_] =>
      val annotation = udt.userClass.getAnnotation(classOf[SQLUserDefinedType])
      val udtClass: Class[_] = if (annotation != null) {
        annotation.udt()
      } else {
        UDTRegistration.getUDTFor(udt.userClass.getName).getOrElse {
          throw new SparkException(s"${udt.userClass.getName} is not annotated with " +
            "SQLUserDefinedType nor registered with UDTRegistration.}")
        }
      }
      val obj = NewInstance(
        udtClass,
        Nil,
        dataType = ObjectType(udtClass))
      Invoke(obj, "deserialize", ObjectType(udt.userClass), input :: Nil)

    case TimestampType =>
      StaticInvoke(
        DateTimeUtils.getClass,
        ObjectType(classOf[java.sql.Timestamp]),
        "toJavaTimestamp",
        input :: Nil,
        returnNullable = false)

    case DateType =>
      StaticInvoke(
        DateTimeUtils.getClass,
        ObjectType(classOf[java.sql.Date]),
        "toJavaDate",
        input :: Nil,
        returnNullable = false)

    case _: DecimalType =>
      Invoke(input, "toJavaBigDecimal", ObjectType(classOf[java.math.BigDecimal]),
        returnNullable = false)

    case StringType =>
      Invoke(input, "toString", ObjectType(classOf[String]), returnNullable = false)

    case ArrayType(et, nullable) =>
      val arrayData =
        Invoke(
          MapObjects(deserializerFor(_), input, et),
          "array",
          ObjectType(classOf[Array[_]]), returnNullable = false)
      StaticInvoke(
        scala.collection.mutable.WrappedArray.getClass,
        ObjectType(classOf[Seq[_]]),
        "make",
        arrayData :: Nil,
        returnNullable = false)

    case MapType(kt, vt, valueNullable) =>
      val keyArrayType = ArrayType(kt, false)
      val keyData = deserializerFor(Invoke(input, "keyArray", keyArrayType))

      val valueArrayType = ArrayType(vt, valueNullable)
      val valueData = deserializerFor(Invoke(input, "valueArray", valueArrayType))

      StaticInvoke(
        ArrayBasedMapData.getClass,
        ObjectType(classOf[Map[_, _]]),
        "toScalaMap",
        keyData :: valueData :: Nil,
        returnNullable = false)

    case schema@StructType(fields) =>
      val convertedFields = fields.zipWithIndex.map { case (f, i) =>
        If(
          Invoke(input, "isNullAt", BooleanType, Literal(i) :: Nil),
          Literal.create(null, externalDataTypeFor(f.dataType match {
            case kstw: DataTypeWithClass => kstw.dt
            case o => o
          })),
          deserializerFor(GetStructField(input, i)))
      }
      If(IsNull(input),
        Literal.create(null, externalDataTypeFor(input.dataType)),
        CreateExternalRow(convertedFields, schema))
  }
}
