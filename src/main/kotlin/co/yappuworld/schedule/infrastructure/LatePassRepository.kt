package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.entity.LatePassEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LatePassRepository : JpaRepository<LatePassEntity, Long> {

    fun findByGenerationAndUserId(
        generation: Int,
        userId: UUID
    ): LatePassEntity?

    fun findAllByGeneration(generation: Int): List<LatePassEntity>

    fun findAllByGenerationAndUserIdIn(
        generation: Int,
        userIds: Collection<UUID>
    ): List<LatePassEntity>
}
