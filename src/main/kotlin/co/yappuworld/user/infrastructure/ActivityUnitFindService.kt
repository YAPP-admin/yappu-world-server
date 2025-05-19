package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class ActivityUnitFindService(
    private val activityUnitRepository: ActivityUnitRepository
) {

    fun findActivityUnits(userId: UUID): List<ActivityUnitEntity> = activityUnitRepository.findAllByUserId(userId)

    fun findParticipants(generation: Int): List<ActivityUnitEntity> =
        activityUnitRepository
            .findAll {
                select(entity(ActivityUnitEntity::class))
                    .from(entity(ActivityUnitEntity::class))
                    .whereAnd(
                        path(ActivityUnitEntity::generation).equal(generation),
                        path(ActivityUnitEntity::position).`in`(Position.participantPositions)
                    )
            }.filterNotNull()

    fun findParticipants(
        generation: Int,
        userIds: List<UUID>
    ): List<ActivityUnitEntity> =
        activityUnitRepository
            .findAll {
                select(entity(ActivityUnitEntity::class))
                    .from(entity(ActivityUnitEntity::class))
                    .whereAnd(
                        path(ActivityUnitEntity::generation).equal(generation),
                        path(ActivityUnitEntity::position).`in`(Position.participantPositions),
                        path(ActivityUnitEntity::userId).`in`(userIds)
                    )
            }.filterNotNull()
}
