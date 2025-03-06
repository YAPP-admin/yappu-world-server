package co.yappuworld.global.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

object TimeUtils {

    fun getCurrentZonedTimeInKST(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))

    fun getCurrentDateTimeInKST(): LocalDateTime = getCurrentZonedTimeInKST().toLocalDateTime()
}
