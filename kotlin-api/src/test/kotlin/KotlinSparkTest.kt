import org.junit.Assert.*
import org.junit.Test

class KotlinSparkTest {
    @Test
    fun simpleTest() {
        val schema = schema(object : KTypeRef<Pair<Pair<Int, Int>, String>>() {}.type)
        println(schema)
    }

    @Test
    fun test1() {
        data class Test<T>(val a: T, val b: Pair<T, Int>)
        val schema = schema(object : KTypeRef<Pair<String, Test<Int>>>() {}.type)
        println(schema)
    }

    @Test
    fun test2() {
        data class Test2<T>(val vala2: T, val lista: List<T>)
        data class Test<T>(val vala: T, val para: Pair<T, Test2<String>>)

        val schema = schema(object : KTypeRef<Pair<String, Test<Int>>>() {}.type)
        println(schema)
    }
}