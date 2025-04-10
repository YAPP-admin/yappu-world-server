package co.yappuworld.global.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object TimeUtils {

    fun LocalTime.toMicroPrecision(): LocalTime = this.withNano((this.nano / 1000) * 1000)

    fun getCurrentZonedTimeInKST(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))

    fun getCurrentDateTimeInKST(): LocalDateTime = getCurrentZonedTimeInKST().toLocalDateTime()

    fun LocalDate.isBeforeOrEqual(other: LocalDate): Boolean = this.isBefore(other) || this.isEqual(other)

    fun LocalDate.isAfterOrEqualThan(other: LocalDate): Boolean = this.isAfter(other) || this.isEqual(other)

    fun LocalDateTime.isBetween(
        startInclusive: LocalDateTime,
        endExclusive: LocalDateTime
    ): Boolean {
        if (startInclusive.isAfter(endExclusive)) {
            throw IllegalArgumentException("시작 시간이 종료 시간보다 늦을 수 없습니다.")
        }

        return (this.isAfter(startInclusive) || this.isEqual(startInclusive)) && this.isBefore(endExclusive)
    }
}
