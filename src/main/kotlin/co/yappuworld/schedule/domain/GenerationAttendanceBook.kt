package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ABSENT
import co.yappuworld.schedule.domain.vo.AttendanceStatus.PENDING
import co.yappuworld.schedule.infrastructure.jpa.LatePassEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.user.domain.model.UserActivityUnit
import java.time.LocalDateTime
import java.util.UUID

class GenerationAttendanceBook(
    val generation: Int,
    val sessions: List<SessionEntity>,
    val userActivityUnits: List<UserActivityUnit>,
    sessionParticipants: List<SessionParticipant>,
    latePasses: List<LatePassEntity>,
    private val now: LocalDateTime
) {

    private val sessionById: Map<UUID, SessionEntity> = sessions.associateBy { it.id }

    // sessionId to userId to AttendanceStatus
    private val bySession: Map<UUID, Map<UUID, SessionParticipant?>>

    // userId to sessionId to AttendanceStatus
    private val byUser: Map<UUID, Map<UUID, SessionParticipant?>>

    private val latePassGroupedByUserId: Map<UUID, List<LatePassEntity>> = latePasses.groupBy { it.userId }

    init {
        require(sessions.all { it.generation == generation })
        require(userActivityUnits.all { it.generation == generation })

        // (userId to sessionId) to SessionParticipant
        val sessionParticipantMatrix = sessionParticipants.associateBy { (it.userId to it.sessionId) }

        this.bySession = sessions.associate { session ->
            session.id to userActivityUnits.associate { userActivityUnit ->
                userActivityUnit.userId to sessionParticipantMatrix[userActivityUnit.userId to session.id]
            }
        }

        this.byUser = userActivityUnits.associate { userActivityUnit ->
            userActivityUnit.userId to sessions.associate { session ->
                session.id to sessionParticipantMatrix[userActivityUnit.userId to session.id]
            }
        }
    }

    fun getSessionStatusesByUserId(sessionId: UUID): Map<UUID, AttendanceStatus?> {
        val sessionParticipantsInSession = bySession[sessionId]
            ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)
        val session = sessionById[sessionId]
            ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)

        return sessionParticipantsInSession.mapValues { (_, sessionParticipant) ->
            when {
                sessionParticipant?.attendanceStatus == PENDING && session.isFinished(now) -> ABSENT
                else -> sessionParticipant?.attendanceStatus
            }
        }
    }

    fun getStatus(
        sessionId: UUID,
        userId: UUID
    ): AttendanceStatus? {
        val sessionParticipants = bySession[sessionId]
            ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)
        val sessionParticipant = sessionParticipants[userId] ?: return null

        if (sessionParticipant.isPending() && sessionById[sessionParticipant.sessionId]?.isFinished(now) == true) {
            return ABSENT
        }

        return sessionParticipant.attendanceStatus
    }

    fun getUserAttendanceStatistics(userId: UUID): UserAttendanceStatistics {
        val attendanceStatuses = byUser[userId] ?: throw BusinessException(AttendanceError.USER_NOT_FOUND)
        val sessionParticipants = attendanceStatuses.values.filterNotNull()
        return UserAttendanceStatistics.from(
            sessions = sessions,
            sessionParticipants = sessionParticipants,
            latePassCount = latePassGroupedByUserId[userId]?.count() ?: 0,
            now = now
        )
    }

    fun getSessionAttendanceStatistics(sessionId: UUID): SessionAttendanceStatistics {
        val sessionParticipants = bySession[sessionId]?.values?.filterNotNull()
            ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)

        return SessionAttendanceStatistics.from(
            session = sessionById[sessionId] ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND),
            sessionParticipants = sessionParticipants,
            now = now
        )
    }
}
