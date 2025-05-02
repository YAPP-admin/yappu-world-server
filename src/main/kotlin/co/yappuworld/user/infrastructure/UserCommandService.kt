package co.yappuworld.user.infrastructure

import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity
import co.yappuworld.user.infrastructure.entity.UserAlarmSettingEntity
import co.yappuworld.user.infrastructure.entity.UserDeviceEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserAlarmSettingRepository
import co.yappuworld.user.infrastructure.jpa.UserDeviceRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val userAlarmSettingRepository: UserAlarmSettingRepository,
    private val userDeviceRepository: UserDeviceRepository
) {

    fun signUp(
        application: SignUpApplicationEntity,
        role: UserRole
    ): UserEntity =
        application.toUser(role).also { user ->
            userRepository.save(user)
            activityUnitRepository.saveAll(application.toActivityUnits(user.id))
            userAlarmSettingRepository.save(UserAlarmSettingEntity(user.id, application.getDeviceAlarmToggle()))
            userDeviceRepository.save(UserDeviceEntity(user.id, application.getFcmToken()))
        }
}
