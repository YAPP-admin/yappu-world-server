package co.yappuworld.schedule.infrastructure.jpa

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SessionParticipantRepository :
    JpaRepository<SessionParticipantEntity, UUID>,
    KotlinJdslJpqlExecutor {

    fun deleteAllBySession(session: SessionEntity)

    fun deleteAllBySessionIdIn(sessionIds: List<UUID>)
}
