package org.apache.spark.sql

import org.apache.spark.sql.catalyst.JavaTypeInference
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.types.StructType

import scala.reflect.ClassTag

object KotlinEncoder {
  def bean[T](kotlinClass: Class[T]): ExpressionEncoder[T] = {
    val schema = JavaTypeInference.inferDataType(kotlinClass)._1
    assert(schema.isInstanceOf[StructType])

    val objSerializer = MyJavaInference.serializerFor(kotlinClass)
    val objDeserializer = MyJavaInference.deserializerFor(kotlinClass)

    new ExpressionEncoder[T](
      objSerializer,
      objDeserializer,
      ClassTag[T](kotlinClass))
  }
}
