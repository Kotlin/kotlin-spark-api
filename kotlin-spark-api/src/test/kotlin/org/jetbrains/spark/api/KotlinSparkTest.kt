package org.jetbrains.spark.api/*-
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
import io.kotest.core.spec.style.AnnotationSpec
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
