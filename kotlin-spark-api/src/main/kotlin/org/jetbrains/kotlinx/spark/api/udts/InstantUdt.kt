package org.jetbrains.kotlinx.spark.api.udts

import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.apache.spark.sql.catalyst.util.DateTimeUtils
import org.apache.spark.sql.types.DataType
import org.apache.spark.sql.types.`TimestampType$`
import org.apache.spark.sql.types.UserDefinedType


class InstantUdt : UserDefinedType<Instant>() {

    override fun userClass(): Class<Instant> = Instant::class.java
    override fun deserialize(datum: Any?): Instant? =
        when (datum) {
            null -> null
            is Long -> DateTimeUtils.microsToInstant(datum).toKotlinInstant()
            else -> throw IllegalArgumentException("Unsupported datum: $datum")
        }

    override fun serialize(obj: Instant?): Long? =
        obj?.let { DateTimeUtils.instantToMicros(it.toJavaInstant()) }

    override fun sqlType(): DataType = `TimestampType$`.`MODULE$`
}