package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.ActivityUnit
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ActivityUnitRepository : CrudRepository<ActivityUnit, UUID> {
    fun findAllByUserId(userId: UUID): List<ActivityUnit>
}
