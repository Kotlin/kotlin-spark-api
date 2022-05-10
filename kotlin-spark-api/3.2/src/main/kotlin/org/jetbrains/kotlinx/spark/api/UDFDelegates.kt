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

import org.apache.spark.sql.UDFRegistration
import kotlin.reflect.KProperty

/**
 * Allows to define a UDF as follows:
 * ```kotlin
 * val myUdfFunctionName by udf.register { argument1: Type1, argument2: Type2, ... ->
 *     myFunction()
 * }
 * ```
 * NOTE: `myUdfFunctionName` MUST be accessed in order for the UDF to get registered.
 * If you don't want or need that, use `udf.register("myUdfFunctionName") { ...` instead.
 */
class UDFWrapperBuilderDelegate<UDF>(val getValue: (name: String) -> UDF) {

    // Function could be registered with multiple names, this map keeps track of them
    private val udfMap: MutableMap<String, UDF> = mutableMapOf()

    fun withName(name: String): UDF = udfMap.getOrPut(name) { getValue(name) }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): UDF = withName(property.name)
}

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { doSomething() }
 * ```
 */
inline fun <reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: () -> R): UDFWrapperBuilderDelegate<UDFWrapper0> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1) -> R): UDFWrapperBuilderDelegate<UDFWrapper1> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2) -> R): UDFWrapperBuilderDelegate<UDFWrapper2> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3) -> R): UDFWrapperBuilderDelegate<UDFWrapper3> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4) -> R): UDFWrapperBuilderDelegate<UDFWrapper4> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5) -> R): UDFWrapperBuilderDelegate<UDFWrapper5> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6) -> R): UDFWrapperBuilderDelegate<UDFWrapper6> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7) -> R): UDFWrapperBuilderDelegate<UDFWrapper7> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8) -> R): UDFWrapperBuilderDelegate<UDFWrapper8> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R): UDFWrapperBuilderDelegate<UDFWrapper9> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R): UDFWrapperBuilderDelegate<UDFWrapper10> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R): UDFWrapperBuilderDelegate<UDFWrapper11> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R): UDFWrapperBuilderDelegate<UDFWrapper12> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12, v13: T13 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R): UDFWrapperBuilderDelegate<UDFWrapper13> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12, v13: T13, v14: T14 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R): UDFWrapperBuilderDelegate<UDFWrapper14> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12, v13: T13, v14: T14, v15: T15 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R): UDFWrapperBuilderDelegate<UDFWrapper15> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12, v13: T13, v14: T14, v15: T15, v16: T16 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R): UDFWrapperBuilderDelegate<UDFWrapper16> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12, v13: T13, v14: T14, v15: T15, v16: T16, v17: T17 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R): UDFWrapperBuilderDelegate<UDFWrapper17> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12, v13: T13, v14: T14, v15: T15, v16: T16, v17: T17, v18: T18 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R): UDFWrapperBuilderDelegate<UDFWrapper18> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12, v13: T13, v14: T14, v15: T15, v16: T16, v17: T17, v18: T18, v19: T19 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R): UDFWrapperBuilderDelegate<UDFWrapper19> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12, v13: T13, v14: T14, v15: T15, v16: T16, v17: T17, v18: T18, v19: T19, v20: T20 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R): UDFWrapperBuilderDelegate<UDFWrapper20> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12, v13: T13, v14: T14, v15: T15, v16: T16, v17: T17, v18: T18, v19: T19, v20: T20, v21: T21 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R): UDFWrapperBuilderDelegate<UDFWrapper21> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }

/** Creates delegate of [UDFRegistration]. Use like:
 * ```kotlin
 * val myFunction by udf.register { v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8, v9: T9, v10: T10, v11: T11, v12: T12, v13: T13, v14: T14, v15: T15, v16: T16, v17: T17, v18: T18, v19: T19, v20: T20, v21: T21, v22: T22 -> doSomething() }
 * ```
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified R> UDFRegistration.register(asNondeterministic: Boolean = false, asNonNullable: Boolean = false, noinline func: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R): UDFWrapperBuilderDelegate<UDFWrapper22> =
    UDFWrapperBuilderDelegate { name ->
        register(
            name = name,
            asNondeterministic = asNondeterministic,
            asNonNullable = asNonNullable,
            func = func,
        )
    }