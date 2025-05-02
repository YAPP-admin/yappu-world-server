package co.yappuworld.user.infrastructure.jpa

import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ActivityUnitRepository : JpaRepository<ActivityUnitEntity, UUID> {

    fun findAllByUserId(userId: UUID): List<ActivityUnitEntity>
}
