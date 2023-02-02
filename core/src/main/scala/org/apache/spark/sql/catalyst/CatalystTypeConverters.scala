package org.apache.spark.sql.catalyst

import kotlin.jvm.JvmClassMappingKt
import kotlin.reflect.{KClass, KFunction, KProperty1}
import kotlin.reflect.full.KClasses

import java.lang.{Iterable => JavaIterable}
import java.math.{BigDecimal => JavaBigDecimal}
import java.math.{BigInteger => JavaBigInteger}
import java.sql.{Date, Timestamp}
import java.time.{Instant, LocalDate}
import java.util.{Map => JavaMap}
import javax.annotation.Nullable
import scala.language.existentials
import org.apache.spark.sql.Row
import org.apache.spark.sql.catalyst.expressions._
import org.apache.spark.sql.catalyst.util._
import org.apache.spark.sql.internal.SQLConf
import org.apache.spark.sql.types._
import org.apache.spark.unsafe.types.UTF8String

/**
 * Functions to convert Scala types to Catalyst types and vice versa.
 */
object CatalystTypeConverters {
  // The Predef.Map is scala.collection.immutable.Map.
  // Since the map values can be mutable, we explicitly import scala.collection.Map at here.

  import scala.collection.Map

  private[sql] def isPrimitive(dataType: DataType): Boolean = {
    dataType match {
      case BooleanType => true
      case ByteType => true
      case ShortType => true
      case IntegerType => true
      case LongType => true
      case FloatType => true
      case DoubleType => true
      case _ => false
    }
  }

  private def getConverterForType(dataType: DataType): CatalystTypeConverter[Any, Any, Any] = {
    val converter = dataType match {
      case udt: UserDefinedType[_] => UDTConverter(udt)
      case arrayType: ArrayType => ArrayConverter(arrayType.elementType)
      case mapType: MapType => MapConverter(mapType.keyType, mapType.valueType)
      case structType: StructType => StructConverter(structType)
      case StringType => StringConverter
      case DateType if SQLConf.get.datetimeJava8ApiEnabled => LocalDateConverter
      case DateType => DateConverter
      case TimestampType if SQLConf.get.datetimeJava8ApiEnabled => InstantConverter
      case TimestampType => TimestampConverter
      case dt: DecimalType => new DecimalConverter(dt)
      case BooleanType => BooleanConverter
      case ByteType => ByteConverter
      case ShortType => ShortConverter
      case IntegerType => IntConverter
      case LongType => LongConverter
      case FloatType => FloatConverter
      case DoubleType => DoubleConverter
      case dataType: DataType => IdentityConverter(dataType)
    }
    converter.asInstanceOf[CatalystTypeConverter[Any, Any, Any]]
  }

  /**
   * Converts a Scala type to its Catalyst equivalent (and vice versa).
   *
   * @tparam ScalaInputType  The type of Scala values that can be converted to Catalyst.
   * @tparam ScalaOutputType The type of Scala values returned when converting Catalyst to Scala.
   * @tparam CatalystType    The internal Catalyst type used to represent values of this Scala type.
   */
  private abstract class CatalystTypeConverter[ScalaInputType, ScalaOutputType, CatalystType]
    extends Serializable {

    /**
     * Converts a Scala type to its Catalyst equivalent while automatically handling nulls
     * and Options.
     */
    final def toCatalyst(@Nullable maybeScalaValue: Any): CatalystType = {
      if (maybeScalaValue == null) {
        null.asInstanceOf[CatalystType]
      } else maybeScalaValue match {
        case opt: Option[ScalaInputType] =>
          if (opt.isDefined) {
            toCatalystImpl(opt.get)
          } else {
            null.asInstanceOf[CatalystType]
          }
        case _ =>
          toCatalystImpl(maybeScalaValue.asInstanceOf[ScalaInputType])
      }
    }

    /**
     * Given a Catalyst row, convert the value at column `column` to its Scala equivalent.
     */
    final def toScala(row: InternalRow, column: Int): ScalaOutputType = {
      if (row.isNullAt(column)) null.asInstanceOf[ScalaOutputType] else toScalaImpl(row, column)
    }

    /**
     * Convert a Catalyst value to its Scala equivalent.
     */
    def toScala(@Nullable catalystValue: CatalystType): ScalaOutputType

    /**
     * Converts a Scala value to its Catalyst equivalent.
     *
     * @param scalaValue the Scala value, guaranteed not to be null.
     * @return the Catalyst value.
     */
    protected def toCatalystImpl(scalaValue: ScalaInputType): CatalystType

    /**
     * Given a Catalyst row, convert the value at column `column` to its Scala equivalent.
     * This method will only be called on non-null columns.
     */
    protected def toScalaImpl(row: InternalRow, column: Int): ScalaOutputType
  }

  private case class IdentityConverter(dataType: DataType)
    extends CatalystTypeConverter[Any, Any, Any] {
    override def toCatalystImpl(scalaValue: Any): Any = scalaValue

    override def toScala(catalystValue: Any): Any = catalystValue

    override def toScalaImpl(row: InternalRow, column: Int): Any = row.get(column, dataType)
  }

  private case class UDTConverter[A >: Null](
                                              udt: UserDefinedType[A]) extends CatalystTypeConverter[A, A, Any] {
    // toCatalyst (it calls toCatalystImpl) will do null check.
    override def toCatalystImpl(scalaValue: A): Any = udt.serialize(scalaValue)

    override def toScala(catalystValue: Any): A = {
      if (catalystValue == null) null else udt.deserialize(catalystValue)
    }

    override def toScalaImpl(row: InternalRow, column: Int): A =
      toScala(row.get(column, udt.sqlType))
  }

  /** Converter for arrays, sequences, and Java iterables. */
  private case class ArrayConverter(
                                     elementType: DataType) extends CatalystTypeConverter[Any, Seq[Any], ArrayData] {

    private[this] val elementConverter = getConverterForType(elementType)

    override def toCatalystImpl(scalaValue: Any): ArrayData = {
      scalaValue match {
        case a: Array[_] =>
          new GenericArrayData(a.map(elementConverter.toCatalyst))
        case s: Seq[_] =>
          new GenericArrayData(s.map(elementConverter.toCatalyst).toArray)
        case i: JavaIterable[_] =>
          val iter = i.iterator
          val convertedIterable = scala.collection.mutable.ArrayBuffer.empty[Any]
          while (iter.hasNext) {
            val item = iter.next()
            convertedIterable += elementConverter.toCatalyst(item)
          }
          new GenericArrayData(convertedIterable.toArray)
        case other => throw new IllegalArgumentException(
          s"The value (${other.toString}) of the type (${other.getClass.getCanonicalName}) "
            + s"cannot be converted to an array of ${elementType.catalogString}")
      }
    }

    override def toScala(catalystValue: ArrayData): Seq[Any] = {
      if (catalystValue == null) {
        null
      } else if (isPrimitive(elementType)) {
        catalystValue.toArray[Any](elementType)
      } else {
        val result = new Array[Any](catalystValue.numElements())
        catalystValue.foreach(elementType, (i, e) => {
          result(i) = elementConverter.toScala(e)
        })
        result
      }
    }

    override def toScalaImpl(row: InternalRow, column: Int): Seq[Any] =
      toScala(row.getArray(column))
  }

  private case class MapConverter(
                                   keyType: DataType,
                                   valueType: DataType)
    extends CatalystTypeConverter[Any, Map[Any, Any], MapData] {

    private[this] val keyConverter = getConverterForType(keyType)
    private[this] val valueConverter = getConverterForType(valueType)

    override def toCatalystImpl(scalaValue: Any): MapData = {
      val keyFunction = (k: Any) => keyConverter.toCatalyst(k)
      val valueFunction = (k: Any) => valueConverter.toCatalyst(k)

      scalaValue match {
        case map: Map[_, _] => ArrayBasedMapData(map, keyFunction, valueFunction)
        case javaMap: JavaMap[_, _] => ArrayBasedMapData(javaMap, keyFunction, valueFunction)
        case other => throw new IllegalArgumentException(
          s"The value (${other.toString}) of the type (${other.getClass.getCanonicalName}) "
            + "cannot be converted to a map type with "
            + s"key type (${keyType.catalogString}) and value type (${valueType.catalogString})")
      }
    }

    override def toScala(catalystValue: MapData): Map[Any, Any] = {
      if (catalystValue == null) {
        null
      } else {
        val keys = catalystValue.keyArray().toArray[Any](keyType)
        val values = catalystValue.valueArray().toArray[Any](valueType)
        val convertedKeys =
          if (isPrimitive(keyType)) keys else keys.map(keyConverter.toScala)
        val convertedValues =
          if (isPrimitive(valueType)) values else values.map(valueConverter.toScala)

        convertedKeys.zip(convertedValues).toMap
      }
    }

    override def toScalaImpl(row: InternalRow, column: Int): Map[Any, Any] =
      toScala(row.getMap(column))
  }

  private case class StructConverter(
                                      structType: StructType) extends CatalystTypeConverter[Any, Row, InternalRow] {

    private[this] val converters = structType.fields.map { f => getConverterForType(f.dataType) }

    override def toCatalystImpl(scalaValue: Any): InternalRow = scalaValue match {
      case row: Row =>
        val ar = new Array[Any](row.size)
        var idx = 0
        while (idx < row.size) {
          ar(idx) = converters(idx).toCatalyst(row(idx))
          idx += 1
        }
        new GenericInternalRow(ar)

      case p: Product =>
        val ar = new Array[Any](structType.size)
        val iter = p.productIterator
        var idx = 0
        while (idx < structType.size) {
          ar(idx) = converters(idx).toCatalyst(iter.next())
          idx += 1
        }
        new GenericInternalRow(ar)

      case ktDataClass: Any if JvmClassMappingKt.getKotlinClass(ktDataClass.getClass).isData =>
        import scala.collection.JavaConverters._
        val klass: KClass[Any] = JvmClassMappingKt.getKotlinClass(ktDataClass.getClass).asInstanceOf[KClass[Any]]
        val iter: Iterator[KProperty1[Any,_]] = KClasses.getDeclaredMemberProperties(klass).iterator().asScala
        val ar = new Array[Any](structType.size)
        var idx = 0
        while (idx < structType.size) {
          ar(idx) = converters(idx).toCatalyst(iter.next().get(ktDataClass))
          idx += 1
        }
        new GenericInternalRow(ar)

      case other => throw new IllegalArgumentException(
        s"The value (${other.toString}) of the type (${other.getClass.getCanonicalName}) "
          + s"cannot be converted to ${structType.catalogString}")
    }

    override def toScala(row: InternalRow): Row = {
      if (row == null) {
        null
      } else {
        val ar = new Array[Any](row.numFields)
        var idx = 0
        while (idx < row.numFields) {
          ar(idx) = converters(idx).toScala(row, idx)
          idx += 1
        }
        new GenericRowWithSchema(ar, structType)
      }
    }

    override def toScalaImpl(row: InternalRow, column: Int): Row =
      toScala(row.getStruct(column, structType.size))
  }

  private object StringConverter extends CatalystTypeConverter[Any, String, UTF8String] {
    override def toCatalystImpl(scalaValue: Any): UTF8String = scalaValue match {
      case str: String => UTF8String.fromString(str)
      case utf8: UTF8String => utf8
      case chr: Char => UTF8String.fromString(chr.toString)
      case other => throw new IllegalArgumentException(
        s"The value (${other.toString}) of the type (${other.getClass.getCanonicalName}) "
          + s"cannot be converted to the string type")
    }

    override def toScala(catalystValue: UTF8String): String =
      if (catalystValue == null) null else catalystValue.toString

    override def toScalaImpl(row: InternalRow, column: Int): String =
      row.getUTF8String(column).toString
  }

  private object DateConverter extends CatalystTypeConverter[Date, Date, Any] {
    override def toCatalystImpl(scalaValue: Date): Int = DateTimeUtils.fromJavaDate(scalaValue)

    override def toScala(catalystValue: Any): Date =
      if (catalystValue == null) null else DateTimeUtils.toJavaDate(catalystValue.asInstanceOf[Int])

    override def toScalaImpl(row: InternalRow, column: Int): Date =
      DateTimeUtils.toJavaDate(row.getInt(column))
  }

  private object LocalDateConverter extends CatalystTypeConverter[LocalDate, LocalDate, Any] {
    override def toCatalystImpl(scalaValue: LocalDate): Int = {
      DateTimeUtils.localDateToDays(scalaValue)
    }

    override def toScala(catalystValue: Any): LocalDate = {
      if (catalystValue == null) null
      else DateTimeUtils.daysToLocalDate(catalystValue.asInstanceOf[Int])
    }

    override def toScalaImpl(row: InternalRow, column: Int): LocalDate =
      DateTimeUtils.daysToLocalDate(row.getInt(column))
  }

  private object TimestampConverter extends CatalystTypeConverter[Timestamp, Timestamp, Any] {
    override def toCatalystImpl(scalaValue: Timestamp): Long =
      DateTimeUtils.fromJavaTimestamp(scalaValue)

    override def toScala(catalystValue: Any): Timestamp =
      if (catalystValue == null) null
      else DateTimeUtils.toJavaTimestamp(catalystValue.asInstanceOf[Long])

    override def toScalaImpl(row: InternalRow, column: Int): Timestamp =
      DateTimeUtils.toJavaTimestamp(row.getLong(column))
  }

  private object InstantConverter extends CatalystTypeConverter[Instant, Instant, Any] {
    override def toCatalystImpl(scalaValue: Instant): Long =
      DateTimeUtils.instantToMicros(scalaValue)

    override def toScala(catalystValue: Any): Instant =
      if (catalystValue == null) null
      else DateTimeUtils.microsToInstant(catalystValue.asInstanceOf[Long])

    override def toScalaImpl(row: InternalRow, column: Int): Instant =
      DateTimeUtils.microsToInstant(row.getLong(column))
  }

  private class DecimalConverter(dataType: DecimalType)
    extends CatalystTypeConverter[Any, JavaBigDecimal, Decimal] {

    private val nullOnOverflow = !SQLConf.get.ansiEnabled

    override def toCatalystImpl(scalaValue: Any): Decimal = {
      val decimal = scalaValue match {
        case d: BigDecimal => Decimal(d)
        case d: JavaBigDecimal => Decimal(d)
        case d: JavaBigInteger => Decimal(d)
        case d: Decimal => d
        case other => throw new IllegalArgumentException(
          s"The value (${other.toString}) of the type (${other.getClass.getCanonicalName}) "
            + s"cannot be converted to ${dataType.catalogString}")
      }
      decimal.toPrecision(dataType.precision, dataType.scale, Decimal.ROUND_HALF_UP, nullOnOverflow)
    }

    override def toScala(catalystValue: Decimal): JavaBigDecimal = {
      if (catalystValue == null) null
      else catalystValue.toJavaBigDecimal
    }

    override def toScalaImpl(row: InternalRow, column: Int): JavaBigDecimal =
      row.getDecimal(column, dataType.precision, dataType.scale).toJavaBigDecimal
  }

  private abstract class PrimitiveConverter[T] extends CatalystTypeConverter[T, Any, Any] {
    final override def toScala(catalystValue: Any): Any = catalystValue

    final override def toCatalystImpl(scalaValue: T): Any = scalaValue
  }

  private object BooleanConverter extends PrimitiveConverter[Boolean] {
    override def toScalaImpl(row: InternalRow, column: Int): Boolean = row.getBoolean(column)
  }

  private object ByteConverter extends PrimitiveConverter[Byte] {
    override def toScalaImpl(row: InternalRow, column: Int): Byte = row.getByte(column)
  }

  private object ShortConverter extends PrimitiveConverter[Short] {
    override def toScalaImpl(row: InternalRow, column: Int): Short = row.getShort(column)
  }

  private object IntConverter extends PrimitiveConverter[Int] {
    override def toScalaImpl(row: InternalRow, column: Int): Int = row.getInt(column)
  }

  private object LongConverter extends PrimitiveConverter[Long] {
    override def toScalaImpl(row: InternalRow, column: Int): Long = row.getLong(column)
  }

  private object FloatConverter extends PrimitiveConverter[Float] {
    override def toScalaImpl(row: InternalRow, column: Int): Float = row.getFloat(column)
  }

  private object DoubleConverter extends PrimitiveConverter[Double] {
    override def toScalaImpl(row: InternalRow, column: Int): Double = row.getDouble(column)
  }

  /**
   * Creates a converter function that will convert Scala objects to the specified Catalyst type.
   * Typical use case would be converting a collection of rows that have the same schema. You will
   * call this function once to get a converter, and apply it to every row.
   */
  def createToCatalystConverter(dataType: DataType): Any => Any = {
    if (isPrimitive(dataType)) {
      // Although the `else` branch here is capable of handling inbound conversion of primitives,
      // we add some special-case handling for those types here. The motivation for this relates to
      // Java method invocation costs: if we have rows that consist entirely of primitive columns,
      // then returning the same conversion function for all of the columns means that the call site
      // will be monomorphic instead of polymorphic. In microbenchmarks, this actually resulted in
      // a measurable performance impact. Note that this optimization will be unnecessary if we
      // use code generation to construct Scala Row -> Catalyst Row converters.
      def convert(maybeScalaValue: Any): Any = {
        maybeScalaValue match {
          case option: Option[Any] =>
            option.orNull
          case _ =>
            maybeScalaValue
        }
      }

      convert
    } else {
      getConverterForType(dataType).toCatalyst
    }
  }

  /**
   * Creates a converter function that will convert Catalyst types to Scala type.
   * Typical use case would be converting a collection of rows that have the same schema. You will
   * call this function once to get a converter, and apply it to every row.
   */
  def createToScalaConverter(dataType: DataType): Any => Any = {
    if (isPrimitive(dataType)) {
      identity
    } else {
      getConverterForType(dataType).toScala
    }
  }

  /**
   * Converts Scala objects to Catalyst rows / types.
   *
   * Note: This should be called before do evaluation on Row
   * (It does not support UDT)
   * This is used to create an RDD or test results with correct types for Catalyst.
   */
  def convertToCatalyst(a: Any): Any = a match {
    case s: String => StringConverter.toCatalyst(s)
    case d: Date => DateConverter.toCatalyst(d)
    case ld: LocalDate => LocalDateConverter.toCatalyst(ld)
    case t: Timestamp => TimestampConverter.toCatalyst(t)
    case i: Instant => InstantConverter.toCatalyst(i)
    case d: BigDecimal => new DecimalConverter(DecimalType(d.precision, d.scale)).toCatalyst(d)
    case d: JavaBigDecimal => new DecimalConverter(DecimalType(d.precision, d.scale)).toCatalyst(d)
    case seq: Seq[Any] => new GenericArrayData(seq.map(convertToCatalyst).toArray)
    case r: Row => InternalRow(r.toSeq.map(convertToCatalyst): _*)
    case arr: Array[Any] => new GenericArrayData(arr.map(convertToCatalyst))
    case map: Map[_, _] =>
      ArrayBasedMapData(
        map,
        (key: Any) => convertToCatalyst(key),
        (value: Any) => convertToCatalyst(value))
    case other => other
  }

  /**
   * Converts Catalyst types used internally in rows to standard Scala types
   * This method is slow, and for batch conversion you should be using converter
   * produced by createToScalaConverter.
   */
  def convertToScala(catalystValue: Any, dataType: DataType): Any = {
    createToScalaConverter(dataType)(catalystValue)
  }
}
