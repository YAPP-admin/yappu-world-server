package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionPageRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminSessionDetailResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionOverviewResponse
import co.yappuworld.schedule.domain.ScheduleError
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.infrastructure.repository.ScheduleJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ScheduleAdminService(
    private val scheduleJpaRepository: ScheduleJpaRepository
) {

    @Transactional
    fun createSchedule(request: AdminSessionCreateRequest): UUID {
        val schedule = scheduleJpaRepository.save(request.toDomain())
        return schedule.id
    }

    @Transactional(readOnly = true)
    fun getSessions(request: AdminSessionPageRequest): OffsetPageResponse<AdminSessionOverviewResponse> =
        when (request.generation != null) {
            true -> scheduleJpaRepository.findAllByGeneration(request.toPageRequest(), request.generation)
            false -> scheduleJpaRepository.findAll(request.toPageRequest())
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
        scheduleJpaRepository
            .findByIdOrNull(id)
            ?.let { AdminSessionDetailResponse(it as SessionEntity) }
            ?: throw BusinessException(ScheduleError.NOT_FOUND_SESSION)

    /**
     * 어드민에서 요청하는 세션 삭제라, hard delete 구현
     */
    @Transactional
    fun deleteSession(id: UUID) {
        scheduleJpaRepository.deleteById(id)
    }

    @Transactional
    fun updateSession(request: AdminSessionUpdateRequest) {
        val schedule = scheduleJpaRepository.findByIdOrNull(request.id)
            ?: throw BusinessException(ScheduleError.UPDATE_FAIL_NOT_SESSION_TYPE)

        (schedule as SessionEntity).apply { request.applyTo(this) }
    }
}
