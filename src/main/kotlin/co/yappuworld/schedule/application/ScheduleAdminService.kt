package co.yappuworld.schedule.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.application.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.application.dto.request.AdminSessionPageAppRequestDto
import co.yappuworld.schedule.application.dto.request.AdminSessionUpdateAppRequestDto
import co.yappuworld.schedule.application.dto.response.AdminSessionDetailsAppResponseDto
import co.yappuworld.schedule.application.dto.response.AdminSessionPageAppResponseDto
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
        val domain = request.toDomain()
        val schedule = scheduleJpaRepository.save(domain)
        return schedule.id
    }

    @Transactional(readOnly = true)
    fun getSessions(request: AdminSessionPageAppRequestDto): AdminSessionPageAppResponseDto =
        when (request.generation != null) {
            true -> scheduleJpaRepository.findAllByGeneration(request.toPageRequest(), request.generation)
            false -> scheduleJpaRepository.findAll(request.toPageRequest())
        }.let { AdminSessionPageAppResponseDto(it) }

    @Transactional(readOnly = true)
    fun getSession(id: UUID): AdminSessionDetailsAppResponseDto =
        scheduleJpaRepository
            .findByIdOrNull(id)
            ?.let { AdminSessionDetailsAppResponseDto(it as SessionEntity) }
            ?: throw BusinessException(ScheduleError.NOT_FOUND_SESSION)

    /**
     * 어드민에서 요청하는 세션 삭제라, hard delete 구현
     */
    @Transactional
    fun deleteSession(id: UUID) {
        scheduleJpaRepository.deleteById(id)
    }

    @Transactional
    fun updateSession(request: AdminSessionUpdateAppRequestDto) {
        val schedule = scheduleJpaRepository.findByIdOrNull(request.id)
            ?: throw BusinessException(ScheduleError.UPDATE_FAIL_NOT_SESSION_TYPE)

        (schedule as SessionEntity).apply { request.applyTo(this) }
    }
}
