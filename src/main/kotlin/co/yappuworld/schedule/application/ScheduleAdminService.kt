package co.yappuworld.schedule.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.application.dto.request.AdminSessionPageAppRequestDto
import co.yappuworld.schedule.application.dto.request.ScheduleCreateAppRequestDto
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
    fun createSchedule(request: ScheduleCreateAppRequestDto): UUID {
        val schedule = scheduleJpaRepository.save(request.toDomain())
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
}
