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
    val user = User()
    val name = User::class.java.getMethod("name").invoke(user)
    val age = User::class.java.getMethod("age").invoke(user)
    val a = User::class.java.getMethod("a").invoke(user)
    val b = User::class.java.getMethod("b").invoke(user)

    if (name != "John Doe" || age != 25 || a != 1.0 || b != 2.0) {
        return "Could not invoke functions name(), age(), a(), or b() from Java"
    }
    @Suppress("USELESS_IS_CHECK")
    if (user !is foo.bar.Product)
        return "User is not a Product"

    @Suppress("USELESS_IS_CHECK")
    if (user !is java.io.Serializable)
        return "User is not Serializable"

    val canEqual = User::class.java.getMethod("canEqual", Any::class.java).invoke(user, user)
    if (canEqual != true) {
        return "Could invoke function canEqual() from Java but was false"
    }
    val productArity = User::class.java.getMethod("productArity").invoke(user)
    if (productArity != 4) {
        return "Could invoke function productArity() from Java but was $productArity"
    }
    val productElement = User::class.java.getMethod("productElement", Int::class.java).invoke(user, 0)
    if (productElement != "John Doe") {
        return "Could invoke function productElement() from Java but was $productElement"
    }
    try {
        User::class.java.getMethod("productElement", Int::class.java).invoke(user, 10)
    } catch (e: Exception) {
        return "OK"
    }

    return "Could invoke function productElement() from Java but did not throw IndexOutOfBoundsException"
}

@Sparkify
data class User(
    val name: String = "John Doe",
    val age: Int = 25,
    @ColumnName("a") val test: Double = 1.0,
    @get:ColumnName("b") val test2: Double = 2.0,
)