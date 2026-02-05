package co.yappuworld.user.infrastructure.jpa

import co.yappuworld.user.infrastructure.entity.SignUpApplicationActivityUnitEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SignUpApplicationActivityUnitRepository : JpaRepository<SignUpApplicationActivityUnitEntity, UUID>
