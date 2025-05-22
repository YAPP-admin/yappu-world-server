package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.SessionParticipant
import co.yappuworld.schedule.infrastructure.jdsl.CustomSessionDsl
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantRepository
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SessionParticipantFindService(
    private val sessionParticipantRepository: SessionParticipantRepository
) {

    fun findSessionParticipantEntities(sessionId: UUID): List<SessionParticipantEntity> =
        sessionParticipantRepository
            .findAll {
                select(entity(SessionParticipantEntity::class))
                    .from(entity(SessionParticipantEntity::class))
                    .where(path(SessionParticipantEntity::session)(SessionEntity::getId).equal(sessionId))
            }.filterNotNull()

    fun findSessionParticipantEntities(
        sessionAndUserActivityUnitIds: List<Pair<UUID, UUID>>
    ): List<SessionParticipantEntity> =
        sessionParticipantRepository
            .findAll {
                val predicates = sessionAndUserActivityUnitIds.map { (sessionId, activityUnitId) ->
                    and(
                        path(SessionParticipantEntity::session)(SessionEntity::getId).equal(sessionId),
                        path(SessionParticipantEntity::activityUnit)(ActivityUnitEntity::getId).equal(activityUnitId)
                    )
                }

                select(entity(SessionParticipantEntity::class))
                    .from(entity(SessionParticipantEntity::class))
                    .where(or(*predicates.toTypedArray()))
            }.filterNotNull()

    fun findSessionParticipants(sessionId: UUID): List<SessionParticipant> =
        sessionParticipantRepository
            .findAll(CustomSessionDsl) {
                selectFromSessionParticipant()
                    .where(path(SessionParticipantEntity::session)(SessionEntity::getId).equal(sessionId))
            }.filterNotNull()

    fun findSessionParticipantsInGeneration(generation: Int): List<SessionParticipant> =
        sessionParticipantRepository
            .findAll(CustomSessionDsl) {
                selectFromSessionParticipant()
                    .where(path(SessionEntity::generation).equal(generation))
            }.filterNotNull()
}
