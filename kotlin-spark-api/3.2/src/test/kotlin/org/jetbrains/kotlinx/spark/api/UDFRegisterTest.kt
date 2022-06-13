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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.apache.spark.sql.AnalysisException
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions.col
import org.junit.jupiter.api.assertThrows
import scala.collection.mutable.WrappedArray
import kotlin.random.Random

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
                        array.asKotlinIterable().joinToString(" ")
                    }
                }

                should("succeed when return a List") {
                    udf.register("StringToIntList") { a: String ->
                        a.asIterable().map { it.code }
                    }

                    val result = spark.sql("select StringToIntList('ab')").to<List<Int>>().collectAsList()
                    result shouldBe listOf(listOf(97, 98))
                }

                should("succeed when using three type udf and as result to udf return type") {
                    listOf("a" to 1, "b" to 2).toDS().toDF().createOrReplaceTempView("test1")
                    udf.register<String, Int, Int>("stringIntDiff") { a, b ->
                        a[0].code - b
                    }
                    val result = spark.sql("select stringIntDiff(first, second) from test1").to<Int>().collectAsList()
                    result shouldBe listOf(96, 96)
                }
            }
        }

        context("calling the UDF-Wrapper") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {
                should("succeed call UDF-Wrapper in withColumn") {

                    val stringArrayMerger = udf.register("stringArrayMerger") { it: WrappedArray<String> ->
                        it.asKotlinIterable().joinToString(" ")
                    }

                    val testData = dsOf(arrayOf("a", "b"))
                    val newData = testData.withColumn("text", stringArrayMerger(
                        testData.singleCol().asWrappedArray()
                    ))

                    (newData.select("text").collectAsList() zip newData.select("value").collectAsList())
                        .forEach { (text, textArray) ->
                            assert(text.getString(0) == textArray.getList<String>(0).joinToString(" "))
                        }
                }

                should("succeed call UDF-Wrapper by delegate in withColumn") {

                    val stringArrayMerger by udf { it: WrappedArray<String> ->
                        it.asKotlinIterable().joinToString(" ")
                    }

                    val testData = dsOf(listOf("a", "b"))
                    val newData = testData.withColumn("text", stringArrayMerger(testData("value")))

                    (newData.select("text").collectAsList() zip newData.select("value").collectAsList())
                        .forEach { (text, textArray) ->
                            assert(text.getString(0) == textArray.getList<String>(0).joinToString(" "))
                        }
                }

                should("succeed in dataset") {
                    val dataset: Dataset<NormalClass> = listOf(
                        NormalClass(name = "a", age = 10),
                        NormalClass(name = "b", age = 20)
                    ).toDS()

                    val nameConcatAge by udf { name: String, age: Int ->
                        "$name-$age"
                    }

                    val collectAsList = dataset.withColumn(
                        "nameAndAge",
                        nameConcatAge(
                            col(NormalClass::name),
                            col(NormalClass::age)
                        )
                    )
                        .select("nameAndAge")
                        .collectAsList()

                    collectAsList[0][0] shouldBe "a-10"
                    collectAsList[1][0] shouldBe "b-20"
                }
            }
        }

        context("non deterministic") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {
                should("allow udfs to be non deterministic") {
                    udf.register("random", nondeterministic = true) { ->
                        Random.nextInt()
                    }

                    val a = spark.sql("SELECT random()")
                        .select(col<_, Int>("random()"))
                        .takeAsList(1)
                        .single()
                    val b = spark.sql("SELECT random()")
                        .select(col<_, Int>("random()"))
                        .takeAsList(1)
                        .single()

                    a shouldNotBe b
                }

                should("allow udfs to be non deterministic using delegate") {

                    val random by udf(nondeterministic = true) { -> Random.nextInt() }

                    val executed = random<Any?>()

                    val map = udf.register(name = "map", func = { it: Int -> "$it yay" })

                    val a = dsOf(1)
                        .selectTyped(random())
                        .takeAsList(1)
                        .single()

                    val b = dsOf(1)
                        .selectTyped(random())
                        .selectTyped(map(col("*").typed()))
                        .showDS()
                        .takeAsList(1)
                        .single()

                    a shouldNotBe b
                }
            }
        }

        context("non nullable") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {

                should("allow udfs to be non nullable") {
                    udf.register<Int?>("test") { ->
                        null
                    }

                    spark.sql("SELECT test()")
                        .select(col<_, Int?>("test()"))
                        .showDS()
                        .takeAsList(1)
                        .single()

                }

                should("allow udfs to be non nullable using delegate") {
                    val test by udf<Int?> { -> null }

                    // access it once
                    test.register()

                    spark.sql("SELECT test()")
                        .select(col<_, Int?>("test()"))
                        .showDS()
                        .takeAsList(1)
                        .single()

                }
            }
        }

        context("udf return data class") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {
                should("return NormalClass") {
                    listOf("a" to 1, "b" to 2).toDS().toDF().createOrReplaceTempView("test2")
                    udf.register<String, Int, NormalClass>("toNormalClass") { a, b ->
                        NormalClass(b, a)
                    }
                    spark.sql("select toNormalClass(first, second) from test2").show()
                }

                should("not return NormalClass using unaccessed by delegate") {
                    listOf("a" to 1, "b" to 2).toDS().toDF().createOrReplaceTempView("test2")
                    val toNormalClass2 by udf { a: String, b: Int ->
                        NormalClass(b, a)
                    }
                    toNormalClass2.register()

                    shouldThrow<AnalysisException> { // toNormalClass2 is never accessed, so the delegate getValue function is not executed
                        spark.sql("select toNormalClass2(first, second) from test2").show()
                    }
                }

                should("return NormalClass using accessed by delegate") {
                    listOf("a" to 1, "b" to 2).toDS().toDF().createOrReplaceTempView("test2")
                    val toNormalClass3 by udf { a: String, b: Int ->
                        NormalClass(b, a)
                    }
                    toNormalClass3.register()
                    spark.sql("select toNormalClass3(first, second) from test2").show()
                }
            }
        }

    }
})


data class NormalClass(
    val age: Int,
    val name: String
)
