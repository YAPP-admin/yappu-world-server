package co.yappuworld.schedule.client.application

import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.AdminAttendanceCodeUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminAttendanceCodeResponse
import co.yappuworld.schedule.client.dto.response.AdminAttendancesResponse
import co.yappuworld.schedule.domain.AttendanceBook
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.LatePassCommandService
import co.yappuworld.schedule.infrastructure.LatePassFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.schedule.infrastructure.entity.LatePassEntity
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminAttendanceService(
    private val attendanceFindService: AttendanceFindService,
    private val userFindService: UserFindService,
    private val sessionFindService: SessionFindService,
    private val generationFindService: GenerationFindService,
    private val latePassFindService: LatePassFindService,
    private val latePassCommandService: LatePassCommandService,
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
        updateAttendanceStatus(request)
        updateLatePasses(request)
    }

    @Transactional
    fun updateSessionAttendances(request: AdminSessionAttendanceUpdateRequest) {
        attendanceFindService
            .findAttendances(request.sessionId)
            .onEach { it.updateStatus(request.attendanceStatus) }
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

    private fun updateAttendanceStatus(request: AdminAttendanceUpdateRequest) {
        val attendanceBySessionAndUserId = attendanceFindService
            .findAttendances(request.getSessionAndUserIdPairs())
            .associateBy { it.session.id to it.userId }

        request.attendances.forEach { request ->
            attendanceBySessionAndUserId[request.sessionId to request.userId]
                ?.apply { updateStatus(request.attendanceStatus) }
        }
    }

    private fun updateLatePasses(request: AdminAttendanceUpdateRequest) {
        val existingLatePasses = latePassFindService
            .findLatePasses(request.generation, request.latePasses.map { it.userId })
            .associateBy { it.userId }

        val latePasses = request.latePasses.map { updateRequest ->
            val latePass = existingLatePasses[updateRequest.userId]
                ?: LatePassEntity(userId = updateRequest.userId, generation = request.generation)

            latePass.apply { updateCount(updateRequest.latePassCount) }
        }

        latePassCommandService.saveAll(latePasses)
    }
}
