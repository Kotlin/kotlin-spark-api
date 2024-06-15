package org.jetbrains.kotlinx.spark.api

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import org.jetbrains.kotlinx.spark.api.plugin.annotations.ColumnName
import org.jetbrains.kotlinx.spark.api.plugin.annotations.Sparkify

class CompilerPluginTest : ShouldSpec({

    @Sparkify
    data class User(
        val name: String = "John Doe",
        val age: Int = 25,
        @ColumnName("test")
        val isEmpty: Boolean = false,
    )

    context("Compiler Plugin") {
        should("be enabled") {
            val user = User()
            shouldNotThrowAny {
                User::class.java.getMethod("name").invoke(user) shouldBe user.name
                User::class.java.getMethod("age").invoke(user) shouldBe user.age
                User::class.java.getMethod("test").invoke(user) shouldBe user.isEmpty
            }

            user should beInstanceOf<User>()
            user should beInstanceOf<scala.Product>()
            user should beInstanceOf<java.io.Serializable>()

            shouldNotThrowAny {
                User::class.java.getMethod("canEqual", Any::class.java).invoke(user, user) shouldBe true
                User::class.java.getMethod("productArity").invoke(user) shouldBe 3
                User::class.java.getMethod("productElement", Int::class.java).invoke(user, 0) shouldBe user.name
                User::class.java.getMethod("productElement", Int::class.java).invoke(user, 1) shouldBe user.age
                User::class.java.getMethod("productElement", Int::class.java).invoke(user, 2) shouldBe user.isEmpty
            }
            shouldThrowAny {
                User::class.java.getMethod("productElement", Int::class.java).invoke(user, 10)
            }
        }
    }
})