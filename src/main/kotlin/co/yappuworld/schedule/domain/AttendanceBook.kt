package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.LatePassEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import java.time.LocalDateTime
import java.util.UUID

class AttendanceBook(
    val generation: Int,
    val attendees: List<Attendee>,
    val sessions: List<SessionEntity>,
    attendances: List<AttendanceEntity>,
    private val latePassCountByUserId: Map<UUID, Int>,
    private val now: LocalDateTime
) {

    // sessionId to userId to AttendanceStatus
    private val bySession: Map<UUID, Map<UUID, AttendanceStatus?>>

    // userId to sessionId to AttendanceStatus
    private val byUser: Map<UUID, Map<UUID, AttendanceStatus?>>

    val finishedSessionCount = sessions.count { it.isFinished(now) }

    init {
        require(sessions.all { it.generation == generation })

        // (userId to sessionId) to AttendanceStatus
        val attendanceMatrix = attendances.associate { (it.userId to it.scheduleId) to it.status }

        fun decideStatus(
            user: Attendee,
            session: SessionEntity
        ): AttendanceStatus? =
            when (session.isFinished(now)) {
                true -> attendanceMatrix[user.id to session.id] ?: AttendanceStatus.ABSENT
                false -> attendanceMatrix[user.id to session.id]
            }

        this.bySession = sessions.associate { session ->
            session.id to attendees.associate { user ->
                user.id to decideStatus(user, session)
            }
        }

        this.byUser = attendees.associate { user ->
            user.id to sessions.associate { session ->
                session.id to decideStatus(user, session)
            }
        }
    }

    constructor(
        generation: Int,
        attendees: List<Attendee>,
        sessions: List<SessionEntity>,
        attendances: List<AttendanceEntity>,
        latePasses: List<LatePassEntity>,
        now: LocalDateTime
    ) : this(
        generation = generation,
        attendees = attendees,
        sessions = sessions,
        attendances = attendances,
        latePassCountByUserId = latePasses
            .groupBy { it.userId }
            .mapValues { (_, latePasses) -> latePasses.size },
        now = now
    )

    fun getSessionStatuses(sessionId: UUID): Map<UUID, AttendanceStatus?> =
        bySession[sessionId] ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)

    fun getStatus(
        sessionId: UUID,
        userId: UUID
    ): AttendanceStatus? {
        if (!bySession.containsKey(sessionId)) throw BusinessException(AttendanceError.SESSION_NOT_FOUND)
        val sessionAttendances = bySession[sessionId] ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)
        if (!sessionAttendances.containsKey(userId)) throw BusinessException(AttendanceError.USER_NOT_FOUND)
        return sessionAttendances[userId]
    }

    fun getUserAttendanceStatistics(userId: UUID): UserAttendanceStatistics {
        val userAttendances = byUser[userId] ?: throw BusinessException(AttendanceError.USER_NOT_FOUND)
        return UserAttendanceStatistics.from(
            userAttendances.map { it.value },
            finishedSessionCount,
            latePassCountByUserId[userId] ?: 0
        )
    }

    fun getSessionAttendanceStatistics(sessionId: UUID): SessionAttendanceStatistics {
        val sessionAttendances = bySession[sessionId] ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)
        return SessionAttendanceStatistics.from(sessionAttendances.map { it.value })
    }
}
