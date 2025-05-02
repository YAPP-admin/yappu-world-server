package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponse
import co.yappuworld.schedule.client.dto.response.SchedulePageResponse
import co.yappuworld.schedule.client.dto.response.UpcomingSessionAttendanceResponse
import co.yappuworld.schedule.domain.ScheduleError
import co.yappuworld.schedule.domain.SessionAttendance
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
    private val generationFindService: GenerationFindService
) {

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
    fun getUpcomingSessionAttendance(
        userId: UUID,
        now: LocalDateTime
    ): UpcomingSessionAttendanceResponse {
        val activeGeneration = generationFindService.findActiveGenerationOrNull()
            ?: throw BusinessException(ScheduleError.NO_SESSION_WITHOUT_ACTIVE_GENERATION)
        val user = userFindService.findUserWithActivityUnitOfGeneration(userId, activeGeneration)
        val session = sessionFindService.findUpcomingSession(activeGeneration, now)
        val attendanceOrNull = attendanceFindService.findSessionAttendance(userId, session.id)

        return UpcomingSessionAttendanceResponse.of(
            sessionAttendance = SessionAttendance(
                user = user,
                session = session,
                attendance = attendanceOrNull
            ),
            now = now
        )
    }
}
