package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponse
import co.yappuworld.schedule.client.dto.response.SchedulePageResponse
import co.yappuworld.schedule.client.dto.response.UpcomingSessionAttendanceResponse
import co.yappuworld.schedule.domain.ScheduleError
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.ScheduleRepository
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
class ScheduleService(
    private val scheduleRepository: ScheduleRepository,
    private val userFindService: UserFindService,
    private val sessionFindService: SessionFindService,
    private val attendanceFindService: AttendanceFindService,
    private val generationFindService: GenerationFindService
) {

    @Transactional(readOnly = true)
    fun getCurrentGenerationSessions(
        userId: UUID,
        now: LocalDate
    ): ActiveGenerationSessionsResponse {
        val currentGeneration = generationFindService.findActiveGeneration()
            ?: return ActiveGenerationSessionsResponse.from(emptyList(), now)
        val sessions = sessionFindService.findSessionsWithAttendanceStatus(currentGeneration, userId)
        return ActiveGenerationSessionsResponse.from(
            sessions = sessions,
            now = now
        )
    }

    fun getSchedules(
        request: SchedulePageRequest,
        now: LocalDateTime
    ): SchedulePageResponse {
        val schedules = scheduleRepository.findScheduleEntitiesByDateBetween(request.from, request.to)
        return SchedulePageResponse.from(schedules, request, now)
    }

    @Transactional(readOnly = true)
    fun getUpcomingSessionAttendance(
        userId: UUID,
        now: LocalDateTime
    ): UpcomingSessionAttendanceResponse {
        val user = userFindService.findUserWithLastActivityUnit(userId)
        val activeGeneration = generationFindService.findActiveGeneration()
            ?: throw BusinessException(ScheduleError.NO_UPCOMING_SESSION)
        val session = sessionFindService.findUpcomingSession(activeGeneration, now)
            ?: throw BusinessException(ScheduleError.NO_UPCOMING_SESSION)
        val attendanceOrNull = attendanceFindService.findSessionAttendance(userId, session.id)

        return UpcomingSessionAttendanceResponse.of(user, activeGeneration, session, attendanceOrNull, now)
    }
}
