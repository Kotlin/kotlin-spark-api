package org.jetbrains.kotlinx.spark.api.udts

import org.apache.spark.sql.catalyst.util.IntervalUtils
import org.apache.spark.sql.types.DataType
import org.apache.spark.sql.types.DayTimeIntervalType
import org.apache.spark.sql.types.UserDefinedType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

// TODO Fails, likely because Duration is a value class.
class DurationUdt : UserDefinedType<Duration>() {

    override fun userClass(): Class<Duration> = Duration::class.java
    override fun deserialize(datum: Any?): Duration? =
        when (datum) {
            null -> null
            is Long -> IntervalUtils.microsToDuration(datum).toKotlinDuration()
//            is Long -> IntervalUtils.microsToDuration(datum).toKotlinDuration().let {
//                // store in nanos
//                it.inWholeNanoseconds shl 1
//            }
            else -> throw IllegalArgumentException("Unsupported datum: $datum")
        }

//    override fun serialize(obj: Duration): Long =
//        IntervalUtils.durationToMicros(obj.toJavaDuration())

    fun serialize(obj: Long): Long? =
        obj?.let { rawValue ->
            val unitDiscriminator = rawValue.toInt() and 1
            fun isInNanos() = unitDiscriminator == 0
            val value = rawValue shr 1
            val duration = if (isInNanos()) value.nanoseconds else value.milliseconds

            IntervalUtils.durationToMicros(duration.toJavaDuration())
        }

    override fun serialize(obj: Duration): Long? =
        obj?.let { IntervalUtils.durationToMicros(it.toJavaDuration()) }


    override fun sqlType(): DataType = DayTimeIntervalType.apply()
}