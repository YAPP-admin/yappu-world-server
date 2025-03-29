package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.ActivityUnitEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ActivityUnitJpaRepository : JpaRepository<ActivityUnitEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<ActivityUnitEntity>
}
