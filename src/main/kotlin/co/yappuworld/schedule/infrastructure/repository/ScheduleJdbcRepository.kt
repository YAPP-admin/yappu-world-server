package co.yappuworld.schedule.infrastructure.repository

import co.yappuworld.schedule.infrastructure.entity.ScheduleJdbcEntity
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ScheduleJdbcRepository : CrudRepository<ScheduleJdbcEntity, UUID>
