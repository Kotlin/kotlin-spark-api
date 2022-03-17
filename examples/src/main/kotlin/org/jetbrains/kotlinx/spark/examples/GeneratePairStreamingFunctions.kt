/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 3.2+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2022 JetBrains
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
package org.jetbrains.kotlinx.spark.examples

import org.apache.spark.streaming.dstream.PairDStreamFunctions
import org.intellij.lang.annotations.Language
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions


object GeneratePairStreamingFunctions {

//    fun <K, V> JavaDStream<Arity2<K, V>>.reduceByKey(func: (V, V) -> V): JavaDStream<Arity2<K, V>> =
//        mapToPair { it.toTuple() }
//            .reduceByKey(func)
//            .map { it.toArity() }

    @JvmStatic
    fun main(args: Array<String>) {

        val klass = PairDStreamFunctions::class

        val functions = klass.functions

        for (function: KFunction<*> in functions) with(function) {

            val types = (typeParameters.map { it.name }.toSet() + "K" + "V").joinToString()

            val parameterString = parameters.drop(1).joinToString {
                "${it.name}: ${it.type}"
            }
            val parameterStringNoType = parameters.drop(1).joinToString { it.name!! }

            @Language("kt")
            val new = """
                fun <$types> JavaDStream<Arity2<K, V>>.$name($parameterString)
                
            """.trimIndent()

//
//            val new =
//                if (returnType.toString().contains("org.apache.spark.streaming.api.java.JavaPairDStream")) {
//                    val newReturnType = returnType.toString()
//                        .replaceFirst("JavaPairDStream<", "JavaDStream<Arity2<")
//                        .replaceAfterLast(">", ">")
//
//                    """
//                    fun <$types> JavaDStream<Arity2<K, V>>.$name($parameterString): $newReturnType =
//                        mapToPair { it.toTuple() }
//                            .$name($parameterStringNoType)
//                            .map { it.toArity() }
//
//                    """.trimIndent()
//                } else {
//                    """
//                    fun <$types> JavaDStream<Arity2<K, V>>.$name($parameterString): $returnType =
//                        mapToPair { it.toTuple() }
//                            .$name($parameterStringNoType)
//
//                    """.trimIndent()
//                }
//                    .replace("!", "")
//                    .replace("(Mutable)", "")
//
//            if ("\$" !in new) println(new)
        }


    }
}
