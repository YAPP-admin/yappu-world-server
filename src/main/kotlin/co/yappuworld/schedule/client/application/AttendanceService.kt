package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.AttendanceRequest
import co.yappuworld.schedule.client.dto.response.AttendanceStatisticsResponse
import co.yappuworld.schedule.client.dto.response.AttendancesHistoryResponse
import co.yappuworld.schedule.domain.AttendanceError
import co.yappuworld.schedule.domain.SessionAttendance
import co.yappuworld.schedule.infrastructure.AttendanceCommandService
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.LatePassFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.user.infrastructure.UserFindService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Service
class AttendanceService(
    private val attendanceFindService: AttendanceFindService,
    private val attendanceCommandService: AttendanceCommandService,
    private val configFindService: ConfigFindService,
    private val sessionFindService: SessionFindService,
    private val generationFindService: GenerationFindService,
    private val userFindService: UserFindService,
    private val latePassFindService: LatePassFindService
) {

    @Transactional
    fun checkIn(
        request: AttendanceRequest,
        userId: UUID,
        now: LocalDateTime
    ) {
        val sessionAttendance = getSessionAttendance(userId, request.sessionId)
            .also { it.validateCheckInAvailability(now) }

        checkAttendanceCode(request.attendanceCode)
        sessionAttendance.checkIn(now)

        attendanceCommandService.save(sessionAttendance)
    }

    @Transactional(readOnly = true)
    fun getAttendanceStatistics(
        userId: UUID,
        now: LocalDateTime
    ): AttendanceStatisticsResponse {
        val activeGeneration = generationFindService.findActiveGeneration()
        val thisGenerationSessions = sessionFindService.findSessionsInGeneration(activeGeneration)
        val attendances = attendanceFindService.findAttendancesBySchedules(
            userId = userId,
            scheduleIds = thisGenerationSessions.map { it.id }
        )
        val latePassCount = latePassFindService.countLatePasses(activeGeneration, userId)

        return AttendanceStatisticsResponse.of(thisGenerationSessions, attendances, now, latePassCount)
    }

    @Transactional(readOnly = true)
    fun getAttendancesHistory(
        userId: UUID,
        now: LocalDateTime
    ): AttendancesHistoryResponse {
        val activeGeneration = generationFindService.findActiveGeneration()
        val sessionsWithAttendance = sessionFindService.findAttendancesHistory(
            generation = activeGeneration,
            userId = userId,
            now = now
        )

        return AttendancesHistoryResponse.of(sessionsWithAttendance)
    }

    private fun getSessionAttendance(
        userId: UUID,
        sessionId: UUID
    ): SessionAttendance {
        val generation = generationFindService.findActiveGeneration()
        val user = userFindService.findUserWithActivityUnitOfGeneration(userId, generation)
        val session = sessionFindService.findSession(sessionId)
        val attendance = attendanceFindService.findSessionAttendance(userId, sessionId)

        return SessionAttendance(user = user, session = session, attendance = attendance)
    }

    private fun checkAttendanceCode(attendanceCode: String) {
        val value = configFindService.findAttendanceCode()

        if (value == null) {
            logger.error { "출석 코드가 등록되지 않았습니다." }
            throw BusinessException(AttendanceError.UNREGISTERED_ATTENDANCE_CODE)
        }

        if (attendanceCode != value) {
            logger.warn { "출석 코드가 일치하지 않습니다." }
            throw BusinessException(AttendanceError.ATTENDANCE_CODE_NOT_MATCH)
        }
    }
}
