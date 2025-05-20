package co.yappuworld.schedule.client.application

import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.AdminAttendanceCodeUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminAttendanceCodeResponse
import co.yappuworld.schedule.client.dto.response.AdminAttendancesResponse
import co.yappuworld.schedule.domain.AttendanceBook
import co.yappuworld.schedule.infrastructure.AttendanceCommandService
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.LatePassFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.schedule.infrastructure.SessionParticipantCommandService
import co.yappuworld.schedule.infrastructure.SessionParticipantFindService
import co.yappuworld.schedule.infrastructure.jpa.AttendanceEntity
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminAttendanceService(
    private val attendanceFindService: AttendanceFindService,
    private val attendanceCommandService: AttendanceCommandService,
    private val sessionParticipantFindService: SessionParticipantFindService,
    private val sessionParticipantCommandService: SessionParticipantCommandService,
    private val userFindService: UserFindService,
    private val sessionFindService: SessionFindService,
    private val generationFindService: GenerationFindService,
    private val latePassFindService: LatePassFindService,
    private val configFindService: ConfigFindService
) {

    @Transactional(readOnly = true)
    fun findAttendances(now: LocalDateTime): AdminAttendancesResponse {
        val activeGeneration = generationFindService.findActiveGeneration()
        return AdminAttendancesResponse.from(
            AttendanceBook(
                generation = activeGeneration,
                attendees = userFindService.findSessionAttendeesOfGeneration(activeGeneration),
                sessions = sessionFindService.findSessionsInGeneration(activeGeneration),
                attendances = attendanceFindService.findAttendancesOfGeneration(activeGeneration),
                latePasses = latePassFindService.findLatePasses(activeGeneration),
                now = now
            )
        )
    }

    @Transactional
    fun updateAttendance(request: AdminAttendanceUpdateRequest) {
        val attendanceBySessionAndUserId = attendanceFindService
            .findAttendances(request.getSessionAndUserIdPairs())
            .associateBy { it.scheduleId to it.userId }

        val newAttendances = mutableListOf<AttendanceEntity>()

        request.targets.forEach { target ->
            attendanceBySessionAndUserId[target.sessionId to target.userId]
                ?.apply { updateStatus(target.attendanceStatus) }
                ?: newAttendances.add(
                    AttendanceEntity(
                        status = target.attendanceStatus,
                        userId = target.userId,
                        scheduleId = target.sessionId
                    )
                )
        }

        if (newAttendances.isNotEmpty()) attendanceCommandService.saveAll(newAttendances)
    }

    @Transactional
    fun updateSessionAttendances(request: AdminSessionAttendanceUpdateRequest) {
        sessionParticipantFindService
            .findSessionParticipantEntieis(request.sessionId)
            .onEach { participant -> participant.adminUpdate(request.attendanceStatus) }
    }

    @Transactional(readOnly = true)
    fun getAttendanceCode(): AdminAttendanceCodeResponse =
        AdminAttendanceCodeResponse(
            configFindService.findAttendanceCodeValue()
        )

    @Transactional
    fun updateAttendanceCode(request: AdminAttendanceCodeUpdateRequest) {
        configFindService
            .findAttendanceCode()
            .update(request.getPaddedCode())
    }

    @Transactional
    fun deleteAttendanceCode() {
        configFindService.findAttendanceCode().reset()
    }
}
