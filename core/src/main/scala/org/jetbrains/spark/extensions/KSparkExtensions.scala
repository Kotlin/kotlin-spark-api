package org.jetbrains.spark.extensions

import org.apache.spark.sql._

object KSparkExtensions {
  def col(d: Dataset[_], name: String) = d.col(name)

  def col(name: String) = functions.col(name)

  def lit(literal: Any) = functions.lit(literal)

}
