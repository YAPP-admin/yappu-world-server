package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.AttendanceRequest
import co.yappuworld.schedule.client.dto.response.AttendancesHistoryResponse
import co.yappuworld.schedule.client.dto.response.AttendancesHistoryResponseV2
import co.yappuworld.schedule.domain.AttendanceBook
import co.yappuworld.schedule.domain.UserAttendanceStatistics
import co.yappuworld.schedule.domain.vo.AttendanceError
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
    private val sessionFindService: SessionFindService,
    private val generationFindService: GenerationFindService,
    private val userFindService: UserFindService,
    private val latePassFindService: LatePassFindService,
    private val configFindService: ConfigFindService
) {

    @Transactional
    fun checkIn(
        request: AttendanceRequest,
        userId: UUID,
        now: LocalDateTime
    ) {
        val sessionAttendance = sessionFindService
            .findSessionAttendance(userId, request.sessionId)
            ?.also { it.validateCheckInAvailability(now) }
            ?: throw BusinessException(AttendanceError.NOT_INVITED)

        checkAttendanceCode(request.attendanceCode)
        sessionAttendance.checkIn(now)
    }

    @Transactional(readOnly = true)
    fun getAttendanceStatistics(
        userId: UUID,
        now: LocalDateTime
    ): UserAttendanceStatistics {
        val activeGeneration = generationFindService.findActiveGeneration()
        val attendee = userFindService.findSessionAttendee(userId, activeGeneration)
        val activeGenerationSessions = sessionFindService.findSessionsInGeneration(activeGeneration)
        val attendances = attendanceFindService.findAttendancesBySchedules(
            userId = userId,
            scheduleIds = activeGenerationSessions.map { it.id }
        )
        val latePassCount = latePassFindService.countLatePasses(activeGeneration, userId)

        val attendanceBook = AttendanceBook(
            generation = activeGeneration,
            attendees = listOf(attendee),
            sessions = activeGenerationSessions,
            attendances = attendances,
            latePassCountByUserId = mapOf(userId to latePassCount),
            now = now
        )

        return attendanceBook.getUserAttendanceStatistics(userId)
    }

    @Transactional(readOnly = true)
    fun getAttendancesHistory(
        userId: UUID,
        now: LocalDateTime
    ): AttendancesHistoryResponse {
        val activeGeneration = generationFindService.findActiveGeneration()
        val sessionsWithAttendance = sessionFindService.findAttendancesHistories(
            generation = activeGeneration,
            userId = userId,
            now = now
        )

        return AttendancesHistoryResponse.of(sessionsWithAttendance)
    }

    @Transactional(readOnly = true)
    fun getAttendancesHistoryV2(
        userId: UUID,
        now: LocalDateTime
    ): AttendancesHistoryResponseV2 {
        val activeGeneration = generationFindService.findActiveGeneration()
        userFindService.findSessionAttendee(userId, activeGeneration)
        val sessionsWithAttendance = sessionFindService.findSessionsWithAttendanceStatus(
            generation = activeGeneration,
            userId = userId,
            now = now
        )

        return AttendancesHistoryResponseV2.of(sessionsWithAttendance, now)
    }

    private fun checkAttendanceCode(attendanceCode: String) {
        val value = configFindService.findAttendanceCodeValue()

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
