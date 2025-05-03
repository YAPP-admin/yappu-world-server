package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.entity.ScheduleEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface ScheduleRepository :
    JpaRepository<ScheduleEntity, UUID>,
    KotlinJdslJpqlExecutor {

    fun findAllByIdIn(ids: List<UUID>): List<SessionEntity>

    /**
     * @param from Inclusive
     * @param to Inclusive
     */
    fun findScheduleEntitiesByDateBetween(
        from: LocalDate,
        to: LocalDate
    ): List<ScheduleEntity>
}
