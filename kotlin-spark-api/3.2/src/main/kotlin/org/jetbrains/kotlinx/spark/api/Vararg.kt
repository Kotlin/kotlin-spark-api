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


import scala.Function0
import scala.Function1
import scala.Function2
import scala.Function3
import scala.Function4
import scala.Function5
import scala.Function6
import scala.Function7
import scala.Function8
import scala.Function9
import scala.Function10
import scala.Function11
import scala.Function12
import scala.Function13
import scala.Function14
import scala.Function15
import scala.Function16
import scala.Function17
import scala.Function18
import scala.Function19
import scala.Function20
import scala.Function21
import scala.Function22
import java.io.Serializable

class VarargUnwrapper<T, Array, R>(
    val varargFunc: (Array) -> R,
    val newArray: (size: Int, init: (i: Int) -> T) -> Array,
) :
    Function0<R>,
    Function1<T, R>,
    Function2<T, T, R>,
    Function3<T, T, T, R>,
    Function4<T, T, T, T, R>,
    Function5<T, T, T, T, T, R>,
    Function6<T, T, T, T, T, T, R>,
    Function7<T, T, T, T, T, T, T, R>,
    Function8<T, T, T, T, T, T, T, T, R>,
    Function9<T, T, T, T, T, T, T, T, T, R>,
    Function10<T, T, T, T, T, T, T, T, T, T, R>,
    Function11<T, T, T, T, T, T, T, T, T, T, T, R>,
    Function12<T, T, T, T, T, T, T, T, T, T, T, T, R>,
    Function13<T, T, T, T, T, T, T, T, T, T, T, T, T, R>,
    Function14<T, T, T, T, T, T, T, T, T, T, T, T, T, T, R>,
    Function15<T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R>,
    Function16<T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R>,
    Function17<T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R>,
    Function18<T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R>,

//    Function19<T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R>, TODO dunno why it breaks
//    Function20<T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R>,
//    Function21<T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R>,
//    Function22<T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R>,
    Serializable {

    private fun vararg(vararg t: T): R = varargFunc(newArray(t.size) { t[it] })

    override fun curried(): Nothing = error("")
    override fun tupled(): Nothing  = error("")

    override fun apply(): R = vararg()
    override fun apply(t0: T): R = vararg(t0)
    override fun apply(t0: T, t1: T): R = vararg(t0, t1)
    override fun apply(t0: T, t1: T, t2: T): R = vararg(t0, t1, t2)
    override fun apply(t0: T, t1: T, t2: T, t3: T): R = vararg(t0, t1, t2, t3)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T): R = vararg(t0, t1, t2, t3, t4)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T): R = vararg(t0, t1, t2, t3, t4, t5)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T): R = vararg(t0, t1, t2, t3, t4, t5, t6)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T, t12: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T, t12: T, t13: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T, t12: T, t13: T, t14: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T, t12: T, t13: T, t14: T, t15: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T, t12: T, t13: T, t14: T, t15: T, t16: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16)
    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T, t12: T, t13: T, t14: T, t15: T, t16: T, t17: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)

//    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T, t12: T, t13: T, t14: T, t15: T, t16: T, t17: T, t18: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18)
//    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T, t12: T, t13: T, t14: T, t15: T, t16: T, t17: T, t18: T, t19: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19)
//    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T, t12: T, t13: T, t14: T, t15: T, t16: T, t17: T, t18: T, t19: T, t20: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20)
//    override fun apply(t0: T, t1: T, t2: T, t3: T, t4: T, t5: T, t6: T, t7: T, t8: T, t9: T, t10: T, t11: T, t12: T, t13: T, t14: T, t15: T, t16: T, t17: T, t18: T, t19: T, t20: T, t21: T): R = vararg(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21)
}

