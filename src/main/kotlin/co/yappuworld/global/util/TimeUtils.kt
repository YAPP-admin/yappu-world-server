package co.yappuworld.global.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

object TimeUtils {

    fun getCurrentZonedTimeInKST(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))

    fun getCurrentDateTimeInKST(): LocalDateTime = getCurrentZonedTimeInKST().toLocalDateTime()

    fun LocalDate.isBeforeOrEqualThan(other: LocalDate): Boolean = this.isBefore(other) || this.isEqual(other)

    fun LocalDate.isAfterOrEqualThan(other: LocalDate): Boolean = this.isAfter(other) || this.isEqual(other)
}
