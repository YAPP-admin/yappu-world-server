package co.yappuworld.schedule.client.dto.response

import co.yappuworld.attendance.domain.AttendanceStatus
import co.yappuworld.global.util.TimeUtils.isBeforeOrEqual
import co.yappuworld.schedule.domain.SessionProgressPhase
import co.yappuworld.schedule.domain.SessionProgressPhase.DONE
import co.yappuworld.schedule.domain.SessionProgressPhase.PENDING
import co.yappuworld.schedule.domain.SessionProgressPhase.TODAY
import co.yappuworld.schedule.domain.SessionProgressPhase.UPCOMING
import co.yappuworld.schedule.domain.SessionType
import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendance
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class ActiveGenerationSessionsResponse(
    @Schema(description = "활동 중인 기수의 세션 목록, 데이터가 없다면 빈 리스트 반환")
    val sessions: List<ActiveGenerationSessionResponse>,
    @Schema(
        description = """
            가장 가까이 예정된 세션의 인덱스
            모든 세션이 종료됐다면 마지막 인덱스 반환
            빈 리스트인 경우 null
        """,
        nullable = true
    )
    val upcomingSessionId: UUID? = null
) {

    companion object {
        fun from(
            sessions: List<SessionWithAttendance>,
            now: LocalDate
        ): ActiveGenerationSessionsResponse {
            if (sessions.isEmpty()) return ActiveGenerationSessionsResponse(emptyList(), null)

            val orderedSessions = sessions.sortedBy { it.date }
            val (upcomingSessionIndex, upcomingSessionStatus) = getUpcomingSessionIndexAndStatus(orderedSessions, now)
                ?: return ActiveGenerationSessionsResponse(
                    sessions.map { ActiveGenerationSessionResponse.from(it, DONE) },
                    sessions.last().id
                )

            return ActiveGenerationSessionsResponse(
                sessions = sessions.mapIndexed { index, session ->
                    when {
                        index < upcomingSessionIndex -> ActiveGenerationSessionResponse.from(session, DONE)
                        index == upcomingSessionIndex -> ActiveGenerationSessionResponse.from(
                            session,
                            upcomingSessionStatus
                        )
                        else -> ActiveGenerationSessionResponse.from(session, PENDING)
                    }
                },
                upcomingSessionId = sessions[upcomingSessionIndex].id
            )
        }

        private fun getUpcomingSessionIndexAndStatus(
            orderedSessions: List<SessionWithAttendance>,
            now: LocalDate
        ): Pair<Int, SessionProgressPhase>? {
            val upcomingSession = orderedSessions.firstOrNull { now.isBeforeOrEqual(it.date) }
                ?: return null
            val upcomingSessionIndex = orderedSessions.indexOf(upcomingSession)

            return when {
                upcomingSession.date.isEqual(now) -> upcomingSessionIndex to TODAY
                else -> upcomingSessionIndex to UPCOMING
            }
        }
    }
}

data class ActiveGenerationSessionResponse(
    @Schema(description = "세션 식별자")
    val id: UUID,
    @Schema(description = "세션 이름")
    val name: String,
    @Schema(description = "세션 장소", nullable = true)
    val place: String?,
    @Schema(description = "세션 시작일", nullable = true)
    val date: LocalDate,
    @Schema(description = "세션 종료일", nullable = true)
    val endDate: LocalDate?,
    @Schema(description = "세션 시작 시간", nullable = true)
    val time: LocalTime?,
    @Schema(description = "세션 종료 시간", nullable = true)
    val endTime: LocalTime?,
    @Schema(description = "세션 타입")
    val type: SessionType,
    @Schema(description = "세션 진행 상태")
    val progressPhase: SessionProgressPhase,
    @Schema(description = "출석 상태", nullable = true, allowableValues = ["출석", "지각", "결석", "조퇴", "공결"])
    val attendanceStatus: String?
) {

    companion object {

        fun from(
            session: SessionWithAttendance,
            status: SessionProgressPhase
        ): ActiveGenerationSessionResponse {
            val attendanceStatus = if (session.attendanceStatus == null && status == DONE) {
                AttendanceStatus.ABSENT
            } else {
                session.attendanceStatus
            }

            return ActiveGenerationSessionResponse(
                id = session.id,
                name = session.name,
                place = session.place,
                date = session.date,
                endDate = session.endDate,
                time = session.time,
                endTime = session.endTime,
                type = session.sessionType,
                progressPhase = status,
                attendanceStatus = attendanceStatus?.let { it.label }
            )
        }
    }
}
