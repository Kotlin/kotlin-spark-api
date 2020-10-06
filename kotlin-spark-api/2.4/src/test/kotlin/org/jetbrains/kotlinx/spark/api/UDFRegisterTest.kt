package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.types.DataTypes
import org.junit.jupiter.api.assertThrows
import scala.collection.JavaConversions
import scala.collection.JavaConverters
import scala.collection.mutable.WrappedArray

class UDFRegisterTest : ShouldSpec({
    context("org.jetbrains.kotlinx.spark.api.UDFRegister") {
        context("the function checkForValidType"){
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
                should("$index: throw an ${TypeOfUDFParameterNotSupportedException::class.simpleName} when encountering ${invalidType.qualifiedName}"){
                    assertThrows<TypeOfUDFParameterNotSupportedException> {
                        invalidType.checkForValidType("test")
                    }
                }
            }
        }

        context("the register-function"){
            withSpark {

                should("fails when using a simple kotlin.Array"){
                    assertThrows<TypeOfUDFParameterNotSupportedException> {
                        udf().register("shouldFail", DataTypes.StringType) { array: Array<String> ->
                            array.joinToString(" ")
                        }
                    }
                }

                should("succeeds when using a WrappedArray"){
                    udf().register("shouldSucceed", DataTypes.StringType){
                        array: WrappedArray<String> ->
                        array.asIterable().joinToString(" ")
                    }
                }
            }
        }

        context("calling the UDF-Wrapper"){
            withSpark {
                val schema  = DataTypes.createStructType(
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

                val stringArrayMerger = udf().register("stringArrayMerger", DataTypes.StringType){
                    array: WrappedArray<String> ->
                    array.asIterable().joinToString(" ")
                }

                should("succeeds when using the right number of arguments"){

                    val newData = testData.withColumn("text", stringArrayMerger(testData.col("textArray")))

                    newData.select("text").collectAsList().zip(newData.select("textArray").collectAsList()).forEach {
                        (text, textArray) ->
                        assert(text.getString(0) == textArray.getList<String>(0).joinToString(" "))
                    }
                }
            }
        }
    }
})