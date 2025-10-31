package co.yappuworld.team.infrastructure.jpa

import co.yappuworld.team.infrastructure.entity.ServiceEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ServiceRepository : JpaRepository<ServiceEntity, UUID>
