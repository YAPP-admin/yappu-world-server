package co.yappuworld.global.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object TimeUtils {

    val startOfDay: LocalTime = LocalTime.MIN
    val endOfDay: LocalTime = LocalTime.MAX.withNano(999999000)

    fun LocalTime.toMicroPrecision(): LocalTime = this.withNano((this.nano / 1000) * 1000)

    fun getCurrentZonedTimeInKST(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))

    fun getCurrentDateTimeInKST(): LocalDateTime = getCurrentZonedTimeInKST().toLocalDateTime()

    fun LocalDate.isBeforeOrEqual(other: LocalDate): Boolean = this.isBefore(other) || this.isEqual(other)

    fun LocalDate.isAfterOrEqualThan(other: LocalDate): Boolean = this.isAfter(other) || this.isEqual(other)
}
