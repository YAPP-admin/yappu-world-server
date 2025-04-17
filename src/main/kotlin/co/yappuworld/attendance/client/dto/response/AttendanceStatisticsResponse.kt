package co.yappuworld.attendance.client.dto.response

import co.yappuworld.attendance.domain.Attendance
import co.yappuworld.attendance.domain.AttendanceStatus.ABSENT
import co.yappuworld.attendance.domain.AttendanceStatus.LATE
import co.yappuworld.attendance.domain.AttendanceStatus.ON_TIME
import co.yappuworld.schedule.domain.SessionEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class AttendanceStatisticsResponse(
    @Schema(description = "전체 세션 수")
    val totalSessionCount: Int,
    @Schema(description = "남은 세션 수")
    val remainingSessionCount: Int,
    @Schema(description = "세션 진행률")
    val sessionProgressRate: Int,
    @Schema(description = "출석 점수")
    val attendancePoint: Int,
    @Schema(description = "출석한 세션 수")
    val attendanceCount: Int,
    @Schema(description = "지각한 세션 수")
    val lateCount: Int,
    @Schema(description = "결석한 세션 수")
    val absenceCount: Int,
    @Schema(description = "지각 면제권 수")
    val latePassCount: Int
) {

    companion object {
        fun of(
            sessions: List<SessionEntity>,
            attendances: List<Attendance>,
            now: LocalDateTime,
            latePassCount: Int
        ): AttendanceStatisticsResponse {
            val finishedSessions = sessions.filter {
                it.date.isBefore(now.toLocalDate()) ||
                    (it.date.isEqual(now.toLocalDate()) && it.endTime?.isBefore(now.toLocalTime()) ?: false)
            }

            val attendanceBySession = attendances.associateBy { it.scheduleId }

            val defaultPoint = 100
            val attendanceCount = finishedSessions.count { attendanceBySession[it.id]?.status == ON_TIME }
            val lateCount = finishedSessions.count { attendanceBySession[it.id]?.status == LATE }
            val absenceCount = finishedSessions.count { attendanceBySession[it.id]?.status == ABSENT }

            val attendancePoint = defaultPoint - lateCount * 10 - absenceCount * 20 + latePassCount * 10

            return AttendanceStatisticsResponse(
                totalSessionCount = sessions.size,
                remainingSessionCount = sessions.size - finishedSessions.size,
                sessionProgressRate = finishedSessions.size * 100 / sessions.size,
                attendancePoint = attendancePoint,
                attendanceCount = attendanceCount,
                lateCount = lateCount,
                absenceCount = absenceCount,
                latePassCount = latePassCount
            )
        }
    }
}
