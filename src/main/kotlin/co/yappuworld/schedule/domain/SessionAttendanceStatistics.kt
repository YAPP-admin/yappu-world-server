package co.yappuworld.schedule.domain

import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ABSENT
import co.yappuworld.schedule.domain.vo.AttendanceStatus.EARLY_CHECK_OUT
import co.yappuworld.schedule.domain.vo.AttendanceStatus.EXCUSED_ABSENCE
import co.yappuworld.schedule.domain.vo.AttendanceStatus.LATE
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ON_TIME
import co.yappuworld.schedule.domain.vo.AttendanceStatus.PENDING
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import java.time.LocalDateTime

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
                totalOnTimeCount = statuses.count { it == ON_TIME },
                totalLateCount = statuses.count { it == LATE },
                totalAbsentCount = statuses.count { it == ABSENT },
                totalEarlyCheckOutCount = statuses.count { it == EARLY_CHECK_OUT },
                totalExcusedAbsenceCount = statuses.count { it == EXCUSED_ABSENCE }
            )

        fun from(
            session: SessionEntity,
            sessionParticipants: List<SessionParticipant>,
            now: LocalDateTime
        ): SessionAttendanceStatistics {
            val sessionParticipationBySessionId = sessionParticipants.associateBy { it.userId }

            var totalOnTimeCount = 0
            var totalLateCount = 0
            var totalAbsentCount = 0
            var totalEarlyCheckOutCount = 0
            var totalExcusedAbsenceCount = 0
            sessionParticipationBySessionId.forEach { (sessionId, sessionParticipant) ->
                when (sessionParticipant.attendanceStatus) {
                    PENDING -> if (session.isFinished(now)) totalAbsentCount++
                    ON_TIME -> totalOnTimeCount++
                    LATE -> totalLateCount++
                    ABSENT -> totalAbsentCount++
                    EARLY_CHECK_OUT -> totalEarlyCheckOutCount++
                    EXCUSED_ABSENCE -> totalExcusedAbsenceCount++
                }
            }

            return SessionAttendanceStatistics(
                totalPersonCount = sessionParticipationBySessionId.size,
                totalOnTimeCount = totalOnTimeCount,
                totalLateCount = totalLateCount,
                totalAbsentCount = totalAbsentCount,
                totalEarlyCheckOutCount = totalEarlyCheckOutCount,
                totalExcusedAbsenceCount = totalExcusedAbsenceCount
            )
        }
    }
}
