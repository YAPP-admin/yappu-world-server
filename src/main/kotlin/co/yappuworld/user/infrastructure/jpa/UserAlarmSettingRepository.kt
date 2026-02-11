package co.yappuworld.user.infrastructure.jpa

import co.yappuworld.user.infrastructure.entity.UserAlarmSettingEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserAlarmSettingRepository : JpaRepository<UserAlarmSettingEntity, UUID> {

    fun findUserAlarmSettingOrNullByUserId(userId: UUID): UserAlarmSettingEntity?

    fun deleteByUserId(userId: UUID)
}
