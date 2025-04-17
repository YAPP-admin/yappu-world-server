package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.LatePass
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LatePassRepository : JpaRepository<LatePass, Long> {

    fun countAllByGenerationAndUserId(
        generation: Int,
        userId: UUID
    ): Int
}
