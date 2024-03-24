package foo.bar

annotation class Sparkify
annotation class ColumnName(val name: String)

// Fake Equals
interface Equals {
    fun canEqual(that: Any?): Boolean
}

// Fake Product
interface Product: Equals {
    fun productElement(n: Int): Any
    fun productArity(): Int
}

fun test() {
    val user = User()
    user.productArity() // should not be an error
}

@Sparkify
data <!ABSTRACT_MEMBER_NOT_IMPLEMENTED!>class User<!>(
    val name: String = "John Doe",
    val age: Int = 25,
    @ColumnName("a") val test: Double = 1.0,
    @get:ColumnName("b") val test2: Double = 2.0,
)
