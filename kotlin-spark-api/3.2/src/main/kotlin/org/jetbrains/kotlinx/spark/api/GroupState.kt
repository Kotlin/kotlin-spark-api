package org.jetbrains.kotlinx.spark.api

import org.apache.spark.sql.streaming.GroupState
import kotlin.reflect.KProperty

/**
 * (Kotlin-specific)
 * Returns the group state value if it exists, else [null].
 * This is comparable to [GroupState.getOption], but instead utilises Kotlin's nullability features
 * to get the same result.
 */
fun <S> GroupState<S>.getOrNull(): S? = if (exists()) get() else null

/**
 * (Kotlin-specific)
 * Allows the group state object to be used as a delegate. Will be [null] if it does not exist.
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
 * Allows the group state object to be used as a delegate. Will be [null] if it does not exist.
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
