package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import scala.Function1
import scala.Tuple2
import scala.collection.JavaConversions
import java.lang.UnsupportedOperationException
import scala.collection.Set as ScalaSet
import scala.collection.mutable.Set as ScalaMutableSet
import scala.collection.Map as ScalaMap
import scala.collection.mutable.Map as ScalaMutableMap
import scala.collection.concurrent.Map as ScalaConcurrentMap
import scala.collection.Seq as ScalaSequence
import scala.collection.mutable.Seq as ScalaMutableSequence

private fun createScalaSet(): ScalaSet<String> = scala.collection.`Set$`.`MODULE$`.newBuilder<String>().apply {
    `$plus$eq`("a")
    `$plus$eq`("b")
    `$plus$eq`("c")
}.result()

private fun createScalaMutableSet(): ScalaMutableSet<String> = scala.collection.mutable.`HashSet$`.`MODULE$`.empty<String>().apply {
    `$plus$eq`("a")
    `$plus$eq`("b")
    `$plus$eq`("c")
}

private fun createScalaMap(): ScalaMap<String, String> = scala.collection.`Map$`.`MODULE$`.newBuilder<String, String>().apply {
    `$plus$eq`(Tuple2("a", "a"))
    `$plus$eq`(Tuple2("b", "b"))
    `$plus$eq`(Tuple2("c", "c"))
}.result() as ScalaMap<String, String>

private fun createScalaMutableMap(): ScalaMutableMap<String, String> = scala.collection.mutable.`HashMap$`.`MODULE$`.empty<String, String>().apply {
    `$plus$eq`(Tuple2("a", "a"))
    `$plus$eq`(Tuple2("b", "b"))
    `$plus$eq`(Tuple2("c", "c"))
}

private fun createScalaConcurrentMap(): ScalaConcurrentMap<String, String> = scala.collection.concurrent.`TrieMap$`.`MODULE$`.empty<String, String>().apply {
    `$plus$eq`(Tuple2("a", "a"))
    `$plus$eq`(Tuple2("b", "b"))
    `$plus$eq`(Tuple2("c", "c"))
}

private fun countFunc() = Function1<String, Any>{ true }

private fun fkt() = Function1<Any, String> {
    ('A'.toInt()+it as Int).toChar().toString()
}

private fun createScalaSequence(): ScalaSequence<String> = scala.collection.`Seq$`.`MODULE$`.tabulate<String>(3, fkt()) as ScalaSequence<String>

private fun createScalaMutableSequence(): ScalaMutableSequence<String> = scala.collection.mutable.`Seq$`.`MODULE$`.tabulate<String>(3, fkt()) as ScalaMutableSequence<String>

class ConversionsTest : ShouldSpec({
    context("org.jetbrains.kotlinx.spark.api.Conversions"){
        context("Behaviour test for underlying library"){
            should("Not change when using immutable conversion"){
                val original: ScalaSet<String> = createScalaSet()
                val javaSet = JavaConversions.setAsJavaSet(original)
                assertThrows<UnsupportedOperationException> { javaSet.add("d") }
                original.size().shouldBe(3)
            }

            should("Change when using mutable conversion"){
                val original: ScalaMutableSet<String> = createScalaMutableSet()
                val javaSet: MutableSet<String> = JavaConversions.mutableSetAsJavaSet(original)
                javaSet.add("d")
                original.size().shouldBe(4)
            }
        }
        context("Scala -> Java"){
            context("Immutable"){
                should("Sequence should not change"){
                    val seq: ScalaSequence<String> = createScalaSequence()
                    val list = seq.asList()
                    list.size.shouldBe(3)
                    seq.count(Function1<String, Any>{ true }).shouldBe(3)
                }
            }
            context("Mutable"){

            }
        }

    }
})