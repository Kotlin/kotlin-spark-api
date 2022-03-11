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
package org.jetbrains.kotlinx.spark.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.string.shouldContain
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase

class JupyterTests : ShouldSpec(object : (ShouldSpec) -> Unit, JupyterReplTestCase() {

    override fun invoke(it: ShouldSpec) = it.run()

    fun ShouldSpec.run() {
        context("Jupyter") {
//            @Language("kts")
//            val html = execHtml(
//                """
//            val ds = listOf(1, 2, 3).toDS(spark)
//            ds
//            """.trimIndent()
//            )
//
//            println(html)
//
//            html shouldContain "value"
//            html shouldContain "1"
//            html shouldContain "2"
//            html shouldContain "3"


        }
    }


//    val jupyter = object : JupyterReplTestCase() {}


})
