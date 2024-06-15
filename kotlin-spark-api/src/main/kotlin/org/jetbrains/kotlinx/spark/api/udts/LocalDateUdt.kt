package org.jetbrains.kotlinx.spark.api.udts

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.apache.spark.sql.catalyst.util.DateTimeUtils
import org.apache.spark.sql.types.DataType
import org.apache.spark.sql.types.`DateType$`
import org.apache.spark.sql.types.UserDefinedType


class LocalDateUdt : UserDefinedType<LocalDate>() {

    override fun userClass(): Class<LocalDate> = LocalDate::class.java
    override fun deserialize(datum: Any?): LocalDate? =
        when (datum) {
            null -> null
            is Int -> DateTimeUtils.daysToLocalDate(datum).toKotlinLocalDate()
            else -> throw IllegalArgumentException("Unsupported datum: $datum")
        }

    override fun serialize(obj: LocalDate?): Int? =
        obj?.let { DateTimeUtils.localDateToDays(it.toJavaLocalDate()) }

    override fun sqlType(): DataType = `DateType$`.`MODULE$`
}