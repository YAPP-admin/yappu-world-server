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
import co.yappuworld.schedule.infrastructure.SessionParticipantFindService
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
    private val activityUnitFindService: ActivityUnitFindService,
    private val sessionParticipantFindService: SessionParticipantFindService
) {

    @Transactional
    fun createSession(request: AdminSessionCreateRequest): UUID {
        val session = request.toDomain()
        scheduleCommandService.save(session)
        inviteParticipants(session, request.participantIds)

        return session.id
    }

    @Transactional(readOnly = true)
    fun getSessions(request: AdminSessionPageRequest): OffsetPageResponse<AdminSessionOverviewResponse> =
        when (request.generation != null) {
            true -> sessionFindService.findSessionsInGeneration(request.toPageRequest(), request.generation)
            false -> sessionFindService.findSessions(request.toPageRequest())
        }.let { OffsetPageResponse.from(it) { session -> AdminSessionOverviewResponse(session) } }

    @Transactional(readOnly = true)
    fun getSession(id: UUID): AdminSessionDetailResponse {
        val session = sessionFindService.findSession(id)
        val participants = sessionParticipantFindService.findSessionParticipants(session.id)
        return AdminSessionDetailResponse.from(session, participants)
    }

    /**
     * 어드민에서 요청하는 세션 삭제라, hard delete 구현
     * 참가자들도 모두 삭제
     */
    @Transactional
    fun deleteSession(request: AdminSessionDeleteRequest) {
        val sessions = sessionFindService.findSessions(request.ids)

        if (sessions.size != request.ids.size) {
            throw BusinessException(ScheduleError.CONTAIN_IMPROPER_ID_FOR_DELETE_SESSION)
        }

        sessionParticipantCommandService.deleteAllSessionParticipants(sessions)
        scheduleCommandService.deleteAll(sessions)
    }

    @Transactional
    fun updateSession(request: AdminSessionUpdateRequest) {
        val session = sessionFindService.findSession(request.id)

        session.apply { request.applyTo(this) }
        updateParticipants(session, request.participantIds)
    }

    private fun inviteParticipants(
        session: SessionEntity,
        userIds: List<UUID>?
    ) {
        val participants = when {
            // TODO: 현재 프론트에서 해당 값을 파라미터로 보내고 있지 않으므로, 파라미터가 추가되고 나면 해당 로직은 삭제
            userIds == null -> activityUnitFindService.findGenerationMembers(session.generation)
            userIds.isEmpty() -> return
            else -> activityUnitFindService.findGenerationMembers(session.generation, userIds)
        }

        sessionParticipantCommandService.saveAll(session, participants)
    }

    private fun updateParticipants(
        session: SessionEntity,
        updateUserIds: List<UUID>?
    ) {
        /**
         * TODO: 프론트 개발 완료시 삭제
         * 생성 시점에 참여자들을 기수 멤버 전원으로 등록해놓았으므로, 별도 조치 X
         */
        if (updateUserIds == null) {
            return
        }

        if (updateUserIds.isEmpty()) {
            sessionParticipantCommandService.deleteAllSessionParticipants(session)
            return
        }

        val existParticipants = sessionParticipantFindService.findSessionParticipantEntieis(session.id)
        val existParticipantIds = existParticipants.map { it.id }.toSet()

        val updateParticipantIds = updateUserIds.toSet()
        val newParticipantIds = updateParticipantIds - existParticipantIds
        activityUnitFindService
            .findGenerationMembers(session.generation, newParticipantIds)
            .also { sessionParticipantCommandService.saveAll(session, it) }

        existParticipants
            .filter { it.id !in updateParticipantIds }
            .also { sessionParticipantCommandService.deleteAll(it) }
    }
}
