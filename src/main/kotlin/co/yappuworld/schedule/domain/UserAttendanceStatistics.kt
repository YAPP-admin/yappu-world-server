package co.yappuworld.schedule.domain

import co.yappuworld.schedule.domain.AttendancePolicy.ABSENT_PENALTY
import co.yappuworld.schedule.domain.AttendancePolicy.ATTENDANCE_DEFAULT_POINT
import co.yappuworld.schedule.domain.AttendancePolicy.LATE_PASS_BONUS
import co.yappuworld.schedule.domain.AttendancePolicy.LATE_PENALTY
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ABSENT
import co.yappuworld.schedule.domain.vo.AttendanceStatus.EARLY_CHECK_OUT
import co.yappuworld.schedule.domain.vo.AttendanceStatus.EXCUSED_ABSENCE
import co.yappuworld.schedule.domain.vo.AttendanceStatus.LATE
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ON_TIME
import co.yappuworld.schedule.domain.vo.AttendanceStatus.PENDING
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import java.time.LocalDateTime
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
            attendances: List<AttendanceStatus?>,
            finishedSessionCount: Int,
            latePassCount: Int
        ): UserAttendanceStatistics {
            val totalSessionCount = attendances.size
            val leftSessionCount = totalSessionCount - finishedSessionCount
            val onTimeCount = attendances.count { it == ON_TIME }
            val lateCount = attendances.count { it == LATE }
            val absentCount = attendances.count { it == ABSENT }
            val earlyCheckOutCount = attendances.count { it == EARLY_CHECK_OUT }
            val excusedAbsenceCount = attendances.count { it == EXCUSED_ABSENCE }

            val penaltyPoint = (lateCount * LATE_PENALTY) + (absentCount * ABSENT_PENALTY)
            val bonusPoint = latePassCount * LATE_PASS_BONUS
            val totalPoint = min(ATTENDANCE_DEFAULT_POINT - penaltyPoint + bonusPoint, ATTENDANCE_DEFAULT_POINT)

            return UserAttendanceStatistics(
                totalSessionCount = totalSessionCount,
                leftSessionCount = leftSessionCount,
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

        fun from(
            sessions: List<SessionEntity>,
            sessionParticipants: List<SessionParticipant>,
            latePassCount: Int,
            now: LocalDateTime
        ): UserAttendanceStatistics {
            val sessionById = sessions.associateBy { it.id }
            val sessionParticipationBySessionId = sessionParticipants.associateBy { it.sessionId }

            val totalSessionCount = sessionParticipationBySessionId.size
            val finishedSessionCount = sessionParticipationBySessionId.count { (participatedSessionId, _) ->
                sessionById[participatedSessionId]?.isFinished(now) == true
            }
            val leftSessionCount = totalSessionCount - finishedSessionCount

            var onTimeCount = 0
            var lateCount = 0
            var absentCount = 0
            var earlyCheckOutCount = 0
            var excusedAbsenceCount = 0
            sessionParticipationBySessionId.forEach { (sessionId, sessionParticipant) ->
                when (sessionParticipant.attendanceStatus) {
                    PENDING -> if (sessionById[sessionId]?.isFinished(now) == true) absentCount++
                    ON_TIME -> onTimeCount++
                    LATE -> lateCount++
                    ABSENT -> absentCount++
                    EARLY_CHECK_OUT -> earlyCheckOutCount++
                    EXCUSED_ABSENCE -> excusedAbsenceCount++
                }
            }

            val penaltyPoint = (lateCount * LATE_PENALTY) + (absentCount * ABSENT_PENALTY)
            val bonusPoint = latePassCount * LATE_PASS_BONUS
            val totalPoint = min(ATTENDANCE_DEFAULT_POINT - penaltyPoint + bonusPoint, ATTENDANCE_DEFAULT_POINT)

            return UserAttendanceStatistics(
                totalSessionCount = totalSessionCount,
                leftSessionCount = leftSessionCount,
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
