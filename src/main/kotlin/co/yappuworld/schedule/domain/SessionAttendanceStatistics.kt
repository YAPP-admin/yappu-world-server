package co.yappuworld.schedule.domain

import co.yappuworld.schedule.domain.vo.AttendanceStatus

data class SessionAttendanceStatistics(
    val totalPersonCount: Int,
    val totalOnTimeCount: Int,
    val totalLateCount: Int,
    val totalAbsentCount: Int,
    val totalEarlyCheckOutCount: Int,
    val totalExcusedAbsenceCount: Int
) {

    companion object {
        fun from(statuses: List<AttendanceStatus?>): SessionAttendanceStatistics =
            SessionAttendanceStatistics(
                totalPersonCount = statuses.size,
                totalOnTimeCount = statuses.count { it == AttendanceStatus.ON_TIME },
                totalLateCount = statuses.count { it == AttendanceStatus.LATE },
                totalAbsentCount = statuses.count { it == AttendanceStatus.ABSENT },
                totalEarlyCheckOutCount = statuses.count { it == AttendanceStatus.EARLY_CHECK_OUT },
                totalExcusedAbsenceCount = statuses.count { it == AttendanceStatus.EXCUSED_ABSENCE }
            )
    }
}
