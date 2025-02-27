package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.Schedule
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ScheduleRepository : CrudRepository<Schedule, UUID>
