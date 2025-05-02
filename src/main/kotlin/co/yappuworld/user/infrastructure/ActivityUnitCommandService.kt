package co.yappuworld.user.infrastructure

import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ActivityUnitCommandService(
    private val activityUnitRepository: ActivityUnitRepository
) {

    fun saveAll(activityUnits: List<ActivityUnitEntity>) {
        require(activityUnits.isNotEmpty()) { "최소 하나의 저장 대상이 필요합니다." }
        activityUnitRepository.saveAll(activityUnits)
    }

    fun deleteAll(ids: List<UUID>) {
        require(ids.isNotEmpty()) { "최소 하나 이상의 삭제 아이템이 필요합니다." }
        activityUnitRepository.deleteAllById(ids)
    }
}
