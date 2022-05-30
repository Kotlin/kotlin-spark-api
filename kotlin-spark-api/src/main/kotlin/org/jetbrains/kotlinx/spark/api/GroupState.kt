/*-
 * =LICENSE=
 * Kotlin Spark API: API for Spark 3.0+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2021 JetBrains
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

/**
 * This file contains some helper functions to more easily work with [GroupState] from Kotlin.
 */

package org.jetbrains.kotlinx.spark.api

import org.apache.spark.sql.streaming.GroupState
import kotlin.reflect.KProperty

/**
 * (Kotlin-specific)
 * Returns the group state value if it exists, else `null`.
 * This is comparable to [GroupState.getOption], but instead utilises Kotlin's nullability features
 * to get the same result.
 */
fun <S> GroupState<S>.getOrNull(): S? = if (exists()) get() else null

/**
 * (Kotlin-specific)
 * Allows the group state object to be used as a delegate. Will be `null` if it does not exist.
 *
 * For example:
 * ```kotlin
 * groupedDataset.mapGroupsWithState(GroupStateTimeout.NoTimeout()) { key, values, state: GroupState<Int> ->
 *     var s by state
 *     ...
 * }
 * ```
 */
operator fun <S> GroupState<S>.getValue(thisRef: Any?, property: KProperty<*>): S? = getOrNull()

/**
 * (Kotlin-specific)
 * Allows the group state object to be used as a delegate. Will be `null` if it does not exist.
 *
 * For example:
 * ```kotlin
 * groupedDataset.mapGroupsWithState(GroupStateTimeout.NoTimeout()) { key, values, state: GroupState<Int> ->
 *     var s by state
 *     ...
 * }
 * ```
 */
operator fun <S> GroupState<S>.setValue(thisRef: Any?, property: KProperty<*>, value: S?): Unit = update(value)
