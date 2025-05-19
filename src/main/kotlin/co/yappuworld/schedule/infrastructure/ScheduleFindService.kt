package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.jpa.ScheduleEntity
import co.yappuworld.schedule.infrastructure.jpa.ScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class ScheduleFindService(
    private val scheduleRepository: ScheduleRepository
) {

    fun findSchedulesBetween(
        from: LocalDate,
        toInclusive: LocalDate
    ): List<ScheduleEntity> = scheduleRepository.findScheduleEntitiesByDateBetween(from, toInclusive)
}
