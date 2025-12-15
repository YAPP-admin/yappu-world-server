package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.entity.ScheduleEntity
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
    ): List<ScheduleEntity> =
        scheduleRepository
            .findAll {
                select(entity(ScheduleEntity::class))
                    .from(entity(ScheduleEntity::class))
                    .where(
                        and(
                            path(ScheduleEntity::date).greaterThanOrEqualTo(from),
                            path(ScheduleEntity::date).lessThanOrEqualTo(toInclusive)
                        )
                    )
            }.filterNotNull()
}
