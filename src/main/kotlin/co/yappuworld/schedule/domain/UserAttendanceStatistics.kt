package co.yappuworld.schedule.domain

import co.yappuworld.schedule.domain.AttendancePolicy.ABSENT_PENALTY
import co.yappuworld.schedule.domain.AttendancePolicy.ATTENDANCE_DEFAULT_POINT
import co.yappuworld.schedule.domain.AttendancePolicy.LATE_PASS_BONUS
import co.yappuworld.schedule.domain.AttendancePolicy.LATE_PENALTY
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.min

data class UserAttendanceStatistics(
    val totalSessionCount: Int,
    val leftSessionCount: Int,
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
            attendanceBySessionId: Map<UUID, AttendanceStatus?>,
            sessionById: Map<UUID, SessionEntity>,
            latePassCount: Int,
            now: LocalDateTime
        ): UserAttendanceStatistics {
            val attendances = attendanceBySessionId.values.toList()

            val totalSessionCount = attendances.count { it != null }
            val finishedSessionCount = attendanceBySessionId.count {
                it.value != null && sessionById[it.key]?.isFinished(now) == true
            }

            val attendanceCountStatistics = AttendanceCountStatistics.from(attendances)
            val attendancePointStatistics = AttendancePointStatistics.from(attendanceCountStatistics, latePassCount)

            return UserAttendanceStatistics(
                totalSessionCount = totalSessionCount,
                leftSessionCount = totalSessionCount - finishedSessionCount,
                onTimeCount = attendanceCountStatistics.onTimeCount,
                lateCount = attendanceCountStatistics.lateCount,
                absentCount = attendanceCountStatistics.absentCount,
                earlyCheckOutCount = attendanceCountStatistics.earlyCheckOutCount,
                excusedAbsenceCount = attendanceCountStatistics.excusedAbsenceCount,
                latePassCount = latePassCount,
                totalPoint = attendancePointStatistics.totalPoint,
                penaltyPoint = attendancePointStatistics.penaltyPoint,
                bonusPoint = attendancePointStatistics.bonusPoint
            )
        }
    }
}

data class AttendanceCountStatistics(
    val onTimeCount: Int,
    val lateCount: Int,
    val absentCount: Int,
    val earlyCheckOutCount: Int,
    val excusedAbsenceCount: Int
) {

    companion object {
        fun from(attendances: List<AttendanceStatus?>): AttendanceCountStatistics {
            var onTimeCount = 0
            var lateCount = 0
            var absentCount = 0
            var earlyCheckOutCount = 0
            var excusedAbsenceCount = 0

            attendances.forEach {
                when (it) {
                    AttendanceStatus.ON_TIME -> onTimeCount++
                    AttendanceStatus.LATE -> lateCount++
                    AttendanceStatus.ABSENT -> absentCount++
                    AttendanceStatus.EARLY_CHECK_OUT -> earlyCheckOutCount++
                    AttendanceStatus.EXCUSED_ABSENCE -> excusedAbsenceCount++
                    else -> Unit
                }
            }

            return AttendanceCountStatistics(
                onTimeCount = onTimeCount,
                lateCount = lateCount,
                absentCount = absentCount,
                earlyCheckOutCount = earlyCheckOutCount,
                excusedAbsenceCount = excusedAbsenceCount
            )
        }
    }
}

data class AttendancePointStatistics(
    val totalPoint: Int,
    val penaltyPoint: Int,
    val bonusPoint: Int
) {

    companion object {
        fun from(
            attendanceCountStatistics: AttendanceCountStatistics,
            latePassCount: Int
        ): AttendancePointStatistics {
            val penaltyPoint =
                (attendanceCountStatistics.lateCount * LATE_PENALTY) +
                    (attendanceCountStatistics.absentCount * ABSENT_PENALTY)
            val bonusPoint = latePassCount * LATE_PASS_BONUS
            val totalPoint = min(ATTENDANCE_DEFAULT_POINT - penaltyPoint + bonusPoint, ATTENDANCE_DEFAULT_POINT)

            return AttendancePointStatistics(
                totalPoint = totalPoint,
                penaltyPoint = penaltyPoint,
                bonusPoint = bonusPoint
            )
        }
    }
}
