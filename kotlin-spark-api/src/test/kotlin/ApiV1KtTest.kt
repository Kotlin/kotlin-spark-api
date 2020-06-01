/*-
 * =LICENSE=
 * Kotlin Spark API
 * ----------
 * Copyright (C) 2019 - 2020 JetBrains
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
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.*
import io.kotest.matchers.collections.shouldHaveSize
import org.jetbrains.spark.api.schema
import struct.model.DataType
import struct.model.DataType.StructType
import struct.model.DataType.TypeName
import struct.model.ElementType.ComplexElement
import struct.model.ElementType.SimpleElement
import struct.model.Struct
import struct.model.StructField
import java.util.*
import kotlin.reflect.typeOf


@OptIn(ExperimentalStdlibApi::class)
class ApiV1KtTest : ShouldSpec({
    context("org.jetbrains.spark.api.org.jetbrains.spark.api.schema") {
        data class Test2<T>(val vala2: T, val para2: Pair<T, String>)
        data class Test<T>(val vala: T, val tripl1: Triple<T, Test2<Long>, T>)

        val schema = schema(typeOf<Pair<String, Test<Int>>>())
        var struct = Struct.fromJson(schema.prettyJson())!!
        var testField: DataType
        var typeValue: StructType
        should("contain correct typings") {
            struct.fields!!.apply {
                //root object is pair of string and struct
                this shouldHaveSize 2
                struct.type shouldBe "struct"
                this.forOne { it.shouldBeDescribed("first", "string") }
                this.forOne { it.shouldBeDescribed<StructType>("second") }
                // struct is os type Test with integer named vala and Triple named tripl1
                testField = this.find { it.name == "second" }!!.type
            }
            testField should instanceOf(StructType::class)
            typeValue = testField as StructType
            struct = typeValue.value
            struct.fields!!.apply {
                this shouldHaveSize 2
                this.forOne { it.shouldBeDescribed("vala", "integer") }
                this.forOne { it.shouldBeDescribed<StructType>("tripl1") }
                // tripl1 is Triple of integer, Test2 and integer
                testField = this.find { it.name == "tripl1" }!!.type
            }
            testField should instanceOf(StructType::class)
            typeValue = testField as StructType
            struct = typeValue.value
            struct.fields!!.apply {
                this shouldHaveSize 3
                this.forOne { it.shouldBeDescribed("first", "integer") }
                this.forOne { it.shouldBeDescribed<StructType>("second") }
                this.forOne { it.shouldBeDescribed("third", "integer") }
                // Test2 lying in field second contains fields vala2 of type long and para2 which is pair
                testField = this.find { it.name == "second" }!!.type
            }
            testField should instanceOf(StructType::class)
            typeValue = testField as StructType
            struct = typeValue.value
            struct.fields!!.apply {
                this shouldHaveSize 2
                this.forOne { it.shouldBeDescribed("vala2", "long") }
                this.forOne { it.shouldBeDescribed<StructType>("para2") }
                // para2 is Pair of long to string
                testField = this.find { it.name == "para2" }!!.type
            }
            testField should instanceOf(StructType::class)
            typeValue = testField as StructType
            struct = typeValue.value
            struct.fields!!.apply {
                this shouldHaveSize 2
                this.forOne { it.shouldBeDescribed("first", "long") }
                this.forOne { it.shouldBeDescribed("second", "string") }
            }
        }
    }
    context("org.jetbrains.spark.api.org.jetbrains.spark.api.schema with more complex data") {
        data class Single<T>(val vala3: T)
        data class Test2<T>(val vala2: T, val para2: Pair<T, Single<Double>>)
        data class Test<T>(val vala: T, val tripl1: Triple<T, Test2<Long>, T>)

        val schema = schema(typeOf<Pair<String, Test<Int>>>())
        var struct = Struct.fromJson(schema.prettyJson())!!
        var testField: DataType
        var typeValue: StructType
        should("contain correct typings") {
            //root object is pair of string and struct
            struct.fields!!.apply {
                this shouldHaveSize 2
                struct.type shouldBe "struct"
                this.forOne { it.shouldBeDescribed("first", "string") }
                this.forOne { it.shouldBeDescribed<StructType>("second") }
                // struct is os type Test with integer named vala and Triple named tripl1
                testField = this.find { it.name == "second" }!!.type
            }
            testField should instanceOf(StructType::class)
            typeValue = testField as StructType
            struct = typeValue.value
            struct.fields!!.apply {
                this shouldHaveSize 2
                this.forOne { it.shouldBeDescribed("vala", "integer") }
                this.forOne { it.shouldBeDescribed<StructType>("tripl1") }
                // tripl1 is Triple of integer, Test2 and integer
                testField = this.find { it.name == "tripl1" }!!.type
            }
            testField should instanceOf(StructType::class)
            typeValue = testField as StructType
            struct = typeValue.value
            struct.fields!!.apply {
                this shouldHaveSize 3
                this.forOne { it.shouldBeDescribed("first", "integer") }
                this.forOne { it.shouldBeDescribed<StructType>("second") }
                this.forOne { it.shouldBeDescribed("third", "integer") }
                // Test2 lying in field second contains fields vala2 of type long and para2 which is pair
                testField = this.find { it.name == "second" }!!.type
            }
            testField should instanceOf(StructType::class)
            typeValue = testField as StructType
            struct = typeValue.value
            struct.fields!!.apply {
                this shouldHaveSize 2
                this.forOne { it.shouldBeDescribed("vala2", "long") }
                this.forOne { it.shouldBeDescribed<StructType>("para2") }
                // para2 is Pair of long to Single
                testField = find { it.name == "para2" }!!.type
            }
            testField should instanceOf(StructType::class)
            typeValue = testField as StructType
            struct = typeValue.value
            struct.fields!!.apply {
                this shouldHaveSize 2
                forOne { it.shouldBeDescribed("first", "long") }
                forOne { it.shouldBeDescribed<StructType>("second") }
                testField = find { it.name == "second" }!!.type
            }
            // para2 is Pair of long to string
            testField should instanceOf(StructType::class)
            typeValue = testField as StructType
            struct = typeValue.value
            struct.fields!!.apply {
                this shouldHaveSize 1
                forOne { it.shouldBeDescribed("vala3", "double") }
            }
        }

    }
    context("org.jetbrains.spark.api.org.jetbrains.spark.api.schema without generics") {
        data class Test(val a: String, val b: Int, val c: Double)

        val schema = schema(typeOf<Test>())
        val struct = Struct.fromJson(schema.prettyJson())!!
        should("return correct types too") {
            struct.fields!!
            struct.fields shouldHaveSize 3
            struct.fields.forOne { it.shouldBeDescribed("a", "string") }
            struct.fields.forOne { it.shouldBeDescribed("b", "integer") }
            struct.fields.forOne { it.shouldBeDescribed("c", "double") }
        }
    }
    context("type with list of ints") {
        val schema = schema(typeOf<List<Int>>())
        val struct = Struct.fromJson(schema.prettyJson())!!
        should("return correct types too") {
            struct.type shouldBe "array"
            struct.elementType!! shouldBe SimpleElement("integer")
        }
    }
    context("type with list of Pairs int to long") {
        val schema = schema(typeOf<List<Pair<Int, Long>>>())
        val struct = Struct.fromJson(schema.prettyJson())!!
        should("return correct types too") {
            struct.type shouldBe "array"
            struct.elementType!! should instanceOf(ComplexElement::class)
            val elType = (struct.elementType as ComplexElement)
            elType.value.fields!!.forOne { it.shouldBeDescribed("first", "integer") }
            elType.value.fields.forOne { it.shouldBeDescribed("second", "long") }
        }
    }
    context("type with list of generic data class with E generic name") {
        data class Test<E>(val e: E)

        val schema = schema(typeOf<List<Test<String>>>())
        val struct = Struct.fromJson(schema.prettyJson())!!
        should("return correct types too") {
            struct.type shouldBe "array"
            struct.elementType!! should instanceOf(ComplexElement::class)
            val elType = (struct.elementType as ComplexElement)
            elType.value.fields!!.forOne { it.shouldBeDescribed("e", "string") }
        }
    }
    context("type with list of list of int") {
        val schema = schema(typeOf<List<List<Int>>>())
        val struct = Struct.fromJson(schema.prettyJson())!!
        should("return correct types too") {
            struct.type shouldBe "array"
            struct.elementType!! should instanceOf(ComplexElement::class)
            val elType = (struct.elementType as ComplexElement)
            elType.value.elementType!! should instanceOf(SimpleElement::class)
            val el = elType.value.elementType as SimpleElement
            el.value shouldBe "integer"
        }
    }
    context("Subtypes of list") {
        val schema = schema(typeOf<ArrayList<Int>>())
        val struct = Struct.fromJson(schema.prettyJson())!!
        should("return correct types too") {
            struct.type shouldBe "array"
            struct.elementType!! should instanceOf(SimpleElement::class)
            struct.containsNull shouldBe false
            val elType = (struct.elementType as SimpleElement)
            elType.value shouldBe "integer"
        }
    }
    context("Subtypes of list with nullable values") {
        val schema = schema(typeOf<ArrayList<Int?>>())
        val struct = Struct.fromJson(schema.prettyJson())!!
        should("return correct types too") {
            struct.type shouldBe "array"
            struct.elementType!! should instanceOf(SimpleElement::class)
            struct.containsNull shouldBe true
            val elType = (struct.elementType as SimpleElement)
            elType.value shouldBe "integer"
        }
    }
})

inline fun <reified T> StructField.shouldBeDescribed(name: String) = this should describedStruct<T>(name)
fun StructField.shouldBeDescribed(name: String, typeName: String) = this should describedStruct(name, typeName)
inline fun <reified T> StructField.shouldNotBeDescribed(name: String) = this shouldNot describedStruct<T>(name)
fun StructField.shouldNotBeDescribed(name: String, typeName: String) = this shouldNot describedStruct(name, typeName)

inline fun <reified T> describedStruct(name: String) = object : Matcher<StructField> {
    override fun test(value: StructField) =
            MatcherResult(value.name == name, "name should be equal $name but was ${value.name}", "name should not be equal $name but was ${value.name}")
} and object : Matcher<StructField> {
    override fun test(value: StructField) =
            MatcherResult(value.type is T, "type should be ${T::class} but was ${value::class}", "type should not be ${T::class} but was ${value::class}")
}

fun describedStruct(name: String, typeName: String) = object : Matcher<StructField> {
    override fun test(value: StructField) =
            MatcherResult(value.name == name, "name should be equal $name but was ${value.name}", "name should not be equal $name but was ${value.name}")
} and object : Matcher<StructField> {
    override fun test(value: StructField) =
            MatcherResult(value.type is TypeName, "type should be StringValue but was ${value::class}", "type should not be StringValue but was ${value::class}")
} and object : Matcher<StructField> {
    override fun test(value: StructField) =
            MatcherResult((value.type as TypeName).value == typeName, "type should be of type $typeName but was ${value.type.value}", "type should not be of type $typeName but was ${value.type.value}")
}

