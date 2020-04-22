package org.apache.spark.sql

import org.apache.spark.sql.catalyst.analysis.Resolver
import org.apache.spark.sql.catalyst.expressions.{AttributeReference, Expression}
import org.apache.spark.sql.catalyst.util.StringUtils
import org.apache.spark.sql.types.{DataType, Metadata, StructField, StructType}


trait DataTypeWithClass {
  val dt: DataType
  val cls: Class[_]
  val nullable: Boolean
}

trait ComplexWrapper extends DataTypeWithClass

class KDataTypeWrapper(val dt: StructType
                       , val cls: Class[_]
                       , val nullable: Boolean = true) extends StructType with ComplexWrapper {
  override def fieldNames: Array[String] = dt.fieldNames

  override def names: Array[String] = dt.names

  override def equals(that: Any): Boolean = dt.equals(that)

  override def hashCode(): Int = dt.hashCode()

  override def add(field: StructField): StructType = dt.add(field)

  override def add(name: String, dataType: DataType): StructType = dt.add(name, dataType)

  override def add(name: String, dataType: DataType, nullable: Boolean): StructType = dt.add(name, dataType, nullable)

  override def add(name: String, dataType: DataType, nullable: Boolean, metadata: Metadata): StructType = dt.add(name, dataType, nullable, metadata)

  override def add(name: String, dataType: DataType, nullable: Boolean, comment: String): StructType = dt.add(name, dataType, nullable, comment)

  override def add(name: String, dataType: String): StructType = dt.add(name, dataType)

  override def add(name: String, dataType: String, nullable: Boolean): StructType = dt.add(name, dataType, nullable)

  override def add(name: String, dataType: String, nullable: Boolean, metadata: Metadata): StructType = dt.add(name, dataType, nullable, metadata)

  override def add(name: String, dataType: String, nullable: Boolean, comment: String): StructType = dt.add(name, dataType, nullable, comment)

  override def apply(name: String): StructField = dt.apply(name)

  override def apply(names: Set[String]): StructType = dt.apply(names)

  override def fieldIndex(name: String): Int = dt.fieldIndex(name)

  override private[sql] def getFieldIndex(name: String) = dt.getFieldIndex(name)

  override private[sql] def findNestedField(fieldNames: Seq[String], includeCollections: Boolean) = dt.findNestedField(fieldNames, includeCollections)

  override private[sql] def buildFormattedString(prefix: String, builder: StringBuilder): Unit = dt.buildFormattedString(prefix, builder)

  override protected[sql] def toAttributes: Seq[AttributeReference] = dt.toAttributes

  override def treeString: String = dt.treeString

  override def treeString(maxDepth: Int): String = dt.treeString(maxDepth)

  override def printTreeString(): Unit = dt.printTreeString()

  private[sql] override def jsonValue = dt.jsonValue

  override def apply(fieldIndex: Int): StructField = dt.apply(fieldIndex)

  override def length: Int = dt.length

  override def iterator: Iterator[StructField] = dt.iterator

  override def defaultSize: Int = dt.defaultSize

  override def simpleString: String = dt.simpleString

  override def catalogString: String = dt.catalogString

  override def sql: String = dt.sql

  override def toDDL: String = dt.toDDL

  private[sql] override def simpleString(maxNumberFields: Int) = dt.simpleString(maxNumberFields)

  override private[sql] def merge(that: StructType) = dt.merge(that)

  private[spark] override def asNullable = dt.asNullable

  private[spark] override def existsRecursively(f: DataType => Boolean) = dt.existsRecursively(f)

  override private[sql] lazy val interpretedOrdering = dt.interpretedOrdering

  override def toString = s"KDataTypeWrapper(dt=$dt, cls=$cls, nullable=$nullable)"
}

case class KComplexTypeWrapper(dt: DataType, cls: Class[_], nullable: Boolean) extends DataType with ComplexWrapper {
  override private[sql] def unapply(e: Expression) = dt.unapply(e)

  override def typeName: String = dt.typeName

  override private[sql] def jsonValue = dt.jsonValue

  override def json: String = dt.json

  override def prettyJson: String = dt.prettyJson

  override def simpleString: String = dt.simpleString

  override def catalogString: String = dt.catalogString

  override private[sql] def simpleString(maxNumberFields: Int) = dt.simpleString(maxNumberFields)

  override def sql: String = dt.sql

  override private[spark] def sameType(other: DataType) = dt.sameType(other)

  override private[spark] def existsRecursively(f: DataType => Boolean) = dt.existsRecursively(f)

  private[sql] override def defaultConcreteType = dt.defaultConcreteType

  private[sql] override def acceptsType(other: DataType) = dt.acceptsType(other)

  override def defaultSize: Int = dt.defaultSize

  override private[spark] def asNullable = dt.asNullable

}

case class KSimpleTypeWrapper(dt: DataType, cls: Class[_], nullable: Boolean) extends DataType with DataTypeWithClass {
  override private[sql] def unapply(e: Expression) = dt.unapply(e)

  override def typeName: String = dt.typeName

  override private[sql] def jsonValue = dt.jsonValue

  override def json: String = dt.json

  override def prettyJson: String = dt.prettyJson

  override def simpleString: String = dt.simpleString

  override def catalogString: String = dt.catalogString

  override private[sql] def simpleString(maxNumberFields: Int) = dt.simpleString(maxNumberFields)

  override def sql: String = dt.sql

  override private[spark] def sameType(other: DataType) = dt.sameType(other)

  override private[spark] def existsRecursively(f: DataType => Boolean) = dt.existsRecursively(f)

  private[sql] override def defaultConcreteType = dt.defaultConcreteType

  private[sql] override def acceptsType(other: DataType) = dt.acceptsType(other)

  override def defaultSize: Int = dt.defaultSize

  override private[spark] def asNullable = dt.asNullable
}

object helpme {

  def listToSeq(i: java.util.List[_]): Seq[_] = Seq(i.toArray: _*)
}