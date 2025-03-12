package co.yappuworld.schedule.infrastructure.repository

import co.yappuworld.schedule.domain.ScheduleEntity
import co.yappuworld.schedule.domain.SessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface ScheduleJpaRepository : JpaRepository<ScheduleEntity, UUID> {

    fun findSessionEntitiesByGeneration(generation: Int): List<SessionEntity>

    /**
     * @param from Inclusive
     * @Param to Inclusive
     */
    fun findScheduleEntitiesByDateIsBetween(
        from: LocalDate,
        to: LocalDate
    ): List<ScheduleEntity>
}
