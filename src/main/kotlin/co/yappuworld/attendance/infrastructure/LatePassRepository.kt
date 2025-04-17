package co.yappuworld.attendance.infrastructure

import co.yappuworld.attendance.domain.LatePass
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LatePassRepository : JpaRepository<LatePass, Long> {

    fun countAllByGenerationAndUserId(
        generation: Int,
        userId: UUID
    ): Int
}
