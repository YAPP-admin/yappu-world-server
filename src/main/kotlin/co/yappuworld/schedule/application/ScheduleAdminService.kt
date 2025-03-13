package co.yappuworld.schedule.application

import co.yappuworld.schedule.application.dto.request.AdminSessionPageAppRequestDto
import co.yappuworld.schedule.application.dto.request.ScheduleCreateAppRequestDto
import co.yappuworld.schedule.application.dto.response.AdminSessionPageAppResponseDto
import co.yappuworld.schedule.infrastructure.repository.ScheduleJpaRepository
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
}
