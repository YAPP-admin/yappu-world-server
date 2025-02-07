package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.UserAlarmSetting
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface UserAlarmSettingRepository : CrudRepository<UserAlarmSetting, UUID> {

    fun findUserAlarmSettingOrNullByUserId(userId: UUID): UserAlarmSetting?
}
