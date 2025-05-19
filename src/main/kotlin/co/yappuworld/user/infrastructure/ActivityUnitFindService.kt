package co.yappuworld.user.infrastructure

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
}
