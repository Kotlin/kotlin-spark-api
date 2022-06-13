/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 3.0+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2021 JetBrains
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
 * This file contains all Column helper functions.
 * This includes easier Column creation and operator functions.
 */

package org.jetbrains.kotlinx.spark.api

import org.apache.spark.sql.Column
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.TypedColumn
import org.apache.spark.sql.functions
import kotlin.reflect.KProperty1

/**
 * Selects column based on the column name and returns it as a [TypedColumn].
 *
 * For example:
 * ```kotlin
 * dataset.col<_, Int>("a")
 * ```
 *
 * @note The column name can also reference to a nested column like `a.b`.
 */
inline fun <T, reified R> Dataset<T>.col(colName: String): TypedColumn<T, R> =
    org.jetbrains.kotlinx.spark.api.col<T, R>(colName)

/**
 * Selects column based on the column name and returns it as a [TypedColumn].
 *
 * For example:
 * ```kotlin
 * dataset<_, Int>("a")
 * ```
 * @note The column name can also reference to a nested column like `a.b`.
 */
inline operator fun <T, reified R> Dataset<T>.invoke(colName: String): TypedColumn<T, R> =
    org.jetbrains.kotlinx.spark.api.col<T, R>(colName)

/**
 * Selects column based on the column name and returns it as a [Column].
 *
 * @note The column name can also reference to a nested column like `a.b`.
 *
 */
operator fun Dataset<*>.invoke(colName: String): Column = apply(colName)

/**
 * Helper function to quickly get a [TypedColumn] (or [Column]) from a dataset in a refactor-safe manner.
 * ```kotlin
 *    val dataset: Dataset<YourClass> = ...
 *    val columnA: TypedColumn<YourClass, TypeOfA> = dataset.col(YourClass::a)
 * ```
 * @see invoke
 */
@Suppress("UNCHECKED_CAST")
inline fun <T, reified U> Dataset<T>.col(column: KProperty1<T, U>): TypedColumn<T, U> =
    col(column.name).`as`()


/**
 * Helper function to quickly get a [TypedColumn] (or [Column]) from a dataset in a refactor-safe manner.
 * ```kotlin
 *    val dataset: Dataset<YourClass> = ...
 *    val columnA: TypedColumn<YourClass, TypeOfA> = dataset(YourClass::a)
 * ```
 * @see col
 */
inline operator fun <T, reified U> Dataset<T>.invoke(column: KProperty1<T, U>): TypedColumn<T, U> = col(column)


/**
 * Can be used to create a [TypedColumn] for a simple [Dataset]
 * with just one single column called "value".
 */
inline fun <reified T> Dataset<T>.singleCol(colName: String = "value"): TypedColumn<T, T> {
    require(schema().fields().size == 1) { "This Dataset<${T::class.simpleName}> contains more than 1 column" }
    return org.jetbrains.kotlinx.spark.api.singleCol(colName)
}

@Suppress("FunctionName")
@Deprecated(
    message = "Changed to \"`===`\" to better reflect Scala API.",
    replaceWith = ReplaceWith("this `===` c"),
    level = DeprecationLevel.ERROR,
)
infix fun Column.`==`(c: Column): Column = `$eq$eq$eq`(c)

/**
 * Unary minus, i.e. negate the expression.
 * ```
 *   // Scala: select the amount column and negates all values.
 *   df.select( -df("amount") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.select( -df("amount") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.select( negate(col("amount") );
 * ```
 */
operator fun Column.unaryMinus(): Column = `unary_$minus`()

/**
 * Inversion of boolean expression, i.e. NOT.
 * ```
 *   // Scala: select rows that are not active (isActive === false)
 *   df.filter( !df("isActive") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.filter( !df("amount") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.filter( not(df.col("isActive")) );
 * ```
 */
operator fun Column.not(): Column = `unary_$bang`()

/**
 * Equality test.
 * ```
 *   // Scala:
 *   df.filter( df("colA") === df("colB") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.filter( df("colA") eq df("colB") )
 *   // or
 *   df.filter( df("colA") `===` df("colB") )
 *
 *   // Java
 *   import static org.apache.spark.sql.functions.*;
 *   df.filter( col("colA").equalTo(col("colB")) );
 * ```
 */
infix fun Column.eq(other: Any): Column = `$eq$eq$eq`(other)

/**
 * Equality test.
 * ```
 *   // Scala:
 *   df.filter( df("colA") === df("colB") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.filter( df("colA") eq df("colB") )
 *   // or
 *   df.filter( df("colA") `===` df("colB") )
 *
 *   // Java
 *   import static org.apache.spark.sql.functions.*;
 *   df.filter( col("colA").equalTo(col("colB")) );
 * ```
 */
infix fun Column.`===`(other: Any): Column = `$eq$eq$eq`(other)

/**
 * Inequality test.
 * ```
 *   // Scala:
 *   df.select( df("colA") =!= df("colB") )
 *   df.select( !(df("colA") === df("colB")) )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.select( df("colA") neq df("colB") )
 *   df.select( !(df("colA") eq df("colB")) )
 *   // or
 *   df.select( df("colA") `=!=` df("colB") )
 *   df.select( !(df("colA") `===` df("colB")) )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.select( col("colA").notEqual(col("colB")) );
 * ```
 */
infix fun Column.neq(other: Any): Column = `$eq$bang$eq`(other)

/**
 * Inequality test.
 * ```
 *   // Scala:
 *   df.select( df("colA") =!= df("colB") )
 *   df.select( !(df("colA") === df("colB")) )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.select( df("colA") neq df("colB") )
 *   df.select( !(df("colA") eq df("colB")) )
 *   // or
 *   df.select( df("colA") `=!=` df("colB") )
 *   df.select( !(df("colA") `===` df("colB")) )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.select( col("colA").notEqual(col("colB")) );
 * ```
 */
infix fun Column.`=!=`(other: Any): Column = `$eq$bang$eq`(other)

/**
 * Greater than.
 * ```
 *   // Scala: The following selects people older than 21.
 *   people.select( people("age") > 21 )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("age") gt 21 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("age").gt(21) );
 * ```
 */
infix fun Column.gt(other: Any): Column = `$greater`(other)

/**
 * Less than.
 * ```
 *   // Scala: The following selects people younger than 21.
 *   people.select( people("age") < 21 )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("age") lt 21 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("age").lt(21) );
 * ```
 */
infix fun Column.lt(other: Any): Column = `$less`(other)

/**
 * Less than or equal to.
 * ```
 *   // Scala: The following selects people age 21 or younger than 21.
 *   people.select( people("age") <= 21 )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("age") leq 21 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("age").leq(21) );
 * ```
 */
infix fun Column.leq(other: Any): Column = `$less$eq`(other)

/**
 * Greater than or equal to an expression.
 * ```
 *   // Scala: The following selects people age 21 or older than 21.
 *   people.select( people("age") >= 21 )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("age") geq 21 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("age").geq(21) );
 * ```
 */
infix fun Column.geq(other: Any): Column = `$greater$eq`(other)

/**
 * True if the current column is in the given [range].
 * ```
 *   // Scala:
 *   df.where( df("colA").between(1, 5) )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.where( df("colA") inRangeOf 1..5 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.where( df.col("colA").between(1, 5) );
 * ```
 */
infix fun Column.inRangeOf(range: ClosedRange<*>): Column = between(range.start, range.endInclusive)

/**
 * Boolean OR.
 * ```
 *   // Scala: The following selects people that are in school or employed.
 *   people.filter( people("inSchool") || people("isEmployed") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.filter( people("inSchool") or people("isEmployed") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.filter( people.col("inSchool").or(people.col("isEmployed")) );
 * ```
 */
infix fun Column.or(other: Any): Column = `$bar$bar`(other)

/**
 * Boolean AND.
 * ```
 *   // Scala: The following selects people that are in school and employed at the same time.
 *   people.select( people("inSchool") && people("isEmployed") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("inSchool") and people("isEmployed") )
 *   // or
 *   people.select( people("inSchool") `&&` people("isEmployed") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("inSchool").and(people.col("isEmployed")) );
 * ```
 */
infix fun Column.and(other: Any): Column = `$amp$amp`(other)

/**
 * Boolean AND.
 * ```
 *   // Scala: The following selects people that are in school and employed at the same time.
 *   people.select( people("inSchool") && people("isEmployed") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("inSchool") and people("isEmployed") )
 *   // or
 *   people.select( people("inSchool") `&&` people("isEmployed") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("inSchool").and(people.col("isEmployed")) );
 * ```
 */
infix fun Column.`&&`(other: Any): Column = `$amp$amp`(other)

/**
 * Multiplication of this expression and another expression.
 * ```
 *   // Scala: The following multiplies a person's height by their weight.
 *   people.select( people("height") * people("weight") )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("height") * people("weight") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("height").multiply(people.col("weight")) );
 * ```
 */
operator fun Column.times(other: Any): Column = `$times`(other)

/**
 * Division this expression by another expression.
 * ```
 *   // Scala: The following divides a person's height by their weight.
 *   people.select( people("height") / people("weight") )
 *
 *   // Kotlin
 *   import org.jetbrains.kotlinx.spark.api.*
 *   people.select( people("height") / people("weight") )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   people.select( people.col("height").divide(people.col("weight")) );
 * ```
 */
operator fun Column.div(other: Any): Column = `$div`(other)

/**
 * Modulo (a.k.a. remainder) expression.
 * ```
 *   // Scala:
 *   df.where( df("colA") % 2 === 0 )
 *
 *   // Kotlin:
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.where( df("colA") % 2 eq 0 )
 *
 *   // Java:
 *   import static org.apache.spark.sql.functions.*;
 *   df.where( df.col("colA").mod(2).equalTo(0) );
 * ```
 */
operator fun Column.rem(other: Any): Column = `$percent`(other)

/**
 * An expression that gets an item at position `ordinal` out of an array,
 * or gets a value by key `key` in a `MapType`.
 * ```
 *   // Scala:
 *   df.where( df("arrayColumn").getItem(0) === 5 )
 *
 *   // Kotlin
 *   import org.jetbrains.kotlinx.spark.api.*
 *   df.where( df("arrayColumn")[0] eq 5 )
 *
 *   // Java
 *   import static org.apache.spark.sql.functions.*;
 *   df.where( df.col("arrayColumn").getItem(0).equalTo(5) );
 * ```
 */
operator fun Column.get(key: Any): Column = getItem(key)

/**
 * Provides a type hint about the expected return value of this column. This information can
 * be used by operations such as `select` on a [Dataset] to automatically convert the
 * results into the correct JVM types.
 *
 * ```
 * val df: Dataset<Row> = ...
 * val typedColumn: Dataset<Int> = df.selectTyped( col("a").`as`<Int>() )
 * ```
 *
 * @see typed
 */
@Suppress("UNCHECKED_CAST")
inline fun <DsType, reified U> Column.`as`(): TypedColumn<DsType, U> = `as`(encoder<U>()) as TypedColumn<DsType, U>

/**
 * Provides a type hint about the expected return value of this column. This information can
 * be used by operations such as `select` on a [Dataset] to automatically convert the
 * results into the correct JVM types.
 *
 * ```
 * val df: Dataset<Row> = ...
 * val typedColumn: Dataset<Int> = df.selectTyped( col("a").typed<Int>() )
 * ```
 *
 * @see as
 */
@Suppress("UNCHECKED_CAST")
inline fun <DsType, reified T> Column.typed(): TypedColumn<DsType, T> = `as`()

/**
 * Creates a [Column] of literal value.
 *
 * The passed in object is returned directly if it is already a [Column].
 * If the object is a Scala Symbol, it is converted into a [Column] also.
 * Otherwise, a new [Column] is created to represent the literal value.
 *
 * This is just a shortcut to the function from [org.apache.spark.sql.functions].
 * For all the functions, simply add `import org.apache.spark.sql.functions.*` to your file.
 */
fun lit(a: Any): Column = functions.lit(a)

/**
 * Returns a [TypedColumn] based on the given column name and type [DsType].
 *
 * This is just a shortcut to the function from [org.apache.spark.sql.functions] combined with an [as] call.
 * For all the functions, simply add `import org.apache.spark.sql.functions.*` to your file.
 *
 * @see col
 * @see as
 */
inline fun <DsType, reified U> col(colName: String): TypedColumn<DsType, U> = functions.col(colName).`as`()

/**
 * Can be used to create a [TypedColumn] for a simple [Dataset]
 * with just one single column called "value".
 */
inline fun <reified DsType> singleCol(colName: String = "value"): TypedColumn<DsType, DsType> = functions.col(colName).`as`()

/**
 * Returns a [Column] based on the given column name.
 *
 */
fun col(colName: String): Column = functions.col(colName)

/**
 * Returns a [Column] based on the given class attribute, not connected to a dataset.
 * ```kotlin
 *    val dataset: Dataset<YourClass> = ...
 *    val new: Dataset<Tuple2<TypeOfA, TypeOfB>> = dataset.select( col(YourClass::a), col(YourClass::b) )
 * ```
 * @see col
 */
@Suppress("UNCHECKED_CAST")
inline fun <DsType, reified U> col(column: KProperty1<DsType, U>): TypedColumn<DsType, U> =
    functions.col(column.name).`as`()
