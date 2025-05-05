package co.yappuworld.schedule.domain

import co.yappuworld.schedule.domain.AttendancePolicy.ABSENT_PENALTY
import co.yappuworld.schedule.domain.AttendancePolicy.ATTENDANCE_DEFAULT_POINT
import co.yappuworld.schedule.domain.AttendancePolicy.LATE_PASS_BONUS
import co.yappuworld.schedule.domain.AttendancePolicy.LATE_PENALTY
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import kotlin.math.min

data class UserAttendanceStatistics(
    val totalSessionCount: Int,
    val onTimeCount: Int,
    val lateCount: Int,
    val absentCount: Int,
    val earlyCheckOutCount: Int,
    val excusedAbsenceCount: Int,
    val latePassCount: Int,
    val totalPoint: Int,
    val penaltyPoint: Int,
    val bonusPoint: Int
) {

    companion object {
        fun from(
            attendances: List<AttendanceStatus?>,
            latePassCount: Int
        ): UserAttendanceStatistics {
            val totalSessionCount = attendances.size
            val onTimeCount = attendances.count { it == AttendanceStatus.ON_TIME }
            val lateCount = attendances.count { it == AttendanceStatus.LATE }
            val absentCount = attendances.count { it == AttendanceStatus.ABSENT }
            val earlyCheckOutCount = attendances.count { it == AttendanceStatus.EARLY_CHECK_OUT }
            val excusedAbsenceCount = attendances.count { it == AttendanceStatus.EXCUSED_ABSENCE }

            val penaltyPoint = (lateCount * LATE_PENALTY) + (absentCount * ABSENT_PENALTY)
            val bonusPoint = latePassCount * LATE_PASS_BONUS
            val totalPoint = min(ATTENDANCE_DEFAULT_POINT - penaltyPoint + bonusPoint, ATTENDANCE_DEFAULT_POINT)

            return UserAttendanceStatistics(
                totalSessionCount = totalSessionCount,
                onTimeCount = onTimeCount,
                lateCount = lateCount,
                absentCount = absentCount,
                earlyCheckOutCount = earlyCheckOutCount,
                excusedAbsenceCount = excusedAbsenceCount,
                latePassCount = latePassCount,
                totalPoint = totalPoint,
                penaltyPoint = penaltyPoint,
                bonusPoint = bonusPoint
            )
        }
    }
}
