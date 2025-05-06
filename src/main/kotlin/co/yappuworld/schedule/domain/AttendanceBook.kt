package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.LatePassEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import java.time.LocalDateTime
import java.util.UUID

class AttendanceBook(
    val generation: Int,
    val users: List<UserWithActivityUnit>,
    val sessions: List<SessionEntity>,
    attendances: List<AttendanceEntity>,
    private val latePassCountByUserId: Map<UUID, Int>,
    private val now: LocalDateTime
) {

    // sessionId to userId to AttendanceStatus
    private val bySession: Map<UUID, Map<UUID, AttendanceStatus?>>

    // userId to sessionId to AttendanceStatus
    private val byUser: Map<UUID, Map<UUID, AttendanceStatus?>>

    init {
        require(users.all { it.generation == generation } || sessions.all { it.generation == generation })

        // (userId to sessionId) to AttendanceStatus
        val attendanceMatrix = attendances.associate { (it.userId to it.scheduleId) to it.status }
        val decideStatus: (UserWithActivityUnit, SessionEntity) -> AttendanceStatus? = { user, session ->
            when (session.isFinished(now)) {
                true -> attendanceMatrix[user.userId to session.id] ?: AttendanceStatus.ABSENT
                false -> attendanceMatrix[user.userId to session.id].also { require(it == null) }
            }
        }

        this.bySession = sessions.associate { session ->
            session.id to users.associate { user ->
                user.userId to decideStatus(user, session)
            }
        }

        this.byUser = users.associate { user ->
            user.userId to sessions.associate { session ->
                session.id to decideStatus(user, session)
            }
        }
    }

    constructor(
        generation: Int,
        users: List<UserWithActivityUnit>,
        sessions: List<SessionEntity>,
        attendances: List<AttendanceEntity>,
        latePasses: List<LatePassEntity>,
        now: LocalDateTime
    ) : this(
        generation = generation,
        users = users,
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
        return UserAttendanceStatistics.from(userAttendances.map { it.value }, latePassCountByUserId[userId] ?: 0)
    }

    fun getSessionAttendanceStatistics(sessionId: UUID): SessionAttendanceStatistics {
        val sessionAttendances = bySession[sessionId] ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)
        return SessionAttendanceStatistics.from(sessionAttendances.map { it.value })
    }
}
