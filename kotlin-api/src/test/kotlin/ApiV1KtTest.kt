import io.kotlintest.*
import io.kotlintest.inspectors.forOne
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.instanceOf
import io.kotlintest.specs.ShouldSpec
import struct.model.DataType
import struct.model.DataType.TypeName
import struct.model.Struct
import struct.model.StructField
import kotlin.reflect.KClass



class ApiV1KtTest : ShouldSpec({
    "schema"{
        data class Test2<T>(val vala2: T, val para2: Pair<T, String>)
        data class Test<T>(val vala: T, val tripl1: Triple<T, Test2<Long>, T>)
        val schema = schema(object : KTypeRef<Pair<String, Test<Int>>>() {}.type)
        var struct = Struct.fromJson(schema.prettyJson())!!
        should("contain correct typings") {
            //root object is pair of string and struct
            struct.fields shouldHaveSize 2
            struct.type shouldBe "struct"
            struct.fields.forOne { it.shouldBeDescribed("first", "string") }
            struct.fields.forOne { it.shouldBeDescribed<DataType.StructType>("second") }
            // struct is os type Test with integer named vala and Triple named tripl1
            var testField = struct.fields.find { it.name == "second" }!!.type
            testField shouldBeInstanceOf DataType.StructType::class
            var typeValue = testField as DataType.StructType
            struct = typeValue.value
            struct.fields shouldHaveSize 2
            struct.fields.forOne { it.shouldBeDescribed("vala", "integer") }
            struct.fields.forOne { it.shouldBeDescribed<DataType.StructType>("tripl1") }
            // tripl1 is Triple of integer, Test2 and integer
            testField = struct.fields.find { it.name == "tripl1" }!!.type
            testField shouldBeInstanceOf DataType.StructType::class
            typeValue = testField as DataType.StructType
            struct = typeValue.value
            struct.fields shouldHaveSize 3
            struct.fields.forOne { it.shouldBeDescribed("first", "integer") }
            struct.fields.forOne { it.shouldBeDescribed<DataType.StructType>("second") }
            struct.fields.forOne { it.shouldBeDescribed("third", "integer") }
            // Test2 lying in field second contains fields vala2 of type long and para2 which is pair
            testField = struct.fields.find { it.name == "second" }!!.type
            testField shouldBeInstanceOf DataType.StructType::class
            typeValue = testField as DataType.StructType
            struct = typeValue.value
            struct.fields shouldHaveSize 2
            struct.fields.forOne { it.shouldBeDescribed("vala2", "long") }
            struct.fields.forOne { it.shouldBeDescribed<DataType.StructType>("para2") }
            // para2 is Pair of long to string
            testField = struct.fields.find { it.name == "para2" }!!.type
            testField shouldBeInstanceOf DataType.StructType::class
            typeValue = testField as DataType.StructType
            struct = typeValue.value
            struct.fields shouldHaveSize 2
            struct.fields.forOne { it.shouldBeDescribed("first", "long") }
            struct.fields.forOne { it.shouldBeDescribed("second", "string") }
        }
    }
    "schema with more complex data"{
        data class Single<T>(val vala3:T)
        data class Test2<T>(val vala2: T, val para2: Pair<T, Single<Double>>)
        data class Test<T>(val vala: T, val tripl1: Triple<T, Test2<Long>, T>)
        val schema = schema(object : KTypeRef<Pair<String, Test<Int>>>() {}.type)
        var struct = Struct.fromJson(schema.prettyJson())!!
        should("contain correct typings") {
            //root object is pair of string and struct
            struct.fields shouldHaveSize 2
            struct.type shouldBe "struct"
            struct.fields.forOne { it.shouldBeDescribed("first", "string") }
            struct.fields.forOne { it.shouldBeDescribed<DataType.StructType>("second") }
            // struct is os type Test with integer named vala and Triple named tripl1
            var testField = struct.fields.find { it.name == "second" }!!.type
            testField shouldBeInstanceOf DataType.StructType::class
            var typeValue = testField as DataType.StructType
            struct = typeValue.value
            struct.fields shouldHaveSize 2
            struct.fields.forOne { it.shouldBeDescribed("vala", "integer") }
            struct.fields.forOne { it.shouldBeDescribed<DataType.StructType>("tripl1") }
            // tripl1 is Triple of integer, Test2 and integer
            testField = struct.fields.find { it.name == "tripl1" }!!.type
            testField shouldBeInstanceOf DataType.StructType::class
            typeValue = testField as DataType.StructType
            struct = typeValue.value
            struct.fields shouldHaveSize 3
            struct.fields.forOne { it.shouldBeDescribed("first", "integer") }
            struct.fields.forOne { it.shouldBeDescribed<DataType.StructType>("second") }
            struct.fields.forOne { it.shouldBeDescribed("third", "integer") }
            // Test2 lying in field second contains fields vala2 of type long and para2 which is pair
            testField = struct.fields.find { it.name == "second" }!!.type
            testField shouldBeInstanceOf DataType.StructType::class
            typeValue = testField as DataType.StructType
            struct = typeValue.value
            struct.fields shouldHaveSize 2
            struct.fields.forOne { it.shouldBeDescribed("vala2", "long") }
            struct.fields.forOne { it.shouldBeDescribed<DataType.StructType>("para2") }
            // para2 is Pair of long to Single
            testField = struct.fields.find { it.name == "para2" }!!.type
            testField shouldBeInstanceOf DataType.StructType::class
            typeValue = testField as DataType.StructType
            struct = typeValue.value
            struct.fields shouldHaveSize 2
            struct.fields.forOne { it.shouldBeDescribed("first", "long") }
            struct.fields.forOne { it.shouldBeDescribed<DataType.StructType>("second") }
            // para2 is Pair of long to string
            testField = struct.fields.find { it.name == "second" }!!.type
            testField shouldBeInstanceOf DataType.StructType::class
            typeValue = testField as DataType.StructType
            struct = typeValue.value
            struct.fields shouldHaveSize 1
            struct.fields.forOne { it.shouldBeDescribed("vala3", "double") }
        }

    }
})

inline fun <reified T> StructField.shouldBeDescribed(name: String) = this should describedStruct<T>(name)
fun StructField.shouldBeDescribed(name: String, typeName: String) = this should describedStruct(name, typeName)
inline fun <reified T> StructField.shouldNotBeDescribed(name: String) = this shouldNot describedStruct<T>(name)
fun StructField.shouldNotBeDescribed(name: String, typeName: String) = this shouldNot describedStruct(name, typeName)

infix fun DataType.shouldBeInstanceOf(c: KClass<*>) = this should instanceOf(c)
infix fun DataType.shouldNotBeInstanceOf(c: KClass<*>) = this shouldNot instanceOf(c)


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

