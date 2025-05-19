package co.yappuworld.user.infrastructure.jpa

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ActivityUnitRepository : JpaRepository<ActivityUnitEntity, UUID> {

    fun findAllByUserId(userId: UUID): List<ActivityUnitEntity>
}
