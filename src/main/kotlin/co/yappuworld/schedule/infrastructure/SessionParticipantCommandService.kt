package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantRepository
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SessionParticipantCommandService(
    private val sessionParticipantRepository: SessionParticipantRepository
) {

    fun saveAll(
        session: SessionEntity,
        activityUnits: List<ActivityUnitEntity>
    ) {
        sessionParticipantRepository.saveAll(
            activityUnits.map { activityUnit ->
                SessionParticipantEntity(
                    session = session,
                    activityUnit = activityUnit
                )
            }
        )
    }

    fun deleteAll(sessionParticipants: List<SessionParticipantEntity>) {
        sessionParticipantRepository.deleteAll(sessionParticipants)
    }

    fun deleteAllSessionParticipants(session: SessionEntity) {
        sessionParticipantRepository.deleteAllBySession(session)
    }

    fun deleteAllSessionParticipants(sessions: List<SessionEntity>) {
        sessionParticipantRepository.deleteAllBySessionIdIn(sessions.map { it.id })
    }
}
