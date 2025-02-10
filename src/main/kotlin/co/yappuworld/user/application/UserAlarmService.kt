package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.application.dto.request.UpdateDeviceAlarmAppRequestDto
import co.yappuworld.user.application.dto.response.MasterAlarmToggleAppResponse
import co.yappuworld.user.application.dto.response.UserAlarmStatusAppResponse
import co.yappuworld.user.domain.model.UserAlarmSetting
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.UserAlarmSettingRepository
import co.yappuworld.user.infrastructure.UserDeviceRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private val logger = KotlinLogging.logger { }

@Service
class UserAlarmService(
    private val userAlarmSettingRepository: UserAlarmSettingRepository,
    private val userDeviceRepository: UserDeviceRepository
) {

    @Transactional(readOnly = true)
    fun getAlarmStatus(userId: UUID): UserAlarmStatusAppResponse {
        return UserAlarmStatusAppResponse.of(getUserAlarmSetting(userId))
    }

    @Transactional
    fun updateDeviceAlarm(
        userId: UUID,
        request: UpdateDeviceAlarmAppRequestDto
    ) {
        val setting = getUserAlarmSetting(userId).apply { updateDeviceAlarm(request.deviceToggle) }
        userAlarmSettingRepository.save(setting)
    }

    @Transactional
    fun toggleMasterAlarm(userId: UUID): MasterAlarmToggleAppResponse {
        val setting = getUserAlarmSetting(userId)
        setting.toggleMaster()
        userAlarmSettingRepository.save(setting)

        return MasterAlarmToggleAppResponse(setting.master)
    }

    @Transactional
    fun updateFcmToken(
        serId: UUID,
        fcmToken: String
    ) {
        userDeviceRepository.findUserDeviceOrNullByUserId(userId)
            ?.apply { updateFcmToken(fcmToken) }
            ?.also { userDeviceRepository.save(it) }
            ?: throw BusinessException(UserError.USER_RELATED_DATA_NOT_FOUND)
    }

    private fun getUserAlarmSetting(userId: UUID): UserAlarmSetting {
        return userAlarmSettingRepository.findUserAlarmSettingOrNullByUserId(userId)
            ?: run {
                logger.error { "${userId}의 알람 데이터가 존재하지 않습니다. 데이터를 확인하세요" }
                throw BusinessException(UserError.USER_RELATED_DATA_NOT_FOUND)
            }
    }
}
