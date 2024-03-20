package org.jetbrains.kotlinx.spark.api.plugin.annotations


/**
 * Annotate Data Classes with this annotation
 * to make them encodable by Spark.
 *
 * This requires the Gradle Plugin "org.jetbrains.kotlinx.spark.plugin.gradle-plugin"
 * to be enabled for your project.
 *
 * In practice, this annotation will generate `@get:JvmName("propertyName")`
 * for each argument in the primary constructor. This will satisfy the Spark Property
 * encoder with the expectation of there being a "propertyName()" getter-function for each property.
 *
 * See [ColumnName] for custom column names.
 */
@Target(AnnotationTarget.CLASS)
annotation class Sparkify

/**
 * Requires the data class to have the [@Sparkify][Sparkify] annotation!
 *
 * Annotate the primary constructor arguments with this annotation to
 * specify a custom column name for the Spark Dataset.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ColumnName(val name: String)
