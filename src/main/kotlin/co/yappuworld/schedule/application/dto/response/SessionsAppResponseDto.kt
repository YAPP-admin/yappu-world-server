package co.yappuworld.schedule.application.dto.response

import co.yappuworld.global.util.TimeUtils.isBeforeOrEqual
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionProgressPhase
import co.yappuworld.schedule.domain.SessionProgressPhase.DONE
import co.yappuworld.schedule.domain.SessionProgressPhase.PENDING
import co.yappuworld.schedule.domain.SessionProgressPhase.TODAY
import co.yappuworld.schedule.domain.SessionProgressPhase.UPCOMING
import co.yappuworld.schedule.domain.SessionType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class SessionsAppResponseDto(
    val sessions: List<SessionAppResponseDto>,
    val upcomingSessionIndex: Int? = null
) {

    companion object {
        fun from(
            sessions: List<SessionEntity>,
            now: LocalDate
        ): SessionsAppResponseDto {
            if (sessions.isEmpty()) return SessionsAppResponseDto(emptyList(), null)

            val orderedSessions = sessions.sortedBy { it.date }
            val (upcomingSessionIndex, upcomingSessionStatus) = getUpcomingSessionIndexAndStatus(orderedSessions, now)
                ?: return SessionsAppResponseDto(sessions.map { SessionAppResponseDto(it, DONE) }, sessions.lastIndex)

            return SessionsAppResponseDto(
                sessions = sessions.mapIndexed { index, session ->
                    when {
                        index < upcomingSessionIndex -> SessionAppResponseDto(session, DONE)
                        index == upcomingSessionIndex -> SessionAppResponseDto(session, upcomingSessionStatus)
                        else -> SessionAppResponseDto(session, PENDING)
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

data class SessionAppResponseDto(
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
