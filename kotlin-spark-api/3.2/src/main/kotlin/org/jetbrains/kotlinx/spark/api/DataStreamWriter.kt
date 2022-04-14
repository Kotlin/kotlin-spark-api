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