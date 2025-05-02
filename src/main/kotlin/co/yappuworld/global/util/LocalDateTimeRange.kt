package co.yappuworld.global.util

import co.yappuworld.global.util.TimeUtils.isBeforeOrEqual
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class LocalDateTimeRange(
    val start: LocalDateTime,
    val endExclusive: LocalDateTime,
    val unit: TimeUnit = TimeUnit.MINUTES
) : Iterable<LocalDateTime> {

    override fun iterator(): Iterator<LocalDateTime> {
        return object : Iterator<LocalDateTime> {
            private var current = start

            override fun hasNext(): Boolean = current < endExclusive

            override fun next(): LocalDateTime {
                if (!hasNext()) throw NoSuchElementException()
                return current.also {
                    current = when (unit) {
                        TimeUnit.SECONDS -> current.plusSeconds(1)
                        TimeUnit.MINUTES -> current.plusMinutes(1)
                        TimeUnit.HOURS -> current.plusHours(1)
                        TimeUnit.DAYS -> current.plusDays(1)
                        else -> throw IllegalArgumentException("Unsupported time unit: $unit")
                    }
                }
            }

            operator fun rangeTo(other: LocalDateTime): LocalDateTimeRange {
                val otherTime = when (unit) {
                    TimeUnit.NANOSECONDS -> other.plusNanos(1)
                    TimeUnit.MICROSECONDS -> other.plusNanos(1000)
                    TimeUnit.MILLISECONDS -> other.plusNanos(1000000)
                    TimeUnit.SECONDS -> other.plusSeconds(1)
                    TimeUnit.MINUTES -> other.plusMinutes(1)
                    TimeUnit.HOURS -> other.plusHours(1)
                    TimeUnit.DAYS -> other.plusDays(1)
                }
                return LocalDateTimeRange(this.current, otherTime, unit)
            }

            operator fun rangeUntil(other: LocalDateTime): LocalDateTimeRange =
                LocalDateTimeRange(this.current, other, unit)
        }
    }

    operator fun contains(element: LocalDateTime): Boolean =
        start.isBeforeOrEqual(element) && element.isBeforeOrEqual(endExclusive)
}
