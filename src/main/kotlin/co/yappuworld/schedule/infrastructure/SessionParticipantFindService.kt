package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.SessionParticipant
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantRepository
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SessionParticipantFindService(
    private val sessionParticipantRepository: SessionParticipantRepository
) {

    fun findSessionParticipantEntieis(sessionId: UUID): List<SessionParticipantEntity> =
        sessionParticipantRepository
            .findAll {
                select(entity(SessionParticipantEntity::class))
                    .from(entity(SessionParticipantEntity::class))
                    .where(path(SessionParticipantEntity::session)(SessionEntity::getId).equal(sessionId))
            }.filterNotNull()

    fun findSessionParticipants(sessionId: UUID): List<SessionParticipant> =
        sessionParticipantRepository
            .findAll {
                selectNew<SessionParticipant>(
                    path(ActivityUnitEntity::getId),
                    path(ActivityUnitEntity::generation),
                    path(ActivityUnitEntity::position),
                    path(UserEntity::getId),
                    path(UserEntity::name),
                    path(SessionEntity::getId),
                    path(SessionEntity::name)
                ).from(
                    entity(SessionParticipantEntity::class),
                    join(SessionParticipantEntity::activityUnit),
                    join(SessionParticipantEntity::session),
                    join(UserEntity::class).on(
                        path(SessionParticipantEntity::activityUnit)(ActivityUnitEntity::userId)
                            .equal(path(UserEntity::getId))
                    )
                ).where(path(SessionParticipantEntity::session)(SessionEntity::getId).equal(sessionId))
            }.filterNotNull()
}
