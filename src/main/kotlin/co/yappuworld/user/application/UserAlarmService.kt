package co.yappuworld.user.application

import co.yappuworld.user.domain.model.UserAlarmSetting
import co.yappuworld.user.infrastructure.UserAlarmSettingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserAlarmService(
    private val userAlarmSettingRepository: UserAlarmSettingRepository
) {

    @Transactional
    fun initialize(userId: UUID) {
        userAlarmSettingRepository.save(UserAlarmSetting(userId))
    }
}
