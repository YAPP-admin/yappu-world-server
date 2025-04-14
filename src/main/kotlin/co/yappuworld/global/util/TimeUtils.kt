package co.yappuworld.global.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun getCurrentZonedTimeInKST(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))

    fun getCurrentDateTimeInKST(): LocalDateTime = getCurrentZonedTimeInKST().toLocalDateTime()

    fun LocalDate.isBeforeOrEqual(other: LocalDate): Boolean = this.isBefore(other) || this.isEqual(other)

    fun LocalDate.isBetween(
        startInclusive: LocalDate,
        endExclusive: LocalDate
    ): Boolean {
        require(startInclusive.isBefore(endExclusive)) {
            "시작 시간이 종료 시간보다 늦을 수 없습니다."
        }

        return (this.isAfter(startInclusive) || this.isEqual(startInclusive)) && this.isBefore(endExclusive)
    }

    fun LocalDateTime.isBetween(
        startInclusive: LocalDateTime,
        endExclusive: LocalDateTime
    ): Boolean {
        require(startInclusive.isBefore(endExclusive)) {
            "시작 시간이 종료 시간보다 늦을 수 없습니다."
        }

        return (this.isAfter(startInclusive) || this.isEqual(startInclusive)) && this.isBefore(endExclusive)
    }

    class LocalDateRange(
        val start: LocalDate,
        val endExclusive: LocalDate
    ) : Iterable<LocalDate> {

        override fun iterator(): Iterator<LocalDate> {
            return object : Iterator<LocalDate> {
                private var current = start

                override fun hasNext(): Boolean = current <= endExclusive

                override fun next(): LocalDate {
                    if (!hasNext()) throw NoSuchElementException()
                    return current.also { current = current.plusDays(1) }
                }
            }
        }

        operator fun contains(element: LocalDate): Boolean = element.isBetween(start, endExclusive)
    }

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
            }
        }

        operator fun contains(element: LocalDateTime): Boolean = element.isBetween(start, endExclusive)
    }
}
