package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.Session
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SessionRepository : CrudRepository<Session, UUID>
