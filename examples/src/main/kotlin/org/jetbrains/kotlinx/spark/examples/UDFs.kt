/*-
 * =LICENSE=
 * Kotlin Spark API: Examples for Spark 3.2+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2022 JetBrains
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
package org.jetbrains.kotlinx.spark.examples

import org.apache.spark.sql.*
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.functions.*
import org.jetbrains.kotlinx.spark.api.*
import org.jetbrains.kotlinx.spark.api.tuples.*
import scala.Tuple2
import scala.collection.Seq


fun main() {
    sparkExample()
    smartNames()
    functionToUDF()
    strongTypingInDatasets()
    UDAF()
    varargUDFs()
}


/**
 * https://spark.apache.org/docs/latest/sql-ref-functions-udf-scalar.html
 * adapted directly for Kotlin:
 * */
private fun sparkExample(): Unit = withSpark {

    // Define and register a zero-argument non-deterministic UDF
    // UDF is deterministic by default, i.e. produces the same result for the same input.
    val random = udf(nondeterministic = true) { -> Math.random() }
    udf.register("random", random)
    spark.sql("SELECT random()").show()
    // +--------+
    // |random()|
    // +--------+
    // |xxxxxxxx|
    // +--------+

    // Define and register a one-argument UDF
    val plusOne = udf { x: Int -> x + 1 }
    udf.register("plusOne", plusOne)
    spark.sql("SELECT plusOne(5)").show()
    // +----------+
    // |plusOne(5)|
    // +----------+
    // |         6|
    // +----------+

    // Define a two-argument UDF and register it with Spark in one step
    udf.register("strLenKotlin") { str: String, int: Int -> str.length + int }
    spark.sql("SELECT strLenKotlin('test', 1)").show()
    // +---------------------+
    // |strLenKotlin(test, 1)|
    // +---------------------+
    // |                    5|
    // +---------------------+

    // UDF in a WHERE clause
    udf.register("oneArgFilter") { n: Long -> n > 5 }
    spark.range(1, 10).createOrReplaceTempView("test")
    spark.sql("SELECT * FROM test WHERE oneArgFilter(id)").show()
    // +---+
    // | id|
    // +---+
    // |  6|
    // |  7|
    // |  8|
    // |  9|
    // +---+
}

/**
 * Shows how Kotlin's UDF wrappers can carry a name which saves you time and errors.
 */
private fun smartNames(): Unit = withSpark {

    // remember the plusOne function from sparkExample?
    val plusOne = udf { x: Int -> x + 1 }
    udf.register("plusOne", plusOne)
    spark.sql("SELECT plusOne(5)").show()


    // As you can see, there is just too many places where "plusOne" is written and we developers are lazy
    // So, the Kotlin spark api introduces NamedUserDefinedFunctions!
    // The register call will no longer require a name and the name of the named udf will simply be used
    val plusOneNamed = udf("plusOneNamed") { x: Int -> x + 1 }
    udf.register(plusOneNamed)
    spark.sql("SELECT plusOneNamed(5)").show()


    // You can still supply a name at any moment to replace it
    udf.register("plusOneNamed1", plusOneNamed)
    udf.register(plusOneNamed.withName("plusOneNamed2"))


    // Finally, we can even use some Kotlin reflection magic to achieve the following
    // (myUdf.register() does the same as udf.register(), just a tiny bit shorter)
    val plusOneFinal by udf { x: Int -> x + 1 }
    plusOneFinal.register()
    spark.sql("SELECT plusOneFinal(5)").show()
//    +---------------+
//    |plusOneFinal(5)|
//    +---------------+
//    |              6|
//    +---------------+

}


/**
 * Shows how UDFs can be created from normal functions as well.
 */
private fun functionToUDF(): Unit = withSpark {

    // Say we want to convert a normal readable function to a UDF
    fun plusOne(x: Int) = x + 1

    // We can use reflection for that! And as you can see, we get a named udf as well
    val plusOneUDF: NamedUserDefinedFunction1<Int, Int> = udf(::plusOne)

    // This means we can even create and register this udf without any name explicitly supplied
    // in a single line!
    udf.register(::plusOne)
    spark.sql("SELECT plusOne(5)").show()
//    +----------+
//    |plusOne(5)|
//    +----------+
//    |         6|
//    +----------+

    // It also works for functions as lambda values:
    val minusOneUDF: NamedUserDefinedFunction1<Int, Int> = udf(::minusOne)

    // And as usual, you define a new name if you like:
    udf("newName", ::minusOne)
}

private val minusOne = { x: Int -> x - 1 }

/**
 * Shows how UDFs in Kotlin carry typing information, which allows you to do
 * typesafe column operations with them.
 */
private fun strongTypingInDatasets() = withSpark {
    data class User(val name: String, val age: Int?)
    val ds: Dataset<User> = dsOf(
        User("A", null),
        User("B", 23),
        User("C", 60),
        User("D", 14),
    ).showDS()
//    +----+----+
//    |name| age|
//    +----+----+
//    |   A|null|
//    |   B|  23|
//    |   C|  60|
//    |   D|  14|
//    +----+----+


    // UDFs can also be used, no registering needed, to perform operations on columns
    // using the Dataset API. UDFs are not as optimized as other Spark functions in terms of
    // raw performance, however, in return you get infinitely more versatility.
    // UDFs are usually executed using the [apply] method present in them, but,
    // of course, we had to Kotlin-ify those too, which means you can do:
    val replaceMissingAge = udf { age: Int?, value: Int -> age ?: value }

    val result1: Dataset<Tuple2<String, Int>> = ds.select(
        col(User::name), replaceMissingAge(col(User::age), typedLit(-1))
    ).showDS()
//    +----+------------+
//    |name|UDF(age, -1)|
//    +----+------------+
//    |   A|          -1|
//    |   B|          23|
//    |   C|          60|
//    |   D|          14|
//    +----+------------+



    // As you can see, the resulting dataset type is Tuple2<String, Int>
    // This is possible since we know what types go in and out of the replaceMissingAge udf.
    // We can thus provide TypedColumns instead of normal ones which the select function takes
    // advantage of!


    // NOTE: In UDFs, iterables, lists, arrays and such need to be represented as Seq
    val toJson by udf { age: Int, name: String, pets: Seq<String> ->
        """{ "age" : $age, "name" : "$name", "pets" : [${pets.asKotlinIterable().joinToString { "\"$it\"" }}] }"""
    }

    // Also when you are using Dataframes (untyped Datasets), you can still provide type hints for columns manually
    // if you want to receive type hints after calling the UDF
    val df: Dataset<Row> = dfOf(
        colNames = arrayOf("name", "age", "pets"),
        t("Alice", 12, emptyList()),
        t("Bob", 24, listOf("Dog", "Cat")),
        t("Charlie", 18, listOf("Bunny")),
    ).showDS()
//    +-------+---+----------+
//    |   name|age|      pets|
//    +-------+---+----------+
//    |  Alice| 12|        []|
//    |    Bob| 24|[Dog, Cat]|
//    |Charlie| 18|   [Bunny]|
//    +-------+---+----------+

    val result2 = df.select(
        toJson(
            col<_, Int>("age"),
            col<_, String>("name"),
            col<Row, List<String>>("pets").asSeq(),
//      or `col<_, Seq<String>>("pets")` if you want to be less strict
        )
    ).showDS(truncate = false)
//    +-------------------------------------------------------+
//    |toJson(age, name, pets)                                |
//    +-------------------------------------------------------+
//    |{ "age" : 12, "name" : "Alice", "pets" : [] }          |
//    |{ "age" : 24, "name" : "Bob", "pets" : ["Dog", "Cat"] }|
//    |{ "age" : 18, "name" : "Charlie", "pets" : ["Bunny"] } |
//    +-------------------------------------------------------+
}

data class Employee(val name: String, val salary: Long)
data class Average(var sum: Long, var count: Long)

private object MyAverage : Aggregator<Employee, Average, Double>() {
    // A zero value for this aggregation. Should satisfy the property that any b + zero = b

    override fun zero(): Average = Average(0L, 0L)

    // Combine two values to produce a new value. For performance, the function may modify `buffer`
    // and return it instead of constructing a new object
    override fun reduce(buffer: Average, employee: Employee): Average {
        buffer.sum += employee.salary
        buffer.count += 1L
        return buffer
    }

    // Merge two intermediate values
    override fun merge(b1: Average, b2: Average): Average {
        b1.sum += b2.sum
        b1.count += b2.count
        return b1
    }

    // Transform the output of the reduction
    override fun finish(reduction: Average): Double = reduction.sum.toDouble() / reduction.count

    // Specifies the Encoder for the intermediate value type
    override fun bufferEncoder(): Encoder<Average> = kotlinEncoderFor()

    // Specifies the Encoder for the final output value type
    override fun outputEncoder(): Encoder<Double> = kotlinEncoderFor()

}

/**
 * Shows how UDAFs can be used from Kotlin.
 */
private fun UDAF() = withSpark {
    // First let's go over the example from Spark for User defined aggregate functions:
    // https://spark.apache.org/docs/latest/sql-ref-functions-udf-aggregate.html
    // See above for Employee, Average, and MyAverage

    val ds: Dataset<Employee> = dsOf(
        Employee("Michael", 3000),
        Employee("Andy", 4500),
        Employee("Justin", 3500),
        Employee("Berta", 4000),
    ).showDS()
//    +-------+------+
//    |   name|salary|
//    +-------+------+
//    |Michael|  3000|
//    |   Andy|  4500|
//    | Justin|  3500|
//    |  Berta|  4000|
//    +-------+------+

    // Convert the function to a `TypedColumn` and give it a name
    val averageSalary: TypedColumn<Employee, Double> = MyAverage.toColumn().name("average_salary")
    val result1: Dataset<Double> = ds.select(averageSalary)
        .showDS()
//    +--------------+
//    |average_salary|
//    +--------------+
//    |        3750.0|
//    +--------------+

    // While this method can work on all columns of a Dataset, if we want to be able
    // to select the columns specifically, we need to convert MyAverage to a UDAF
    // Let's first create a new one with Long as input:
    val myAverage = aggregatorOf<Long, Average, Double>(
        zero = { Average(0L, 0L) },
        reduce = { buffer, it ->
            buffer.sum += it
            buffer.count += 1
            buffer
        },
        merge = { buffer, it ->
            buffer.sum += it.sum
            buffer.count += it.count
            buffer
        },
        finish = { it.sum.toDouble() / it.count },
    )

    // Now we need to define a name, otherwise it will default to "Aggregator", since that's
    // the name of the class `aggregatorOf` will implement and return.
    // We can register it again for SQL or call it directly in Dataset select
    val myAverageUdf = udaf("myAverage", myAverage).register()

    ds.createOrReplaceTempView("employees")
    spark.sql("""SELECT myAverage(salary) as average_salary from employees""")
        .showDS()
//    +--------------+
//    |average_salary|
//    +--------------+
//    |        3750.0|
//    +--------------+

    val result2: Dataset<Double> = ds.select(
        myAverageUdf(
            col(Employee::salary)
        ).name("average_salary")
    ).showDS()
//    +--------------+
//    |average_salary|
//    +--------------+
//    |        3750.0|
//    +--------------+

    // Finally, if you don't need an aggregator directly but just a udaf, you can use something like this:
    val udaf: UserDefinedFunction1<Long, Double> = udaf(
        zero = { Average(0L, 0L) },
        reduce = { buffer, it ->
            buffer.sum += it
            buffer.count += 1
            buffer
        },
        merge = { buffer, it ->
            buffer.sum += it.sum
            buffer.count += it.count
            buffer
        },
        finish = { it.sum.toDouble() / it.count },
    )

    // Or you can even register it right away (note a name is required)
    val registeredUdaf: NamedUserDefinedFunction1<Long, Double> = udf.register(
        name = "average",
        zero = { Average(0L, 0L) },
        reduce = { buffer, it ->
            buffer.sum += it
            buffer.count += 1
            buffer
        },
        merge = { buffer, it ->
            buffer.sum += it.sum
            buffer.count += it.count
            buffer
        },
        finish = { it.sum.toDouble() / it.count },
    )
}

/**
 * Shows the new and unique vararg UDFs the Kotlin Spark API has to offer and how to use them.
 */
private fun varargUDFs() = withSpark {
    // Finally, let's go over something unique to the Kotlin version of the Spark API: Simple Vararg UDFs

    // Wouldn't it be nice to convert a function like this into a UDF you can call with any number of columns?
    fun sumOf(vararg double: Double): Double = double.sum()
    // Well, why don't we try ;)

    // As you can see, we get a `NamedUserDefinedFunctionVararg`
    val sumUDF = udf.register(::sumOf)

    data class Values(val v1: Double, val v2: Double, val v3: Double, val v4: Double)
    val ds = dsOf(
        Values(1.0, 2.0, 3.0, 4.0),
        Values(4.0, 3.0, 2.0, 1.0),
        Values(1.0, 1.0, 1.0, 1.0),
    ).showDS()
//    +---+---+---+---+
//    | v1| v2| v3| v4|
//    +---+---+---+---+
//    |1.0|2.0|3.0|4.0|
//    |4.0|3.0|2.0|1.0|
//    |1.0|1.0|1.0|1.0|
//    +---+---+---+---+

    ds.createOrReplaceTempView("values")
    spark.sql("""SELECT sumOf(v1, v4), sumOf(), sumOf(v1, v2, v3, v4) FROM values""")
        .showDS()
//    +-------------+-------+---------------------+
//    |sumOf(v1, v4)|sumOf()|sumOf(v1, v2, v3, v4)|
//    +-------------+-------+---------------------+
//    |          5.0|    0.0|                 10.0|
//    |          5.0|    0.0|                 10.0|
//    |          2.0|    0.0|                  4.0|
//    +-------------+-------+---------------------+

    val result = ds.select(
        sumUDF(col(Values::v1), col(Values::v4)),
        sumUDF(),
        sumUDF(col(Values::v1), col(Values::v2), col(Values::v3), col(Values::v4)),
    ).showDS()
//    +-------------+-------+---------------------+
//    |sumOf(v1, v4)|sumOf()|sumOf(v1, v2, v3, v4)|
//    +-------------+-------+---------------------+
//    |          5.0|    0.0|                 10.0|
//    |          5.0|    0.0|                 10.0|
//    |          2.0|    0.0|                  4.0|
//    +-------------+-------+---------------------+


    // As you can see, it just works :), up to 22 parameters!
    // In fact, since UDFs don't support arrays (only scala's Seq), any udf that contains just an array
    // as parameter will become a vararg udf:
    udf.register("joinToString") { strings: Array<String> -> strings.joinToString(separator = "-") }
    spark.sql("""SELECT joinToString("a", "hi there", "test"), joinToString(), joinToString("b", "c")""")
        .showDS()
//    +-------------------------------+--------------+------------------+
//    |joinToString(a, hi there, test)|joinToString()|joinToString(b, c)|
//    +-------------------------------+--------------+------------------+
//    |                a-hi there-test|              |               b-c|
//    +-------------------------------+--------------+------------------+
}
