package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionDeleteRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionPageRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminSessionDetailResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionOverviewResponse
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.infrastructure.ScheduleCommandService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.schedule.infrastructure.SessionParticipantCommandService
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.user.infrastructure.ActivityUnitFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminScheduleService(
    private val sessionFindService: SessionFindService,
    private val scheduleCommandService: ScheduleCommandService,
    private val sessionParticipantCommandService: SessionParticipantCommandService,
    private val activityUnitFindService: ActivityUnitFindService
) {

    @Transactional
    fun createSession(request: AdminSessionCreateRequest): UUID {
        val session = request.toDomain()
        scheduleCommandService.save(session)
        inviteParticipants(session, request)

        return session.id
    }

    @Transactional(readOnly = true)
    fun getSessions(request: AdminSessionPageRequest): OffsetPageResponse<AdminSessionOverviewResponse> =
        when (request.generation != null) {
            true -> sessionFindService.findSessionsInGeneration(request.toPageRequest(), request.generation)
            false -> sessionFindService.findSessions(request.toPageRequest())
        }.let { OffsetPageResponse.from(it) { session -> AdminSessionOverviewResponse(session) } }

    @Transactional(readOnly = true)
    fun getSession(id: UUID): AdminSessionDetailResponse =
        AdminSessionDetailResponse(sessionFindService.findSession(id))

    /**
     * 어드민에서 요청하는 세션 삭제라, hard delete 구현
     */
    @Transactional
    fun deleteSession(request: AdminSessionDeleteRequest) {
        val sessions = sessionFindService.findSessions(request.ids)

        if (sessions.size != request.ids.size) {
            throw BusinessException(ScheduleError.CONTAIN_IMPROPER_ID_FOR_DELETE_SESSION)
        }

        scheduleCommandService.deleteAll(sessions)
    }

    @Transactional
    fun updateSession(request: AdminSessionUpdateRequest) {
        sessionFindService
            .findSession(request.id)
            .apply { request.applyTo(this) }
    }

    private fun inviteParticipants(
        session: SessionEntity,
        request: AdminSessionCreateRequest
    ) {
        val participants = when (request.userIds.isNullOrEmpty()) {
            true -> activityUnitFindService.findParticipants(request.generation)
            false -> activityUnitFindService.findParticipants(request.generation, request.userIds)
        }

        sessionParticipantCommandService.saveAll(session, participants)
    }
}
