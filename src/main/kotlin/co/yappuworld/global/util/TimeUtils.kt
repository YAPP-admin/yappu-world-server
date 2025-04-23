package co.yappuworld.global.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun getCurrentZonedTimeInKST(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))

    fun getCurrentDateTimeInKST(): LocalDateTime = getCurrentZonedTimeInKST().toLocalDateTime()

    fun LocalDate.isBeforeOrEqual(other: LocalDate): Boolean = this.isBefore(other) || this.isEqual(other)

    fun LocalDate.isAfterOrEqual(other: LocalDate): Boolean = this.isAfter(other) || this.isEqual(other)

    fun LocalDateTime.isBeforeOrEqual(other: LocalDateTime): Boolean = this.isBefore(other) || this.isEqual(other)

    fun LocalDateTime.isAfterOrEqual(other: LocalDateTime): Boolean = this.isAfter(other) || this.isEqual(other)

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

        operator fun rangeTo(other: LocalDate): LocalDateRange = LocalDateRange(this.start, other.plusDays(1))

        operator fun rangeUntil(other: LocalDate): LocalDateRange = LocalDateRange(this.start, other)
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

        operator fun contains(element: LocalDateTime): Boolean = element.isBetween(start, endExclusive)
    }

    fun DayOfWeek.korean() =
        when (this) {
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
            DayOfWeek.SUNDAY -> "일"
        }
}
