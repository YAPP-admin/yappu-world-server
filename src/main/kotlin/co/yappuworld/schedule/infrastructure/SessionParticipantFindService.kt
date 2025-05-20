package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SessionParticipantFindService(
    private val sessionParticipantRepository: SessionParticipantRepository
) {

    fun findSessionParticipants(sessionId: UUID): List<SessionParticipantEntity> =
        sessionParticipantRepository
            .findAll {
                select(entity(SessionParticipantEntity::class))
                    .from(entity(SessionParticipantEntity::class))
                    .where(path(SessionParticipantEntity::session)(SessionEntity::getId).equal(sessionId))
            }.filterNotNull()
}
