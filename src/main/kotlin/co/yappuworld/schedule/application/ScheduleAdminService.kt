package co.yappuworld.schedule.application

import co.yappuworld.schedule.application.dto.request.ScheduleCreateAppRequestDto
import co.yappuworld.schedule.infrastructure.ScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ScheduleAdminService(
    private val scheduleRepository: ScheduleRepository
) {

    @Transactional
    fun createSchedule(request: ScheduleCreateAppRequestDto): UUID {
        val schedule = scheduleRepository.save(request.toDomain())
        return schedule.id
    }
}
