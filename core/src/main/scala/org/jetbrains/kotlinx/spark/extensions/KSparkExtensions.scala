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
package org.jetbrains.kotlinx.spark.extensions

import org.apache.spark.SparkContext
import org.apache.spark.sql._

import java.util
import scala.collection.JavaConverters
import scala.reflect.ClassTag

object KSparkExtensions {

  val kotlinVersion = /*$"\""+kotlin+"\""$*/ /*-*/ ""
  val scalaVersion = /*$"\""+scala+"\""$*/ /*-*/ ""
  val scalaCompatVersion = /*$"\""+scalaCompat+"\""$*/ /*-*/ ""
  val sparkVersion = /*$"\""+spark+"\""$*/ /*-*/ ""
  val sparkMinorVersion = /*$"\""+sparkMinor+"\""$*/ /*-*/ ""

  def col(d: Dataset[_], name: String): Column = d.col(name)

  def col(name: String): Column = functions.col(name)

  def lit(literal: Any): Column = functions.lit(literal)

  def collectAsList[T](ds: Dataset[T]): util.List[T] = JavaConverters.seqAsJavaList(ds.collect())


  def debugCodegen(df: Dataset[_]): Unit = {
    import org.apache.spark.sql.execution.debug._
    df.debugCodegen()
  }

  def debug(df: Dataset[_]): Unit = {
    import org.apache.spark.sql.execution.debug._
    df.debug()
  }

  def sparkContext(s: SparkSession): SparkContext = s.sparkContext

  /**
   * Produces a ClassTag[T], which is actually just a casted ClassTag[AnyRef].
   *
   * This method is used to keep ClassTags out of the external Java API, as the Java compiler
   * cannot produce them automatically. While this ClassTag-faking does please the compiler,
   * it can cause problems at runtime if the Scala API relies on ClassTags for correctness.
   *
   * Often, though, a ClassTag[AnyRef] will not lead to incorrect behavior, just worse performance
   * or security issues. For instance, an Array[AnyRef] can hold any type T, but may lose primitive
   * specialization.
   */
  def fakeClassTag[T]: ClassTag[T] = ClassTag.AnyRef.asInstanceOf[ClassTag[T]]
}
