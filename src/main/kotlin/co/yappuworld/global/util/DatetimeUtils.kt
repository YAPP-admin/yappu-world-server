package co.yappuworld.global.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object DatetimeUtils {

    fun getCurrentZonedTimeInKST(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))

    fun getCurrentDateTimeInKST(): LocalDateTime = getCurrentZonedTimeInKST().toLocalDateTime()

    fun LocalDate.isBeforeOrEqual(other: LocalDate): Boolean = this.isBefore(other) || this.isEqual(other)

    fun LocalDate.isAfterOrEqual(other: LocalDate): Boolean = this.isAfter(other) || this.isEqual(other)

    fun LocalDate.isBetween(
        start: LocalDate,
        end: LocalDate
    ): Boolean = this.isAfterOrEqual(start) && this.isBeforeOrEqual(end)

    fun LocalDateTime.isBeforeOrEqual(other: LocalDateTime): Boolean = this.isBefore(other) || this.isEqual(other)

    fun LocalDateTime.isAfterOrEqual(other: LocalDateTime): Boolean = this.isAfter(other) || this.isEqual(other)

    fun LocalDateTime.isBetween(
        start: LocalDateTime,
        endExclusive: LocalDateTime
    ): Boolean = this.isAfterOrEqual(start) && this.isBefore(endExclusive)

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

    /**
     * this 기준일
     * other 비교일
     * 예를 들어, this = LocalDate(2024, 12, 12), other = LocalDate(2024, 12, 11) 이면 -1 반환
     */
    fun LocalDate.dDayFrom(other: LocalDate): Long = ChronoUnit.DAYS.between(this, other)
}
