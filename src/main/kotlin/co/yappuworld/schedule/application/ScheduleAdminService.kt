package co.yappuworld.schedule.application

import co.yappuworld.schedule.application.dto.request.ScheduleCreateAppRequestDto
import co.yappuworld.schedule.infrastructure.repository.ScheduleJdbcRepository
import co.yappuworld.schedule.infrastructure.repository.ScheduleJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ScheduleAdminService(
    private val scheduleJpaRepository: ScheduleJpaRepository,
    private val scheduleJdbcRepository: ScheduleJdbcRepository
) {

    @Transactional
    fun createSchedule(request: ScheduleCreateAppRequestDto): UUID {
        val schedule = scheduleJpaRepository.save(request.toDomain())
        return schedule.id
    }
}
