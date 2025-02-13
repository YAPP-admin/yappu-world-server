package co.yappuworld.global.util

import java.time.ZoneId
import java.time.ZonedDateTime

object TimeUtils {

    fun getCurrentZonedTimeInKST(): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
    }
}
