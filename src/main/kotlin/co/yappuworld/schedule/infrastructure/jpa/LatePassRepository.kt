package co.yappuworld.schedule.infrastructure.jpa

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LatePassRepository : JpaRepository<LatePassEntity, UUID> {

    fun countAllByGenerationAndUserId(
        generation: Int,
        userId: UUID
    ): Int

    fun findAllByGeneration(generation: Int): List<LatePassEntity>
}
