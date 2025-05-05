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
    users: List<UserWithActivityUnit>,
    sessionEntities: List<SessionEntity>,
    attendanceEntities: List<AttendanceEntity>,
    latePassEntities: List<LatePassEntity>,
    private val now: LocalDateTime
) {

    // sessionId to userId to AttendanceStatus
    private val bySession: Map<UUID, Map<UUID, AttendanceStatus?>>

    // userId to sessionId to AttendanceStatus
    private val byUser: Map<UUID, Map<UUID, AttendanceStatus?>>

    // userId to latePass
    private val latePassByUserId = latePassEntities.groupBy { it.userId }

    init {
        require(users.all { it.generation == generation } || sessionEntities.all { it.generation == generation })

        // (userId to sessionId) to AttendanceStatus
        val attendanceMatrix = attendanceEntities.associate { (it.userId to it.scheduleId) to it.status }

        this.bySession = sessionEntities.associate { session ->
            session.id to users.associate { user ->
                val status = attendanceMatrix[user.userId to session.id]
                    ?: if (session.isFinished(now)) AttendanceStatus.ABSENT else null
                user.userId to status
            }
        }

        this.byUser = users.associate { user ->
            user.userId to sessionEntities.associate { session ->
                val status = attendanceMatrix[user.userId to session.id]
                    ?: if (session.isFinished(now)) AttendanceStatus.ABSENT else null
                session.id to status
            }
        }
    }

    fun getStatus(
        sessionId: UUID,
        userId: UUID
    ): AttendanceStatus? {
        if (!bySession.containsKey(sessionId)) throw BusinessException(AttendanceError.SESSION_NOT_FOUND)
        val sessionAttendances = bySession[sessionId] ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)
        if (!sessionAttendances.containsKey(userId)) throw BusinessException(AttendanceError.USER_NOT_FOUND)
        return sessionAttendances[userId]
    }

    fun userAttendanceStatistics(userId: UUID): UserAttendanceStatistics {
        val userAttendances = byUser[userId] ?: throw BusinessException(AttendanceError.USER_NOT_FOUND)
        return UserAttendanceStatistics.from(userAttendances.map { it.value }, latePassByUserId[userId]?.size ?: 0)
    }

    fun sessionAttendanceStatistics(sessionId: UUID): SessionAttendanceStatistics {
        val sessionAttendances = bySession[sessionId] ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)
        return SessionAttendanceStatistics.from(sessionAttendances.map { it.value })
    }
}
