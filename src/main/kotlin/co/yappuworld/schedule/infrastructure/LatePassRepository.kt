package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.entity.LatePassEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LatePassRepository : JpaRepository<LatePassEntity, Long> {

    fun countAllByGenerationAndUserId(
        generation: Int,
        userId: UUID
    ): Int
}
