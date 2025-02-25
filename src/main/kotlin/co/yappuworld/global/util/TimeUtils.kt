package co.yappuworld.global.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

object TimeUtils {

    fun getCurrentZonedTimeInKST(): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
    }

    fun getCurrentDateTimeInKST(): LocalDateTime {
        return getCurrentZonedTimeInKST().toLocalDateTime()
    }
}
