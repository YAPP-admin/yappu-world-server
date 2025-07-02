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
        fun from(statuses: List<AttendanceStatus?>): SessionAttendanceStatistics {
            var totalPersonCount = 0
            var totalOnTimeCount = 0
            var totalLateCount = 0
            var totalAbsentCount = 0
            var totalEarlyCheckOutCount = 0
            var totalExcusedAbsenceCount = 0

            statuses.forEach { status ->
                when (status) {
                    AttendanceStatus.ON_TIME -> totalOnTimeCount++
                    AttendanceStatus.LATE -> totalLateCount++
                    AttendanceStatus.ABSENT -> totalAbsentCount++
                    AttendanceStatus.EARLY_CHECK_OUT -> totalEarlyCheckOutCount++
                    AttendanceStatus.EXCUSED_ABSENCE -> totalExcusedAbsenceCount++
                    else -> Unit
                }

                if (status != null) totalPersonCount++
            }

            return SessionAttendanceStatistics(
                totalPersonCount = totalPersonCount,
                totalOnTimeCount = totalOnTimeCount,
                totalLateCount = totalLateCount,
                totalAbsentCount = totalAbsentCount,
                totalEarlyCheckOutCount = totalEarlyCheckOutCount,
                totalExcusedAbsenceCount = totalExcusedAbsenceCount
            )
        }

    }
}
