import io.kotlintest.specs.AnnotationSpec
import org.jetbrains.spark.api.schema
import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
class KotlinSparkTest : AnnotationSpec() {
    @Test
    fun simpleTest() {
        val schema = schema(typeOf<Pair<Pair<Int, Int>, String>>())
        println(schema)
    }

    @Test
    fun test1() {
        data class Test<T>(val a: T, val b: Pair<T, Int>)

        val schema = schema(typeOf<Pair<String, Test<Int>>>())
        println(schema)
    }

    @Test
    fun test2() {
        data class Test2<T>(val vala2: T, val lista: List<T>)
        data class Test<T>(val vala: T, val para: Pair<T, Test2<String>>)

        val schema = schema(typeOf<Pair<String, Test<Int>>>())
        println(schema)
    }
}