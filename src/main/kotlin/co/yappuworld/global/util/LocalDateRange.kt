package co.yappuworld.global.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class LocalDateRange(
    val start: LocalDate,
    val endInclusive: LocalDate
) : Iterable<LocalDate> {

    override fun iterator(): Iterator<LocalDate> {
        return object : Iterator<LocalDate> {
            private var current = start

            override fun hasNext(): Boolean = current <= endInclusive

            override fun next(): LocalDate {
                if (!hasNext()) throw NoSuchElementException()
                return current.also { current = current.plusDays(1) }
            }
        }
    }
}

class LocalDateTimeRange(
    val start: LocalDateTime,
    val endInclusive: LocalDateTime,
    val unit: TimeUnit = TimeUnit.MINUTES
) : Iterable<LocalDateTime> {

    override fun iterator(): Iterator<LocalDateTime> {
        return object : Iterator<LocalDateTime> {
            private var current = start

            override fun hasNext(): Boolean = current <= endInclusive

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
        }
    }
}
