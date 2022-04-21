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

import org.apache.spark.api.java.function.VoidFunction2
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.streaming.DataStreamWriter

/**
 * :: Experimental ::
 *
 * (Scala-specific) Sets the output of the streaming query to be processed using the provided
 * function. This is supported only in the micro-batch execution modes (that is, when the
 * trigger is not continuous). In every micro-batch, the provided function will be called in
 * every micro-batch with (i) the output rows as a Dataset and (ii) the batch identifier.
 * The batchId can be used to deduplicate and transactionally write the output
 * (that is, the provided Dataset) to external systems. The output Dataset is guaranteed
 * to be exactly the same for the same batchId (assuming all operations are deterministic
 * in the query).
 *
 * @since 2.4.0
 */
fun <T> DataStreamWriter<T>.forEachBatch(
    func: (batch: Dataset<T>, batchId: Long) -> Unit,
): DataStreamWriter<T> = foreachBatch(VoidFunction2(func))
