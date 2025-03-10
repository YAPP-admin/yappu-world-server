package co.yappuworld.schedule.infrastructure.repository

import co.yappuworld.schedule.infrastructure.entity.SessionJdbcEntity
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SessionJdbcRepository : CrudRepository<SessionJdbcEntity, UUID>
