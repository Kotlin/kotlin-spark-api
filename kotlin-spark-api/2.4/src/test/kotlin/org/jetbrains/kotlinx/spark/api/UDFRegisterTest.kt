package org.jetbrains.kotlinx.spark.api

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.verbs.expect
import ch.tutteli.atrium.domain.builders.migration.asExpect
import io.kotest.core.spec.style.ShouldSpec
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.types.DataTypes
import org.junit.jupiter.api.assertThrows
import scala.collection.JavaConversions
import scala.collection.mutable.WrappedArray

private fun <T> scala.collection.Iterable<T>.asIterable(): Iterable<T> = JavaConversions.asJavaIterable(this)

class UDFRegisterTest : ShouldSpec({
    context("org.jetbrains.kotlinx.spark.api.UDFRegister") {
        context("the function checkForValidType") {
            val invalidTypes = listOf(
                    Array::class,
                    Iterable::class,
                    List::class,
                    MutableList::class,
                    ByteArray::class,
                    CharArray::class,
                    ShortArray::class,
                    IntArray::class,
                    LongArray::class,
                    FloatArray::class,
                    DoubleArray::class,
                    BooleanArray::class,
                    Map::class,
                    MutableMap::class,
                    Set::class,
                    MutableSet::class,
                    arrayOf("")::class,
                    listOf("")::class,
                    setOf("")::class,
                    mapOf("" to "")::class,
                    mutableListOf("")::class,
                    mutableSetOf("")::class,
                    mutableMapOf("" to "")::class,
            )
            invalidTypes.forEachIndexed { index, invalidType ->
                should("$index: throw an ${TypeOfUDFParameterNotSupportedException::class.simpleName} when encountering ${invalidType.qualifiedName}") {
                    assertThrows<TypeOfUDFParameterNotSupportedException> {
                        invalidType.checkForValidType("test")
                    }
                }
            }
        }

        context("the register-function") {
            withSpark {

                should("fail when using a simple kotlin.Array") {
                    assertThrows<TypeOfUDFParameterNotSupportedException> {
                        udf.register("shouldFail") { array: Array<String> ->
                            array.joinToString(" ")
                        }
                    }
                }

                should("succeed when using a WrappedArray") {
                    udf.register("shouldSucceed") { array: WrappedArray<String> ->
                        array.asIterable().joinToString(" ")
                    }
                }
            }
        }

        context("calling the UDF-Wrapper") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {
                should("succeed when using the right number of arguments") {
                    val schema = DataTypes.createStructType(
                            listOf(
                                    DataTypes.createStructField("textArray", DataTypes.createArrayType(DataTypes.StringType), false),
                                    DataTypes.createStructField("id", DataTypes.StringType, false)
                            )
                    )

                    val rows = listOf(
                            RowFactory.create(arrayOf("a", "b", "c"), "1"),
                            RowFactory.create(arrayOf("d", "e", "f"), "2"),
                            RowFactory.create(arrayOf("g", "h", "i"), "3"),
                    )

                    val testData = spark.createDataFrame(rows, schema)

                    val stringArrayMerger = udf.register<WrappedArray<String>, String>("stringArrayMerger") {
                        it.asIterable().joinToString(" ")
                    }

                    val newData = testData.withColumn("text", stringArrayMerger(testData.col("textArray")))

                    newData.select("text").collectAsList().zip(newData.select("textArray").collectAsList()).forEach { (text, textArray) ->
                        assert(text.getString(0) == textArray.getList<String>(0).joinToString(" "))
                    }
                }
//                should("also work with datasets") {
//                    val ds = listOf("a" to 1, "b" to 2).toDS()
//                    val stringIntDiff = udf.register<String, Int, Arity1<Int>>("stringIntDiff") { a, b ->
//                        c(a[0].toInt() - b)
//                    }
//                    val lst = ds.withColumn("new", stringIntDiff(ds.col("first"), ds.col("second")))
//                        .select("new")
//                        .collectAsList()
//
////                    val result = spark.sql("select stringIntDiff(first, second) from test1").`as`<Int>().collectAsList()
////                    expect(result).asExpect().contains.inOrder.only.values(96, 96)
//                }
                should("also work with datasets") {
                    listOf("a" to 1, "b" to 2).toDS().toDF().createOrReplaceTempView("test1")
                    udf.register<String, Int, Int>("stringIntDiff") { a, b ->
                        a[0].toInt() - b
                    }
                    spark.sql("select stringIntDiff(first, second) from test1").show()

                }
            }
        }
    }
})