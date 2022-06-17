package org.jetbrains.kotlinx.spark.examples

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions.*
import org.jetbrains.kotlinx.spark.api.*
import scala.Tuple2


fun main() {
//    sparkExample()
//    smartNames()
//    functionToUDF()
    strongTypingInDatasets()
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
 * Shows how UDFs can be created from normal functions as well
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

    val result = ds.select(
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
    // This is possible since we know what types go in and our of the replaceMissingAge udf.
    // We can thus provide TypedColumns instead of normal ones which the select function takes
    // advantage of!

    // Also when you have no specific types




}

private fun varargUDFs() = withSpark {
    TODO()
}