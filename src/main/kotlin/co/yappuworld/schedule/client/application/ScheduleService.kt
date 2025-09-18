package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.post.infrastructure.PostFindService
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.client.dto.request.SessionParamRequest
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponse
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponseV2
import co.yappuworld.schedule.client.dto.response.SchedulePageResponse
import co.yappuworld.schedule.client.dto.response.SessionDetailsNoticeResponse
import co.yappuworld.schedule.client.dto.response.SessionDetailsResponse
import co.yappuworld.schedule.client.dto.response.SessionOverviewResponse
import co.yappuworld.schedule.client.dto.response.UpcomingSessionResponse
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.ScheduleFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ScheduleService(
    private val userFindService: UserFindService,
    private val scheduleFindService: ScheduleFindService,
    private val sessionFindService: SessionFindService,
    private val attendanceFindService: AttendanceFindService,
    private val generationFindService: GenerationFindService,
    private val postFindService: PostFindService
) {

    @Transactional(readOnly = true)
    fun getSchedules(
        request: SchedulePageRequest,
        userId: UUID,
        now: LocalDateTime
    ): SchedulePageResponse {
        val userWithActivityUnits = userFindService.findUserWithActivities(userId)
        val schedules = scheduleFindService.findSchedulesBetween(request.from, request.toInclusive)
        val attendances = attendanceFindService.findAttendancesBySchedules(
            userId,
            schedules
                .filterIsInstance<SessionEntity>()
                .filter { it.generation in userWithActivityUnits.activityGenerations }
                .map { it.id }
        )

        return SchedulePageResponse.from(userWithActivityUnits, schedules, attendances, request, now)
    }

    @Transactional(readOnly = true)
    fun getCurrentGenerationSessions(
        userId: UUID,
        now: LocalDateTime
    ): ActiveGenerationSessionsResponse {
        val currentGeneration = generationFindService.findActiveGenerationOrNull()
            ?: return ActiveGenerationSessionsResponse.from(emptyList(), now)
        val sessions = sessionFindService.findSessionsWithAttendanceStatus(currentGeneration, userId, now)
        return ActiveGenerationSessionsResponse.from(
            sessions = sessions,
            now = now
        )
    }

    @Transactional(readOnly = true)
    fun getActiveGenerationSessions(
        userId: UUID,
        now: LocalDateTime
    ): ActiveGenerationSessionsResponseV2 {
        val currentGeneration = generationFindService.findActiveGenerationOrNull()
            ?: return ActiveGenerationSessionsResponseV2.from(emptyList(), now)
        val sessions = sessionFindService.findSessionsWithAttendanceStatus(currentGeneration, userId, now)
        return ActiveGenerationSessionsResponseV2.from(
            sessions = sessions,
            now = now
        )
    }

    fun getSessions(
        userId: UUID,
        request: SessionParamRequest,
        now: LocalDateTime
    ): SessionOverviewResponse {
        val generation = request.generation
            ?: generationFindService.findActiveGenerationOrNull()

        if (generation == null && request.start == null && request.end == null) {
            throw BusinessException(ScheduleError.NO_SESSION_WITHOUT_ACTIVE_GENERATION)
        }

        val user = userFindService.findUserWithActivities(userId)
        val sessions = sessionFindService.findSessions(
            request.generation,
            request.getDateRange()
        )
        val attendances = attendanceFindService.findAttendancesBySchedules(userId, sessions.map { it.id })
        return SessionOverviewResponse.from(
            user = user,
            sessions = sessions,
            attendances = attendances,
            now = now
        )
    }

    @Transactional(readOnly = true)
    fun findUpcomingSessionWithAttendance(
        userId: UUID,
        now: LocalDateTime
    ): UpcomingSessionResponse {
        val sessionAttendance = sessionFindService.findUpcomingSessionAttendance(userId, now)
            ?: throw BusinessException(ScheduleError.NO_UPCOMING_SESSION)
        val notices = postFindService.findNoticesTargetingSession(sessionAttendance.session.id)

        return UpcomingSessionResponse.of(
            sessionAttendance = sessionAttendance,
            notices = notices,
            now = now
        )
    }

    @Transactional(readOnly = true)
    fun findSessionDetails(
        sessionId: UUID,
        now: LocalDateTime
    ): SessionDetailsResponse {
        val session = sessionFindService.findSession(sessionId)
        val notices = postFindService.findNoticesTargetingSession(sessionId)
        val writerIds = notices.map { it.writerId }.distinct()
        val writers = when {
            writerIds.isEmpty() -> emptyMap()
            else -> userFindService.findAllUserWithLastActivityUnit(writerIds).associateBy { it.userId }
        }

        val noticeResponses = notices.map { notice ->
            SessionDetailsNoticeResponse.from(notice)
        }

        return SessionDetailsResponse.of(
            session = session,
            notices = notices,
            writers = writers,
            now = now
        )
    }
}
