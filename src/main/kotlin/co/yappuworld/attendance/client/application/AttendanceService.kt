package co.yappuworld.attendance.client.application

import co.yappuworld.attendance.client.dto.request.AttendanceRequest
import co.yappuworld.attendance.client.dto.response.AttendanceStatisticsResponse
import co.yappuworld.attendance.domain.Attendance
import co.yappuworld.attendance.domain.AttendanceError
import co.yappuworld.attendance.infrastructure.AttendanceCommandService
import co.yappuworld.attendance.infrastructure.AttendanceFindService
import co.yappuworld.attendance.infrastructure.LatePassFindService
import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.user.domain.vo.UserRole
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
        validateCheckIn(request, userId)

        sessionFindService.findSession(request.sessionId)?.let {
            attendanceCommandService.save(
                Attendance.checkInSession(now = now, userId = userId, session = it as SessionEntity)
            )
        }
    }

    @Transactional(readOnly = true)
    fun getAttendanceStatistics(
        userId: UUID,
        now: LocalDateTime
    ): AttendanceStatisticsResponse {
        val activeGeneration = generationFindService.findActiveGeneration()
            ?: throw BusinessException(AttendanceError.NO_ACTIVE_GENERATION)
        val thisGenerationSessions = sessionFindService.findCurrentGenerationSessions(activeGeneration)
        val attendances = attendanceFindService.findAttendancesBySchedules(
            userId = userId,
            scheduleIds = thisGenerationSessions.map { it.id }
        )
        val latePassCount = latePassFindService.countLatePasses(activeGeneration, userId)

        return AttendanceStatisticsResponse.of(thisGenerationSessions, attendances, now, latePassCount)
    }

    private fun validateCheckIn(
        request: AttendanceRequest,
        userId: UUID
    ) {
        val generation = generationFindService.findActiveGeneration()
            ?: throw BusinessException(AttendanceError.NO_ACTIVE_GENERATION)

        validateAttendance(request, userId)
        validateSession(request.sessionId, generation)
        validateAttendee(userId, generation)
    }

    private fun validateAttendance(
        request: AttendanceRequest,
        userId: UUID
    ) {
        if (attendanceFindService.hasAlreadyCheckedIn(userId, request.sessionId)) {
            logger.warn { "이미 출석을 완료한 유저($userId)입니다." }
            throw BusinessException(AttendanceError.ALREADY_CHECKED_IN)
        }

        val attendanceCode = configFindService.findConfig("attendanceCode")?.value
        if (attendanceCode == null) {
            logger.error { "출석 코드가 저장되어 있지 않습니다." }
            throw BusinessException(AttendanceError.ATTENDANCE_CODE_NOT_FOUND)
        }

        if (request.attendanceCode != attendanceCode) {
            logger.warn { "출석 코드가 일치하지 않습니다." }
            throw BusinessException(AttendanceError.ATTENDANCE_CODE_NOT_MATCH)
        }
    }

    private fun validateSession(
        sessionId: UUID,
        generation: Int
    ) {
        val session = sessionFindService.findSession(sessionId)
            ?: throw BusinessException(AttendanceError.SESSION_NOT_FOUND)

        if (session !is SessionEntity) {
            logger.warn { "해당 일정($sessionId)는 세션 타입이 아닙니다." }
            throw BusinessException(AttendanceError.CHECK_IN_ONLY_FOR_SESSION)
        }

        if (session.generation != generation) {
            logger.warn {
                """
                세션의 기수와 활성화된 기수가 일치하지 않습니다.
                세션 기수: ${session.generation}
                활성화 되어 있는 기수: $generation
                """.trimIndent()
            }
            throw BusinessException(AttendanceError.GENERATION_NOT_MATCH)
        }
    }

    private fun validateAttendee(
        userId: UUID,
        generation: Int
    ) {
        val user = userFindService.findUserWithLastActivityUnit(userId)

        if (user.role != UserRole.ACTIVE) {
            logger.warn { "유저($userId)는 활동 멤버가 아니라서 출석이 불가합니다." }
            throw BusinessException(AttendanceError.USER_NOT_ACTIVATE)
        }

        if (user.generation != generation) {
            logger.warn {
                """
                유저의 가장 최근 활동 기수와 활성화된 기수가 일치하지 않습니다.
                유저 기수: ${user.generation}
                활성화 되어 있는 기수: $generation
                """.trimIndent()
            }
            throw BusinessException(AttendanceError.GENERATION_NOT_MATCH)
        }
    }
}
