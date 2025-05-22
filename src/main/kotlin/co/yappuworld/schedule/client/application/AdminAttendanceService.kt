package co.yappuworld.schedule.client.application

import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.AdminAttendanceCodeUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminAttendanceCodeResponse
import co.yappuworld.schedule.client.dto.response.AdminAttendancesResponse
import co.yappuworld.schedule.domain.GenerationAttendanceBook
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.LatePassFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.schedule.infrastructure.SessionParticipantFindService
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminAttendanceService(
    private val attendanceFindService: AttendanceFindService,
    private val sessionParticipantFindService: SessionParticipantFindService,
    private val userFindService: UserFindService,
    private val sessionFindService: SessionFindService,
    private val generationFindService: GenerationFindService,
    private val latePassFindService: LatePassFindService,
    private val configFindService: ConfigFindService
) {

    @Transactional(readOnly = true)
    fun findAttendances(now: LocalDateTime): AdminAttendancesResponse {
        val activeGeneration = generationFindService.findActiveGeneration()
        val sessions = sessionFindService.findSessionsInGeneration(activeGeneration)
        val userActivityUnits = userFindService.findAllUserActivityUnitOfGeneration(activeGeneration)
        val participants = sessionParticipantFindService.findSessionParticipantsInGeneration(activeGeneration)

        return AdminAttendancesResponse.from(
            GenerationAttendanceBook(
                generation = activeGeneration,
                sessions = sessions,
                userActivityUnits = userActivityUnits,
                sessionParticipants = participants,
                latePasses = latePassFindService.findLatePasses(activeGeneration),
                now = now
            )
        )
    }

    @Transactional
    fun updateAttendance(request: AdminAttendanceUpdateRequest) {
        val targetBySessionAndActivityUnitId = request.targets
            .groupBy { it.sessionId }
            .mapValues { (_, list) -> list.associateBy { it.userActivityUnitId } }

        sessionParticipantFindService
            .findSessionParticipantEntities(request.getSessionAndUserActivityUnitIdPairs())
            .onEach { participant ->
                targetBySessionAndActivityUnitId[participant.session.id]
                    ?.get(participant.activityUnit.id)
                    ?.let { request ->
                        participant.forceUpdateStatus(request.attendanceStatus)
                    }
            }
    }

    @Transactional
    fun updateSessionAttendances(request: AdminSessionAttendanceUpdateRequest) {
        sessionParticipantFindService
            .findSessionParticipantEntities(request.sessionId)
            .onEach { participant -> participant.forceUpdateStatus(request.attendanceStatus) }
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
