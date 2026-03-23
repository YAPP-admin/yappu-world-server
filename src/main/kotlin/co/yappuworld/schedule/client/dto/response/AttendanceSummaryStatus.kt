package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.UserAttendanceStatistics

enum class AttendanceSummaryStatus {
    GOOD,
    CAUTION,
    INCOMPLETE;

    companion object {
        fun from(statistics: UserAttendanceStatistics): AttendanceSummaryStatus =
            when {
                statistics.totalPoint < 70 -> INCOMPLETE
                statistics.totalPoint < 80 -> CAUTION
                else -> GOOD
            }
    }
}
