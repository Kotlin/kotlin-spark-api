package org.jetbrains.kotlinx.spark.api.udts

import kotlinx.datetime.DateTimePeriod
import org.apache.spark.sql.types.CalendarIntervalType
import org.apache.spark.sql.types.`CalendarIntervalType$`
import org.apache.spark.sql.types.UserDefinedType
import org.apache.spark.unsafe.types.CalendarInterval
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

/**
 * NOTE: Just like java.time.DatePeriod, this is truncated to months.
 */
class DateTimePeriodUdt : UserDefinedType<DateTimePeriod>() {

    override fun userClass(): Class<DateTimePeriod> = DateTimePeriod::class.java
    override fun deserialize(datum: Any?): DateTimePeriod? =
        when (datum) {
            null -> null
            is CalendarInterval ->
                DateTimePeriod(
                    months = datum.months,
                    days = datum.days,
                    nanoseconds = datum.microseconds * 1_000,
                )

            else -> throw IllegalArgumentException("Unsupported datum: $datum")
        }

    override fun serialize(obj: DateTimePeriod?): CalendarInterval? =
        obj?.let {
            CalendarInterval(
                /* months = */ obj.months + obj.years * 12,
                /* days = */ obj.days,
                /* microseconds = */
                (obj.hours.hours +
                        obj.minutes.minutes +
                        obj.seconds.seconds +
                        obj.nanoseconds.nanoseconds).inWholeMicroseconds,
            )
        }

    override fun sqlType(): CalendarIntervalType = `CalendarIntervalType$`.`MODULE$`
}