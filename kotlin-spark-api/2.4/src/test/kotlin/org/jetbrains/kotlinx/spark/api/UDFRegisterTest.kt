/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 2.4+ (Scala 2.12)
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
package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.apache.spark.sql.Dataset
import org.junit.jupiter.api.assertThrows
import scala.collection.JavaConversions
import scala.collection.mutable.WrappedArray

@Suppress("unused")
private fun <T> scala.collection.Iterable<T>.asIterable(): Iterable<T> = JavaConversions.asJavaIterable(this)

@Suppress("unused")
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

                should("succeed when return a List") {
                    udf.register<String, List<Int>>("StringToIntList") { a ->
                        a.asIterable().map { it.code }
                    }

                    val result = spark.sql("select StringToIntList('ab')").`as`<List<Int>>().collectAsList()
                    result shouldBe listOf(listOf(97, 98))
                }

                should("succeed when using three type udf and as result to udf return type") {
                    listOf("a" to 1, "b" to 2).toDS().toDF().createOrReplaceTempView("test1")
                    udf.register<String, Int, Int>("stringIntDiff") { a, b ->
                        a[0].code - b
                    }
                    val result = spark.sql("select stringIntDiff(first, second) from test1").`as`<Int>().collectAsList()
                    result shouldBe listOf(96, 96)
                }
            }
        }

        context("calling the UDF-Wrapper") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {
                should("succeed call UDF-Wrapper in withColumn") {

                    val stringArrayMerger = udf.register<WrappedArray<String>, String>("stringArrayMerger") {
                        it.asIterable().joinToString(" ")
                    }

                    val testData = dsOf(listOf("a", "b"))
                    val newData = testData.withColumn("text", stringArrayMerger(testData.col("value")))

                    newData.select("text").collectAsList().zip(newData.select("value").collectAsList())
                        .forEach { (text, textArray) ->
                            assert(text.getString(0) == textArray.getList<String>(0).joinToString(" "))
                        }
                }


                should("succeed in dataset") {
                    val dataset: Dataset<NormalClass> = listOf(NormalClass("a", 10), NormalClass("b", 20)).toDS()

                    val udfWrapper = udf.register<String, Int, String>("nameConcatAge") { name, age ->
                        "$name-$age"
                    }

                    val collectAsList = dataset.withColumn(
                        "nameAndAge",
                        udfWrapper(dataset.col("name"), dataset.col("age"))
                    )
                        .select("nameAndAge")
                        .collectAsList()

                    collectAsList[0][0] shouldBe "a-10"
                    collectAsList[1][0] shouldBe "b-20"
                }
            }
        }

        // get the same exception with: https://forums.databricks.com/questions/13361/how-do-i-create-a-udf-in-java-which-return-a-compl.html
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
