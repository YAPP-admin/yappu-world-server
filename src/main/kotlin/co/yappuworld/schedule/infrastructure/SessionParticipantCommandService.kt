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

    fun invite(
        session: SessionEntity,
        activityUnit: ActivityUnitEntity
    ) {
        sessionParticipantRepository.save(
            SessionParticipantEntity(
                session = session,
                activityUnit = activityUnit
            )
        )
    }
}
