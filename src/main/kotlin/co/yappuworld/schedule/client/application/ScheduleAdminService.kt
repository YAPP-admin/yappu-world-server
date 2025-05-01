package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionDeleteRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionPageRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminSessionDetailResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionOverviewResponse
import co.yappuworld.schedule.domain.ScheduleError
import co.yappuworld.schedule.domain.entity.SessionEntity
import co.yappuworld.schedule.infrastructure.ScheduleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ScheduleAdminService(
    private val scheduleRepository: ScheduleRepository
) {

    @Transactional
    fun createSchedule(request: AdminSessionCreateRequest): UUID {
        val schedule = scheduleRepository.save(request.toDomain())
        return schedule.id
    }

    @Transactional(readOnly = true)
    fun getSessions(request: AdminSessionPageRequest): OffsetPageResponse<AdminSessionOverviewResponse> =
        when (request.generation != null) {
            true -> scheduleRepository.findAllByGeneration(request.toPageRequest(), request.generation)
            false -> scheduleRepository.findAll(request.toPageRequest())
        }.let {
            OffsetPageResponse(
                data = it.content.map { session -> AdminSessionOverviewResponse(session as SessionEntity) },
                totalCount = it.totalElements,
                totalPages = it.totalPages,
                page = request.page,
                size = request.size
            )
        }

    @Transactional(readOnly = true)
    fun getSession(id: UUID): AdminSessionDetailResponse =
        scheduleRepository
            .findByIdOrNull(id)
            ?.let { AdminSessionDetailResponse(it as SessionEntity) }
            ?: throw BusinessException(ScheduleError.NOT_FOUND_SESSION)

    /**
     * 어드민에서 요청하는 세션 삭제라, hard delete 구현
     */
    @Transactional
    fun deleteSession(request: AdminSessionDeleteRequest) {
        val sessions = scheduleRepository.findAllByIdIn(request.ids)

        if (sessions.size != request.ids.size) {
            throw BusinessException(ScheduleError.CONTAIN_IMPROPER_ID_FOR_DELETE_SESSION)
        }

        scheduleRepository.deleteAll(sessions)
    }

    @Transactional
    fun updateSession(request: AdminSessionUpdateRequest) {
        val schedule = scheduleRepository.findByIdOrNull(request.id)
            ?: throw BusinessException(ScheduleError.UPDATE_FAIL_NOT_SESSION_TYPE)

        (schedule as SessionEntity).apply { request.applyTo(this) }
    }
}
