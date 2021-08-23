package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
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

                should("succeed when using three type udf and as result to udf return type") {
                    listOf("a" to 1, "b" to 2).toDS().toDF().createOrReplaceTempView("test1")
                    udf.register<String, Int, Int>("stringIntDiff") { a, b ->
                        a[0].toInt() - b
                    }
                    val result = spark.sql("select stringIntDiff(first, second) from test1").`as`<Int>().collectAsList()
                    result shouldBe listOf(96, 96)
                }
            }
        }

        context("calling the UDF-Wrapper") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {
                should("succeed when using the right number of arguments") {
                    val schema = DataTypes.createStructType(
                        listOf(
                            DataTypes.createStructField(
                                "textArray",
                                DataTypes.createArrayType(DataTypes.StringType),
                                false
                            ),
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

                    newData.select("text").collectAsList().zip(newData.select("textArray").collectAsList())
                        .forEach { (text, textArray) ->
                            assert(text.getString(0) == textArray.getList<String>(0).joinToString(" "))
                        }
                }
            }
        }

//        context("udf return data class") {
//            withSpark(logLevel = SparkLogLevel.DEBUG) {
//                should("return NormalClass") {
//                    listOf("a" to 1, "b" to 2).toDS().toDF().createOrReplaceTempView("test2")
//                    udf.register<String, Int, NormalClass>("toNormalClass") { a, b ->
//                       NormalClass(a,b)
//                    }
//                    spark.sql("select toNormalClass(first, second) from test2").show()
//                }
//            }
//        }

    }
})

data class NormalClass(
    val name: String,
    val age: Int
)