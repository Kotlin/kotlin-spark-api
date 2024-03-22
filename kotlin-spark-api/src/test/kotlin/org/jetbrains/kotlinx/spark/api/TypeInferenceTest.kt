package org.jetbrains.kotlinx.spark.api/*-
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
import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.creating.Expect
import io.kotest.core.spec.style.ShouldSpec
import org.apache.spark.sql.types.ArrayType
import org.apache.spark.sql.types.IntegerType
import org.jetbrains.kotlinx.spark.api.plugin.annotations.Sparkify
import org.jetbrains.kotlinx.spark.api.struct.model.DataType.StructType
import org.jetbrains.kotlinx.spark.api.struct.model.DataType.TypeName
import org.jetbrains.kotlinx.spark.api.struct.model.ElementType.ComplexElement
import org.jetbrains.kotlinx.spark.api.struct.model.ElementType.SimpleElement
import org.jetbrains.kotlinx.spark.api.struct.model.Struct
import org.jetbrains.kotlinx.spark.api.struct.model.StructField
import kotlin.reflect.typeOf


@OptIn(ExperimentalStdlibApi::class)
class TypeInferenceTest : ShouldSpec({
    context("org.jetbrains.spark.api.org.jetbrains.spark.api.schema") {
        @Sparkify data class Test2<T>(val vala2: T, val para2: Pair<T, String>)
        @Sparkify data class Test<T>(val vala: T, val tripl1: Triple<T, Test2<Long>, T>)

        val struct = Struct.fromJson(schemaFor<Pair<String, Test<Int>>>().prettyJson())!!
        should("contain correct typings") {
            expect(struct.fields).notToEqualNull().toContain.inAnyOrder.only.entries(
                hasField("first", "string"),
                hasStruct(
                    "second",
                    hasField("vala", "integer"),
                    hasStruct(
                        "tripl1",
                        hasField("first", "integer"),
                        hasStruct(
                            "second",
                            hasField("vala2", "long"),
                            hasStruct(
                                "para2",
                                hasField("first", "long"),
                                hasField("second", "string")
                            )
                        ),
                        hasField("third", "integer")
                    )
                )
            )
        }
    }
    context("org.jetbrains.spark.api.org.jetbrains.spark.api.schema with more complex data") {
        @Sparkify data class Single<T>(val vala3: T)
        @Sparkify
        data class Test2<T>(val vala2: T, val para2: Pair<T, Single<Double>>)
        @Sparkify data class Test<T>(val vala: T, val tripl1: Triple<T, Test2<Long>, T>)

        val struct = Struct.fromJson(schemaFor<Pair<String, Test<Int>>>().prettyJson())!!
        should("contain correct typings") {
            expect(struct.fields).notToEqualNull().toContain.inAnyOrder.only.entries(
                hasField("first", "string"),
                hasStruct(
                    "second",
                    hasField("vala", "integer"),
                    hasStruct(
                        "tripl1",
                        hasField("first", "integer"),
                        hasStruct(
                            "second",
                            hasField("vala2", "long"),
                            hasStruct(
                                "para2",
                                hasField("first", "long"),
                                hasStruct(
                                    "second",
                                    hasField("vala3", "double")
                                )
                            )
                        ),
                        hasField("third", "integer")
                    )
                )
            )
        }
    }
    context("org.jetbrains.spark.api.org.jetbrains.spark.api.schema without generics") {
        data class Test(val a: String, val b: Int, val c: Double)

        val struct = Struct.fromJson(schemaFor<Test>().prettyJson())!!
        should("return correct types too") {
            expect(struct.fields).notToEqualNull().toContain.inAnyOrder.only.entries(
                hasField("a", "string"),
                hasField("b", "integer"),
                hasField("c", "double")
            )
        }
    }
    context("type with list of ints") {
        val struct = Struct.fromJson(schemaFor<List<Int>>().prettyJson())!!
        should("return correct types too") {
            expect(struct) {
                isOfType("array")
                feature { f(it::elementType) }.toEqual(SimpleElement("integer"))
            }
        }
    }
    context("type with list of Pairs int to long") {
        val struct = Struct.fromJson(schemaFor<List<Pair<Int, Long>>>().prettyJson())!!
        should("return correct types too") {
            expect(struct) {
                isOfType("array")
                feature { f(it::elementType) }.notToEqualNull().toBeAnInstanceOf(fun Expect<ComplexElement>.() {
                    feature { f(it.value::fields) }.notToEqualNull().toContain.inAnyOrder.only.entries(
                        hasField("first", "integer"),
                        hasField("second", "long")
                    )
                })
            }
        }
    }
    context("type with list of generic data class with E generic name") {
        data class Test<E>(val e: E)

        val struct = Struct.fromJson(schemaFor<List<Test<String>>>().prettyJson())!!
        should("return correct types too") {
            expect(struct) {
                isOfType("array")
                feature { f(it::elementType) }.notToEqualNull().toBeAnInstanceOf(fun Expect<ComplexElement>.() {
                    feature { f(it.value::fields) }.notToEqualNull().toContain.inAnyOrder.only.entries(
                        hasField("e", "string")
                    )
                })
            }
        }
    }
    context("type with list of list of int") {
        val struct = Struct.fromJson(schemaFor<List<List<Int>>>().prettyJson())!!
        should("return correct types too") {
            expect(struct) {
                isOfType("array")
                feature { f(it::elementType) }.notToEqualNull().toBeAnInstanceOf(fun Expect<ComplexElement>.() {
                    feature { f(it.value::elementType) }.toEqual(SimpleElement("integer"))
                })
            }
        }
    }
    context("Subtypes of list") {
        val struct = Struct.fromJson(schemaFor<ArrayList<Int>>().prettyJson())!!
        should("return correct types too") {
            expect(struct) {
                isOfType("array")
                feature { f(it::elementType) }.toEqual(SimpleElement("integer"))
                feature { f(it::containsNull) }.toEqual(false)
            }
        }
    }
    context("Subtypes of list with nullable values") {
        val struct = Struct.fromJson(schemaFor<ArrayList<Int?>>().prettyJson())!!
        should("return correct types too") {
            expect(struct) {
                isOfType("array")
                feature { f(it::elementType) }.toEqual(SimpleElement("integer"))
                feature { f(it::containsNull) }.toEqual(true)
            }
        }
    }
    context("data class with props in order lon → lat") {
        data class Test(val lon: Double, val lat: Double)

        val struct = Struct.fromJson(schemaFor<Test>().prettyJson())!!
        should("Not change order of fields") {
            expect(struct.fields).notToEqualNull().containsExactly(
                hasField("lon", "double"),
                hasField("lat", "double")
            )
        }
    }
    context("data class with nullable list inside") {
        data class Sample(val optionList: List<Int>?)

        val struct = Struct.fromJson(schemaFor<Sample>().prettyJson())!!

        should("show that list is nullable and element is not") {
            expect(struct)
                .feature("some", { fields }) {
                    notToEqualNull().toContain.inOrder.only.entry {
                        this
                            .feature("field name", { name }) { toEqual("optionList") }
                            .feature("optionList is nullable", { nullable }) { toEqual(true) }
                            .feature("optionList", { type }) {
                                this
                                    .isA<StructType>()
                                    .feature("element type of optionList",
                                        { value.elementType }) { toEqual(SimpleElement("integer")) }
                                    .feature("optionList contains null", { value.containsNull }) { toEqual(false) }
                                    .feature("optionList type", { value }) { isOfType("array") }
                            }
                    }
                }
        }

        should("generate valid serializer schema") {
            expect(schemaFor<Sample>() as org.apache.spark.sql.types.StructType) {
                this
                    .feature("data type", { this.fields()?.toList() }) {
                        this.notToEqualNull().toContain.inOrder.only.entry {
                            this
                                .feature("element name", { name() }) { toEqual("optionList") }
                                .feature("field type", { dataType() }, {
                                    this
                                        .isA<ArrayType>()
                                        .feature("element type", { elementType() }) { isA<IntegerType>() }
                                        .feature("element nullable", { containsNull() }) { toEqual(expected = false) }
                                })
                                .feature("optionList nullable", { nullable() }) { toEqual(true) }
                        }
                    }
            }
        }
    }

})

private fun Expect<Struct>.isOfType(type: String) {
    feature { f(it::type) }.toEqual(type)
}

private fun hasStruct(
    name: String,
    expectedField: Expect<StructField>.() -> Unit,
    vararg expectedFields: Expect<StructField>.() -> Unit,
): Expect<StructField>.() -> Unit {
    return {
        feature { f(it::name) }.toEqual(name)
        feature { f(it::type) }.toBeAnInstanceOf(fun Expect<StructType>.() {
            feature { f(it.value::fields) }.notToEqualNull().toContain.inAnyOrder.only.entries(
                expectedField,
                *expectedFields
            )
        })
    }
}

private fun hasField(name: String, type: String): Expect<StructField>.() -> Unit = {
    feature { f(it::name) }.toEqual(name)
    feature { f(it::type) }.isA<TypeName>().feature { f(it::value) }.toEqual(type)
}
