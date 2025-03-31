package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.util.TimeUtils.isBeforeOrEqual
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionProgressPhase
import co.yappuworld.schedule.domain.SessionProgressPhase.DONE
import co.yappuworld.schedule.domain.SessionProgressPhase.PENDING
import co.yappuworld.schedule.domain.SessionProgressPhase.TODAY
import co.yappuworld.schedule.domain.SessionProgressPhase.UPCOMING
import co.yappuworld.schedule.domain.SessionType
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
    val upcomingSessionIndex: Int? = null
) {

    companion object {
        fun from(
            sessions: List<SessionEntity>,
            now: LocalDate
        ): ActiveGenerationSessionsResponse {
            if (sessions.isEmpty()) return ActiveGenerationSessionsResponse(emptyList(), null)

            val orderedSessions = sessions.sortedBy { it.date }
            val (upcomingSessionIndex, upcomingSessionStatus) = getUpcomingSessionIndexAndStatus(orderedSessions, now)
                ?: return ActiveGenerationSessionsResponse(
                    sessions.map { ActiveGenerationSessionResponse(it, DONE) },
                    sessions.lastIndex
                )

            return ActiveGenerationSessionsResponse(
                sessions = sessions.mapIndexed { index, session ->
                    when {
                        index < upcomingSessionIndex -> ActiveGenerationSessionResponse(session, DONE)
                        index == upcomingSessionIndex -> ActiveGenerationSessionResponse(session, upcomingSessionStatus)
                        else -> ActiveGenerationSessionResponse(session, PENDING)
                    }
                },
                upcomingSessionIndex = upcomingSessionIndex
            )
        }

        private fun getUpcomingSessionIndexAndStatus(
            orderedSessions: List<SessionEntity>,
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
    val id: UUID,
    val name: String,
    val place: String?,
    val date: LocalDate,
    val endDate: LocalDate?,
    val time: LocalTime?,
    val endTime: LocalTime?,
    val type: SessionType,
    val progressPhase: SessionProgressPhase
) {

    constructor(session: SessionEntity, status: SessionProgressPhase) : this(
        id = session.id,
        name = session.name,
        place = session.place,
        date = session.date,
        endDate = session.endDate,
        time = session.time,
        endTime = session.endTime,
        type = session.sessionType,
        progressPhase = status
    )
}
