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
@file:Suppress("SqlNoDataSourceInspection")

package org.jetbrains.kotlinx.spark.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beOfType
import org.apache.spark.sql.AnalysisException
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Encoder
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.Aggregator
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.assertThrows
import scala.collection.Seq
import scala.collection.mutable.WrappedArray
import java.io.Serializable
import kotlin.random.Random

@Suppress("unused")
class UDFTest : ShouldSpec({

    context("UDF tests") {
        context("the function checkForValidType") {
            val invalidTypes = listOf(
                Char::class,
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

        context("Test all possible notations") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {
                should("Support official spark notation") {

                    val random = udf(nondeterministic = true) { -> Math.random() }
                    spark.udf().register("random", random)
                    spark.sql("SELECT random()").show()


                    val plusOne = udf { x: Int -> x + 1 }
                    spark.udf().register("plusOne", plusOne)
                    spark.sql("SELECT plusOne(5)").show()

                    spark.udf().register("strLenKotlin") { str: String, int: Int -> str.length + int }
                    spark.sql("SELECT strLenKotlin('test', 1)").show()

                    spark.udf().register("oneArgFilter") { n: Long -> n > 5 }
                    spark.range(1, 10).createOrReplaceTempView("test")
                    spark.sql("SELECT * FROM test WHERE oneArgFilter(id)").show()
                }

                should("Have named UDFs which do not require a name for registering") {
                    val plusOne = udf("plusOne1") { x: Int -> x + 1 }
                    udf.register(plusOne)
                    spark.sql("SELECT plusOne1(5)").show()
                }

                should("Have named UDFs which do not require a name for registering (other register option)") {
                    val plusOne = udf("plusOne2") { x: Int -> x + 1 }
                    plusOne.register()
                    spark.sql("SELECT plusOne2(5)").show()
                }

                should("Allow named udf to register with different name") {
                    val plusOne = udf("plusOne") { x: Int -> x + 1 }
                    plusOne.name shouldBe "plusOne"

                    val plusOne3 = udf.register("plusOne3", plusOne)
                    plusOne3.name shouldBe "plusOne3"

                    spark.sql("SELECT plusOne3(5)").show()
                }

                should("Allow named udf to register with different name (other register option)") {
                    val plusOne = udf("plusOne") { x: Int -> x + 1 }
                    plusOne.name shouldBe "plusOne"

                    val plusOne3 = plusOne.register("plusOne3a")
                    plusOne3.name shouldBe "plusOne3a"

                    spark.sql("SELECT plusOne3a(5)").show()
                }

                should("Only register newest name") {
                    fun plusOne(x: Int) = x + 1

                    val plusOneUdf = udf(::plusOne)
                    plusOneUdf.name shouldBe "plusOne"

                    val plusOne0 = udf("plusOne0", ::plusOne)
                    plusOne0.name shouldBe "plusOne0"

                    val plusOne1 = plusOne0.withName("plusOne1")
                    plusOne1.name shouldBe "plusOne1"

                    val plusOne2 by plusOne1
                    plusOne2.name shouldBe "plusOne2"

                    val plusOne3 by plusOne2
                    plusOne3.name shouldBe "plusOne3"

                    val plusOne4 = udf.register("plusOne4", plusOne3)
                    plusOne4.name shouldBe "plusOne4"

                    val plusOne5 = plusOne4.register("plusOne5")
                    plusOne5.name shouldBe "plusOne5"

                    spark.sql("SELECT plusOne4(5)").show()
                    spark.sql("SELECT plusOne5(5)").show()
                }

                should("Allow udf to be registered from function ref") {
                    fun addTwo(x: Int, y: Int) = x + y

                    val addTwo = udf.register(::addTwo)
                    addTwo.name shouldBe "addTwo"

                    val addTwo1 = udf.register("addTwo1", ::addTwo)
                    addTwo1.name shouldBe "addTwo1"

                    spark.sql("SELECT addTwo(1, 2)")
                    spark.sql("SELECT addTwo1(1, 2)")

                }

                should("Allow udf to be registered from property function ref") {
                    val addTwo = udf.register(::addTwoConst)
                    addTwo.name shouldBe "addTwoConst"

                    val addTwo1 = udf.register("addTwoConst1", ::addTwoConst)
                    addTwo1.name shouldBe "addTwoConst1"

                    spark.sql("SELECT addTwoConst(1, 2)")
                    spark.sql("SELECT addTwoConst1(1, 2)")
                }
            }
        }

        context("the register-function") {
            withSpark {

                should("fail when using a simple kotlin.Array") {
                    assertThrows<TypeOfUDFParameterNotSupportedException> {
                        udf.register("shouldFail", func = { array: Array<String> ->
                            array.joinToString(" ")
                        })
                    }

                    assertThrows<TypeOfUDFParameterNotSupportedException> {
                        udf(func = { array: Array<String> ->
                            array.joinToString(" ")
                        })
                    }
                }

                should("succeed when using a WrappedArray") {
                    udf.register("shouldSucceed") { array: WrappedArray<String> ->
                        array.asKotlinIterable().joinToString(" ")
                    }
                }

                //#if scala.compat.version <= 2.12
                //$should("succeed when using a WrappedArray") {
                //$    udf.register("shouldSucceed") { array: scala.collection.mutable.WrappedArray<String> ->
                //$        array.asKotlinIterable().joinToString(" ")
                //$    }
                //$}
                //#endif

                should("succeed when using a Seq") {
                    udf.register("shouldSucceed") { array: Seq<String> ->
                        array.asKotlinIterable().joinToString(" ")
                    }
                }

                should("succeed when return a List") {
                    udf.register("StringToIntList") { a: String ->
                        a.asIterable().map { it.code }
                    }

                    @Language("SQL")
                    val result = spark.sql("SELECT StringToIntList('ab')").to<List<Int>>().collectAsList()
                    result shouldBe listOf(listOf(97, 98))
                }

                should("succeed when using three type udf and as result to udf return type") {
                    listOf("a" to 1, "b" to 2).toDS().toDF().createOrReplaceTempView("test1")

                    fun stringIntDiff(a: String, b: Int) = a[0].code - b
                    udf.register(::stringIntDiff)

                    @Language("SQL")
                    val result = spark.sql("SELECT stringIntDiff(first, second) FROM test1").to<Int>().collectAsList()
                    result shouldBe listOf(96, 96)
                }
            }
        }

        context("calling the UDF-Wrapper") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {

                //#if scala.compat.version <= 2.12
                //$should("succeed in withColumn with WrappedArray") {
                //$
                //$    val stringArrayMerger = udf { it: scala.collection.mutable.WrappedArray<String> ->
                //$        it.asKotlinIterable().joinToString(" ")
                //$    }
                //$
                //$    val testData = dsOf(arrayOf("a", "b"))
                //$    val newData = testData.withColumn(
                //$        "text",
                //$        stringArrayMerger(
                //$            testData.singleCol().typed()
                //$        ),
                //$    )
                //$
                //$    (newData.select("text").collectAsList() zip newData.select("value").collectAsList())
                //$        .forEach { (text, textArray) ->
                //$            assert(text.getString(0) == textArray.getList<String>(0).joinToString(" "))
                //$        }
                //$}
                //#endif

                should("succeed in withColumn using Seq") {

                    val stringArrayMerger = udf { it: Seq<String> ->
                        it.asKotlinIterable().joinToString(" ")
                    }

                    val testData = dsOf(arrayOf("a", "b"))
                    val newData = testData.withColumn(
                        "text",
                        stringArrayMerger(
                            testData.singleCol().asSeq()
                        ),
                    )

                    (newData.select("text").collectAsList() zip newData.select("value").collectAsList())
                        .forEach { (text, textArray) ->
                            assert(text.getString(0) == textArray.getList<String>(0).joinToString(" "))
                        }
                }

                should("succeed in dataset") {
                    val dataset = listOf(
                        NormalClass(name = "a", age = 10),
                        NormalClass(name = "b", age = 20),
                    ).toDS()

                    val nameConcatAge by udf { name: String, age: Int ->
                        "$name-$age"
                    }

                    val ds = dataset.select(
                        nameConcatAge(
                            col(NormalClass::name),
                            col(NormalClass::age),
                        )
                    )
                    ds should beOfType<Dataset<String>>()

                    "${nameConcatAge.name}(${NormalClass::name.name}, ${NormalClass::age.name})" shouldBe ds.columns().single()

                    val collectAsList = ds.collectAsList()
                    collectAsList[0] shouldBe "a-10"
                    collectAsList[1] shouldBe "b-20"
                }

                should("Return Dataset<Row> if types are not adhered to") {
                    val dataset = listOf(
                        NormalClass(name = "a", age = 10),
                        NormalClass(name = "b", age = 20),
                    ).toDS()

                    val nameConcatAge by udf { name: String, age: Int ->
                        "$name-$age"
                    }

                    val ds = dataset.select(
                        nameConcatAge(
                            col(NormalClass::name),
                            col(NormalClass::age).typed<_, Int?>(),
                        )
                    )
                    ds should beOfType<Dataset<Row>>()

                    "${nameConcatAge.name}(${NormalClass::name.name}, ${NormalClass::age.name})" shouldBe ds.columns().single()

                    val collectAsList = ds.collectAsList()
                    collectAsList[0].getAs<String>(0) shouldBe "a-10"
                    collectAsList[1].getAs<String>(0) shouldBe "b-20"
                }

                should("Return Dataset<Row> if using invokeUntyped") {
                    val dataset = listOf(
                        NormalClass(name = "a", age = 10),
                        NormalClass(name = "b", age = 20),
                    ).toDS()

                    val nameConcatAge by udf { name: String, age: Int ->
                        "$name-$age"
                    }

                    val ds = dataset.select(
                        nameConcatAge.invokeUntyped(
                            col(NormalClass::name),
                            col(NormalClass::age),
                        )
                    )
                    ds should beOfType<Dataset<Row>>()

                    "${nameConcatAge.name}(${NormalClass::name.name}, ${NormalClass::age.name})" shouldBe ds.columns().single()

                    val collectAsList = ds.collectAsList()
                    collectAsList[0].getAs<String>(0) shouldBe "a-10"
                    collectAsList[1].getAs<String>(0) shouldBe "b-20"
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

                    udf.register("toNormalClass") { name: String, age: Int ->
                        NormalClass(age, name)
                    }
                    spark.sql("select toNormalClass(first, second) from test2").show()
                }

                should("not return NormalClass when not registered") {
                    listOf(1 to "a", 2 to "b").toDS().toDF().createOrReplaceTempView("test2")

                    val toNormalClass2 = udf("toNormalClass2", ::NormalClass)

                    shouldThrow<AnalysisException> {
                        spark.sql("select toNormalClass2(first, second) from test2").show()
                    }
                }

                should("return NormalClass using accessed by delegate") {
                    listOf(1 to "a", 2 to "b").toDS().toDF().createOrReplaceTempView("test2")
                    val toNormalClass3 = udf("toNormalClass3", ::NormalClass)
                    toNormalClass3.register()

                    spark.sql("select toNormalClass3(first, second) from test2").show()
                }
            }
        }

    }

    context("UDAF tests") {

        context("Test all notations") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {

                should("Support Spark notation") {
                    val ds = dsOf(
                        Employee("Michael", 3000),
                        Employee("Andy", 4500),
                        Employee("Justin", 3500),
                        Employee("Berta", 4000),
                    )

                    // Convert the function to a `TypedColumn` and give it a name
                    val averageSalary = MyAverage.toColumn().name("average_salary")
                    val result = ds.select(averageSalary)

                    result.collectAsList().single() shouldBe 3750.0
                }

                should("Support all udaf creation methods") {
                    val a = udaf(MyAverage)
                    a.name shouldBe "MyAverage"

                    val b = udaf("myAverage", MyAverage)
                    b.name shouldBe "myAverage"

                    val c = udafUnnamed(MyAverage)
                    c should beOfType<UserDefinedFunction1<Employee, Double>>()

                    val d = udaf(aggregator)
                    d.name shouldBe "Aggregator"

                    val e = object : Aggregator<Long, Average, Double>(), Serializable {
                        override fun zero() = Average(0L, 0L)
                        override fun reduce(buffer: Average, it: Long) =
                            buffer.apply { sum += it; count += 1 }

                        override fun merge(buffer: Average, it: Average) =
                            buffer.apply { sum += it.sum; count += it.count }

                        override fun finish(it: Average) = it.sum.toDouble() / it.count
                        override fun bufferEncoder() = encoder<Average>()
                        override fun outputEncoder() = encoder<Double>()
                    }

                    shouldThrow<IllegalStateException> {
                        // cannot get name of an unnamed object
                        udaf(e)
                    }
                    // should use instead
                    udafUnnamed(e)
                    // or
                    udaf("someName", e)


                    val f = udaf<Long, Average, Double>(
                        zero = { Average(0L, 0L) },
                        reduce = applyFun {
                            sum += it
                            count += 1
                        },
                        merge = applyFun {
                            sum += it.sum
                            count += it.count
                        },
                        finish = { it.sum.toDouble() / it.count },
                        nondeterministic = false,
                    )
                    f should beOfType<UserDefinedFunction1<Long, Double>>()

                    val g = udaf<Long, Average, Double>(
                        name = "g",
                        zero = { Average(0L, 0L) },
                        reduce = applyFun {
                            sum += it
                            count += 1
                        },
                        merge = applyFun {
                            sum += it.sum
                            count += it.count
                        },
                        finish = { it.sum.toDouble() / it.count },
                        nondeterministic = false,
                    )
                    g.name shouldBe "g"
                }
            }
        }

        context("Registering") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {

                val ds = dsOf(
                    Employee("Michael", 3000),
                    Employee("Andy", 4500),
                    Employee("Justin", 3500),
                    Employee("Berta", 4000),
                )
                ds.createOrReplaceTempView("employees")

                should("Support registering udafs from Aggregator") {
                    val a = udaf("myAverage", aggregator).register()
                    a.name shouldBe "myAverage"

                    @Language("SQL")
                    val result = spark.sql("SELECT myAverage(salary) as average_salary FROM employees")
                        .to<Double>()
                    result.collectAsList().single() shouldBe 3750.0
                }

                should("Support registering udafs from Aggregator alternative") {
                    val a = udf.register("myAverage0", aggregator)
                    a.name shouldBe "myAverage0"

                    @Language("SQL")
                    val result = spark.sql("SELECT myAverage0(salary) as average_salary FROM employees")
                        .to<Double>()
                    result.collectAsList().single() shouldBe 3750.0
                }

                should("Support registering udaf in place") {
                    val a = udf.register<Long, Average, Double>(
                        name = "myAverage1",
                        zero = { Average(0L, 0L) },
                        reduce = applyFun {
                            sum += it
                            count += 1
                        },
                        merge = applyFun {
                            sum += it.sum
                            count += it.count
                        },
                        finish = { it.sum.toDouble() / it.count },
                    )
                    a.name shouldBe "myAverage1"

                    @Language("SQL")
                    val result = spark.sql("SELECT myAverage1(salary) as average_salary FROM employees")
                        .to<Double>()
                    result.collectAsList().single() shouldBe 3750.0
                }
            }
        }

        context("Dataset select") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {
                val ds = dsOf(
                    Employee("Michael", 3000),
                    Employee("Andy", 4500),
                    Employee("Justin", 3500),
                    Employee("Berta", 4000),
                )

                should("Allow unnamed UDAFs to work with datasets") {
                    val myAverage = udafUnnamed(
                        object : Aggregator<Long, Average, Double>(), Serializable {
                            override fun zero() = Average(0L, 0L)
                            override fun reduce(buffer: Average, it: Long) =
                                buffer.apply { sum += it; count += 1 }

                            override fun merge(buffer: Average, it: Average) =
                                buffer.apply { sum += it.sum; count += it.count }

                            override fun finish(it: Average) = it.sum.toDouble() / it.count
                            override fun bufferEncoder() = encoder<Average>()
                            override fun outputEncoder() = encoder<Double>()
                        }
                    )

                    val result = ds.select(
                        myAverage(col(Employee::salary))
                    ).showDS()

                    "(${Employee::salary.name})" shouldBe result.columns().single()
                    result should beOfType<Dataset<Double>>()
                    result.collectAsList().single() shouldBe 3750.0
                }

                should("Allow named UDAFs to work with datasets") {
                    val myAverage = udaf(aggregator)

                    val result = ds.select(
                        myAverage(col(Employee::salary))
                    ).showDS()

                    "${myAverage.name.lowercase()}(${Employee::salary.name})" shouldBe result.columns().single()
                    result should beOfType<Dataset<Double>>()
                    result.collectAsList().single() shouldBe 3750.0
                }



            }
        }

    }

    context("vararg UDF tests") {
        fun firstByte(vararg a: Byte) = a.firstOrNull()
        fun firstShort(vararg a: Short) = a.firstOrNull()
        fun firstInt(vararg a: Int) = a.firstOrNull()
        fun firstLong(vararg a: Long) = a.firstOrNull()
        fun firstFloat(vararg a: Float) = a.firstOrNull()
        fun firstDouble(vararg a: Double) = a.firstOrNull()
        fun firstBoolean(vararg a: Boolean) = a.firstOrNull()
        fun firstString(vararg a: String) = a.firstOrNull()


        context("Creating Vararg UDF") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {

                should("Create Byte vararg udf") {
                    udf(::firstByte).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Byte, Byte?>>()
                        it.name shouldBe "firstByte"
                    }
                    udf("test", ::firstByte).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Byte, Byte?>>()
                        it.name shouldBe "test"
                    }
                    udf(::firstByteVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Byte, Byte?>>()
                        it.name shouldBe "firstByteVal"
                    }
                    udf("test", ::firstByteVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Byte, Byte?>>()
                        it.name shouldBe "test"
                    }
                    udf { a: ByteArray -> a.firstOrNull() }.let {
                        it should beOfType<UserDefinedFunctionVararg<Byte, Byte?>>()
                    }
                    udf("test") { a: ByteArray -> a.firstOrNull() }.let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Byte, Byte?>>()
                        it.name shouldBe "test"
                    }
                }

                should("Create Short vararg udf") {
                    udf(::firstShort).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Short, Short?>>()
                        it.name shouldBe "firstShort"
                    }
                    udf("test", ::firstShort).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Short, Short?>>()
                        it.name shouldBe "test"
                    }
                    udf(::firstShortVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Short, Short?>>()
                        it.name shouldBe "firstShortVal"
                    }
                    udf("test", ::firstShortVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Short, Short?>>()
                        it.name shouldBe "test"
                    }
                    udf { a: ShortArray -> a.firstOrNull() }.let {
                        it should beOfType<UserDefinedFunctionVararg<Short, Short?>>()
                    }
                    udf("test") { a: ShortArray -> a.firstOrNull() }.let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Short, Short?>>()
                        it.name shouldBe "test"
                    }
                }

                should("Create Int vararg udf") {
                    udf(::firstInt).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Int, Int?>>()
                        it.name shouldBe "firstInt"
                    }
                    udf("test", ::firstInt).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Int, Int?>>()
                        it.name shouldBe "test"
                    }
                    udf(::firstIntVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Int, Int?>>()
                        it.name shouldBe "firstIntVal"
                    }
                    udf("test", ::firstIntVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Int, Int?>>()
                        it.name shouldBe "test"
                    }
                    udf { a: IntArray -> a.firstOrNull() }.let {
                        it should beOfType<UserDefinedFunctionVararg<Int, Int?>>()
                    }
                    udf("test") { a: IntArray -> a.firstOrNull() }.let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Int, Int?>>()
                        it.name shouldBe "test"
                    }
                }

                should("Create Long vararg udf") {
                    udf(::firstLong).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Long, Long?>>()
                        it.name shouldBe "firstLong"
                    }
                    udf("test", ::firstLong).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Long, Long?>>()
                        it.name shouldBe "test"
                    }
                    udf(::firstLongVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Long, Long?>>()
                        it.name shouldBe "firstLongVal"
                    }
                    udf("test", ::firstLongVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Long, Long?>>()
                        it.name shouldBe "test"
                    }
                    udf { a: LongArray -> a.firstOrNull() }.let {
                        it should beOfType<UserDefinedFunctionVararg<Long, Long?>>()
                    }
                    udf("test") { a: LongArray -> a.firstOrNull() }.let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Long, Long?>>()
                        it.name shouldBe "test"
                    }
                }

                should("Create Float vararg udf") {
                    udf(::firstFloat).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Float, Float?>>()
                        it.name shouldBe "firstFloat"
                    }
                    udf("test", ::firstFloat).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Float, Float?>>()
                        it.name shouldBe "test"
                    }
                    udf(::firstFloatVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Float, Float?>>()
                        it.name shouldBe "firstFloatVal"
                    }
                    udf("test", ::firstFloatVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Float, Float?>>()
                        it.name shouldBe "test"
                    }
                    udf { a: FloatArray -> a.firstOrNull() }.let {
                        it should beOfType<UserDefinedFunctionVararg<Float, Float?>>()
                    }
                    udf("test") { a: FloatArray -> a.firstOrNull() }.let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Float, Float?>>()
                        it.name shouldBe "test"
                    }
                }

                should("Create Double vararg udf") {
                    udf(::firstDouble).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Double, Double?>>()
                        it.name shouldBe "firstDouble"
                    }
                    udf("test", ::firstDouble).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Double, Double?>>()
                        it.name shouldBe "test"
                    }
                    udf(::firstDoubleVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Double, Double?>>()
                        it.name shouldBe "firstDoubleVal"
                    }
                    udf("test", ::firstDoubleVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Double, Double?>>()
                        it.name shouldBe "test"
                    }
                    udf { a: DoubleArray -> a.firstOrNull() }.let {
                        it should beOfType<UserDefinedFunctionVararg<Double, Double?>>()
                    }
                    udf("test") { a: DoubleArray -> a.firstOrNull() }.let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Double, Double?>>()
                        it.name shouldBe "test"
                    }
                }

                should("Create Boolean vararg udf") {
                    udf(::firstBoolean).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Boolean, Boolean?>>()
                        it.name shouldBe "firstBoolean"
                    }
                    udf("test", ::firstBoolean).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Boolean, Boolean?>>()
                        it.name shouldBe "test"
                    }
                    udf(::firstBooleanVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Boolean, Boolean?>>()
                        it.name shouldBe "firstBooleanVal"
                    }
                    udf("test", ::firstBooleanVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Boolean, Boolean?>>()
                        it.name shouldBe "test"
                    }
                    udf { a: BooleanArray -> a.firstOrNull() }.let {
                        it should beOfType<UserDefinedFunctionVararg<Boolean, Boolean?>>()
                    }
                    udf("test") { a: BooleanArray -> a.firstOrNull() }.let {
                        it should beOfType<NamedUserDefinedFunctionVararg<Boolean, Boolean?>>()
                        it.name shouldBe "test"
                    }
                }

                should("Create Any vararg udf") {
                    udf(::firstString).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<String, String?>>()
                        it.name shouldBe "firstString"
                    }
                    udf("test", ::firstString).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<String, String?>>()
                        it.name shouldBe "test"
                    }
                    udf(::firstStringVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<String, String?>>()
                        it.name shouldBe "firstStringVal"
                    }
                    udf("test", ::firstStringVal).let {
                        it should beOfType<NamedUserDefinedFunctionVararg<String, String?>>()
                        it.name shouldBe "test"
                    }
                    udf { a: Array<String> -> a.firstOrNull() }.let {
                        it should beOfType<UserDefinedFunctionVararg<String, String?>>()
                    }
                    udf("test") { a: Array<String> -> a.firstOrNull() }.let {
                        it should beOfType<NamedUserDefinedFunctionVararg<String, String?>>()
                        it.name shouldBe "test"
                    }
                }
            }
        }

        context("Call vararg udf from sql") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {
                should("with Bytes") {
                    val value = 1.toByte()
                    udf.register(::firstByte)

                    spark.sql("select firstByte()")
                        .collectAsList()
                        .single()
                        .getAs<Byte?>(0) shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { value }
                        spark.sql("select firstByte(" + values.joinToString() + ")")
                            .collectAsList()
                            .single()
                            .getAs<Byte?>(0) shouldBe value
                    }

                    val values = (1..23).map { value }
                    shouldThrow<scala.MatchError> {
                        spark.sql("select firstByte(" + values.joinToString() + ")")
                    }
                }

                should("with Shorts") {
                    val value = 1.toShort()
                    udf.register(::firstShort)

                    spark.sql("select firstShort()")
                        .collectAsList()
                        .single()
                        .getAs<Short?>(0) shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { value }
                        spark.sql("select firstShort(" + values.joinToString() + ")")
                            .collectAsList()
                            .single()
                            .getAs<Short?>(0) shouldBe value
                    }

                    val values = (1..23).map { value }
                    shouldThrow<scala.MatchError> {
                        spark.sql("select firstShort(" + values.joinToString() + ")")
                    }
                }

                should("with Ints") {
                    val value = 1
                    udf.register(::firstInt)

                    spark.sql("select firstInt()")
                        .collectAsList()
                        .single()
                        .getAs<Int?>(0) shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { value }
                        spark.sql("select firstInt(" + values.joinToString() + ")")
                            .collectAsList()
                            .single()
                            .getAs<Int?>(0) shouldBe value
                    }

                    val values = (1..23).map { value }
                    shouldThrow<scala.MatchError> {
                        spark.sql("select firstInt(" + values.joinToString() + ")")
                    }
                }

                should("with Longs") {
                    val value = 1L
                    udf.register(::firstLong)

                    spark.sql("select firstLong()")
                        .collectAsList()
                        .single()
                        .getAs<Long?>(0) shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { value }
                        spark.sql("select firstLong(" + values.joinToString() + ")")
                            .collectAsList()
                            .single()
                            .getAs<Long?>(0) shouldBe value
                    }

                    val values = (1..23).map { value }
                    shouldThrow<scala.MatchError> {
                        spark.sql("select firstLong(" + values.joinToString() + ")")
                    }
                }

                should("with Floats") {
                    val value = 1f
                    udf.register(::firstFloat)

                    spark.sql("select firstFloat()")
                        .collectAsList()
                        .single()
                        .getAs<Float?>(0) shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { value }
                        spark.sql("select firstFloat(" + values.joinToString() + ")")
                            .collectAsList()
                            .single()
                            .getAs<Float?>(0) shouldBe value
                    }

                    val values = (1..23).map { value }
                    shouldThrow<scala.MatchError> {
                        spark.sql("select firstFloat(" + values.joinToString() + ")")
                    }
                }

                should("with Doubles") {
                    val value = 1.0
                    udf.register(::firstDouble)

                    spark.sql("select firstDouble()")
                        .collectAsList()
                        .single()
                        .getAs<Double?>(0) shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { value }
                        spark.sql("select firstDouble(" + values.joinToString() + ")")
                            .collectAsList()
                            .single()
                            .getAs<Double?>(0) shouldBe value
                    }

                    val values = (1..23).map { value }
                    shouldThrow<scala.MatchError> {
                        spark.sql("select firstDouble(" + values.joinToString() + ")")
                    }
                }

                should("with Booleans") {
                    val value = true
                    udf.register(::firstBoolean)

                    spark.sql("select firstBoolean()")
                        .collectAsList()
                        .single()
                        .getAs<Boolean?>(0) shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { value }
                        spark.sql("select firstBoolean(" + values.joinToString() + ")")
                            .collectAsList()
                            .single()
                            .getAs<Boolean?>(0) shouldBe value
                    }

                    val values = (1..23).map { value }
                    shouldThrow<scala.MatchError> {
                        spark.sql("select firstBoolean(" + values.joinToString() + ")")
                    }
                }

                should("with Anys") {
                    val value = "test"
                    udf.register(::firstString)

                    spark.sql("select firstString()")
                        .collectAsList()
                        .single()
                        .getAs<String?>(0) shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { value }
                        spark.sql("select firstString(" + values.joinToString { "\"$it\"" } + ")")
                            .collectAsList()
                            .single()
                            .getAs<String?>(0) shouldBe value
                    }

                    val values = (1..23).map { value }
                    shouldThrow<scala.MatchError> {
                        spark.sql("select firstString(" + values.joinToString { "\"$it\"" } + ")")
                    }
                }


            }
        }

        context("Call vararg udf from dataset select") {
            withSpark(logLevel = SparkLogLevel.DEBUG) {
                should("with Bytes") {
                    val value = 1.toByte()
                    val ds = dsOf(value)

                    val firstByte = udf.register(::firstByte)

                    ds.select(firstByte())
                        .collectAsList()
                        .single() shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { ds.singleCol() }.toTypedArray()

                        ds.select(firstByte(*values))
                            .collectAsList()
                            .single() shouldBe value
                    }

                    val values = (1..23).map { ds.singleCol() }.toTypedArray()
                    shouldThrow<scala.MatchError> {
                        ds.select(firstByte(*values))
                    }
                }

                should("with Shorts") {
                    val value = 1.toShort()
                    val ds = dsOf(value)

                    val firstShort = udf.register(::firstShort)

                    ds.select(firstShort())
                        .collectAsList()
                        .single() shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { ds.singleCol() }.toTypedArray()

                        ds.select(firstShort(*values))
                            .collectAsList()
                            .single() shouldBe value
                    }

                    val values = (1..23).map { ds.singleCol() }.toTypedArray()
                    shouldThrow<scala.MatchError> {
                        ds.select(firstShort(*values))
                    }
                }

                should("with Ints") {
                    val value = 1
                    val ds = dsOf(value)

                    val firstInt = udf.register(::firstInt)

                    ds.select(firstInt())
                        .collectAsList()
                        .single() shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { ds.singleCol() }.toTypedArray()

                        ds.select(firstInt(*values))
                            .collectAsList()
                            .single() shouldBe value
                    }

                    val values = (1..23).map { ds.singleCol() }.toTypedArray()
                    shouldThrow<scala.MatchError> {
                        ds.select(firstInt(*values))
                    }
                }

                should("with Longs") {
                    val value = 1L
                    val ds = dsOf(value)

                    val firstLong = udf.register(::firstLong)

                    ds.select(firstLong())
                        .collectAsList()
                        .single() shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { ds.singleCol() }.toTypedArray()

                        ds.select(firstLong(*values))
                            .collectAsList()
                            .single() shouldBe value
                    }

                    val values = (1..23).map { ds.singleCol() }.toTypedArray()
                    shouldThrow<scala.MatchError> {
                        ds.select(firstLong(*values))
                    }
                }

                should("with Floats") {
                    val value = 1f
                    val ds = dsOf(value)

                    val firstFloat = udf.register(::firstFloat)

                    ds.select(firstFloat())
                        .collectAsList()
                        .single() shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { ds.singleCol() }.toTypedArray()

                        ds.select(firstFloat(*values))
                            .collectAsList()
                            .single() shouldBe value
                    }

                    val values = (1..23).map { ds.singleCol() }.toTypedArray()
                    shouldThrow<scala.MatchError> {
                        ds.select(firstFloat(*values))
                    }
                }

                should("with Doubles") {
                    val value = 1.0
                    val ds = dsOf(value)

                    val firstDouble = udf.register(::firstDouble)

                    ds.select(firstDouble())
                        .collectAsList()
                        .single() shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { ds.singleCol() }.toTypedArray()

                        ds.select(firstDouble(*values))
                            .collectAsList()
                            .single() shouldBe value
                    }

                    val values = (1..23).map { ds.singleCol() }.toTypedArray()
                    shouldThrow<scala.MatchError> {
                        ds.select(firstDouble(*values))
                    }
                }

                should("with Booleans") {
                    val value = true
                    val ds = dsOf(value)

                    val firstBoolean = udf.register(::firstBoolean)

                    ds.select(firstBoolean())
                        .collectAsList()
                        .single() shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { ds.singleCol() }.toTypedArray()

                        ds.select(firstBoolean(*values))
                            .collectAsList()
                            .single() shouldBe value
                    }

                    val values = (1..23).map { ds.singleCol() }.toTypedArray()
                    shouldThrow<scala.MatchError> {
                        ds.select(firstBoolean(*values))
                    }
                }

                should("with Anys") {
                    val value = "test"
                    val ds = dsOf(value)

                    val firstString = udf.register(::firstString)

                    ds.select(firstString())
                        .collectAsList()
                        .single() shouldBe null

                    (1..22).forEach { nr ->
                        val values = (1..nr).map { ds.singleCol() }.toTypedArray()

                        ds.select(firstString(*values))
                            .collectAsList()
                            .single() shouldBe value
                    }

                    val values = (1..23).map { ds.singleCol() }.toTypedArray()
                    shouldThrow<scala.MatchError> {
                        ds.select(firstString(*values))
                    }
                }
            }
        }

    }
})

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
    override fun bufferEncoder(): Encoder<Average> = encoder()

    // Specifies the Encoder for the final output value type
    override fun outputEncoder(): Encoder<Double> = encoder()

}

// small but fun helpers I couldn't help but to leave somewhere in the code
// allows 2-argument lambdas to be converted in a this+it lambda
fun <S, T, U> extensionFun(block: S.(T) -> U): S.(T) -> U = block
fun <S, T> applyFun(block: S.(T) -> Unit): S.(T) -> S = extensionFun { block(it); this }

private val aggregator = aggregatorOf<Long, Average, Double>(
    zero = { Average(0L, 0L) },
    reduce = applyFun {
        sum += it
        count += 1
    },
    merge = applyFun {
        sum += it.sum
        count += it.count
    },
    finish = { it.sum.toDouble() / it.count },
)


private val addTwoConst = { x: Int, y: Int -> x + y }

data class NormalClass(
    val age: Int,
    val name: String
)

private val firstByteVal = { a: ByteArray -> a.firstOrNull() }
private val firstShortVal = { a: ShortArray -> a.firstOrNull() }
private val firstIntVal = { a: IntArray -> a.firstOrNull() }
private val firstLongVal = { a: LongArray -> a.firstOrNull() }
private val firstFloatVal = { a: FloatArray -> a.firstOrNull() }
private val firstDoubleVal = { a: DoubleArray -> a.firstOrNull() }
private val firstBooleanVal = { a: BooleanArray -> a.firstOrNull() }
private val firstStringVal = { a: Array<String> -> a.firstOrNull() }