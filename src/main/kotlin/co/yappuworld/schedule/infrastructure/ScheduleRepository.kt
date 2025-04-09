package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.ScheduleEntity
import co.yappuworld.schedule.domain.SessionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface ScheduleRepository : JpaRepository<ScheduleEntity, UUID> {

    fun findAllByIdIn(ids: List<UUID>): List<SessionEntity>

    fun findAllSessionEntityByGeneration(generation: Int): List<SessionEntity>

    /**
     * @param from Inclusive
     * @param to Inclusive
     */
    fun findScheduleEntitiesByDateBetween(
        from: LocalDate,
        to: LocalDate
    ): List<ScheduleEntity>

    fun findAllByGeneration(
        pageable: Pageable,
        generation: Int
    ): Page<SessionEntity>
}
