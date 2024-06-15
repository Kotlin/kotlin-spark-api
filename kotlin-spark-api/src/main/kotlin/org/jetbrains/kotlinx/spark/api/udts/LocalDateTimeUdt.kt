package org.jetbrains.kotlinx.spark.api.udts

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.apache.spark.sql.catalyst.util.DateTimeUtils
import org.apache.spark.sql.types.DataType
import org.apache.spark.sql.types.`TimestampNTZType$`
import org.apache.spark.sql.types.UserDefinedType


class LocalDateTimeUdt : UserDefinedType<LocalDateTime>() {

    override fun userClass(): Class<LocalDateTime> = LocalDateTime::class.java
    override fun deserialize(datum: Any?): LocalDateTime? =
        when (datum) {
            null -> null
            is Long -> DateTimeUtils.microsToLocalDateTime(datum).toKotlinLocalDateTime()
            else -> throw IllegalArgumentException("Unsupported datum: $datum")
        }

    override fun serialize(obj: LocalDateTime?): Long? =
        obj?.let { DateTimeUtils.localDateTimeToMicros(it.toJavaLocalDateTime()) }

    override fun sqlType(): DataType = `TimestampNTZType$`.`MODULE$`
}