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

fun box(): String {

    @Sparkify
    data class User(
        val name: String = "John Doe",
        val age: Int = 25,
        @ColumnName("a") val test: Double = 1.0,
        @get:ColumnName("b") val test2: Double = 2.0,
    )

    val user = User()
    val name = User::class.java.getMethod("name").invoke(user)
    val age = User::class.java.getMethod("age").invoke(user)
    val a = User::class.java.getMethod("a").invoke(user)
    val b = User::class.java.getMethod("b").invoke(user)

    if (name != "John Doe" || age != 25 || a != 1.0 || b != 2.0) {
        return "Could not invoke functions name(), age(), a(), or b() from Java"
    }
    return "OK"
}
