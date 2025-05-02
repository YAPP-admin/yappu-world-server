package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.entity.ScheduleEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ScheduleCommandService(
    private val scheduleRepository: ScheduleRepository
) {

    fun save(schedule: ScheduleEntity) {
        scheduleRepository.save(schedule)
    }

    fun deleteAll(schedules: List<ScheduleEntity>) {
        scheduleRepository.deleteAll(schedules)
    }
}
