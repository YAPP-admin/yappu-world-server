package co.yappuworld.schedule.infrastructure.repository

import co.yappuworld.schedule.domain.ScheduleEntity
import co.yappuworld.schedule.domain.SessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ScheduleJpaRepository : JpaRepository<ScheduleEntity, UUID> {

    fun findSessionEntitiesByGeneration(generation: Int): List<SessionEntity>
}
