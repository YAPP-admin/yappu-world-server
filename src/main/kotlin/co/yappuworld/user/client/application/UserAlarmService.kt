package co.yappuworld.user.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.client.dto.request.UpdateDeviceAlarmRequest
import co.yappuworld.user.client.dto.response.MasterAlarmToggleResponse
import co.yappuworld.user.client.dto.response.UserAlarmStatusResponse
import co.yappuworld.user.domain.model.UserAlarmSettingEntity
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.jpa.UserAlarmSettingRepository
import co.yappuworld.user.infrastructure.jpa.UserDeviceRepository
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
    fun getAlarmStatus(userId: UUID): UserAlarmStatusResponse = UserAlarmStatusResponse.of(getUserAlarmSetting(userId))

    @Transactional
    fun updateDeviceAlarm(
        userId: UUID,
        request: UpdateDeviceAlarmRequest
    ) {
        getUserAlarmSetting(userId)
            .apply { updateDeviceAlarm(request.deviceToggle) }
            .also { userAlarmSettingRepository.save(it) }
    }

    @Transactional
    fun toggleMasterAlarm(userId: UUID): MasterAlarmToggleResponse {
        val setting = getUserAlarmSetting(userId)
        setting.toggleMaster()
        userAlarmSettingRepository.save(setting)

        return MasterAlarmToggleResponse(setting.master)
    }

    @Transactional
    fun updateFcmToken(
        userId: UUID,
        fcmToken: String
    ) {
        userDeviceRepository
            .findUserDeviceOrNullByUserId(userId)
            ?.apply { updateFcmToken(fcmToken) }
            ?: throw BusinessException(UserError.USER_RELATED_DATA_NOT_FOUND)
    }

    private fun getUserAlarmSetting(userId: UUID): UserAlarmSettingEntity =
        userAlarmSettingRepository.findUserAlarmSettingOrNullByUserId(userId)
            ?: run {
                logger.error { "${userId}의 알람 데이터가 존재하지 않습니다. 데이터를 확인하세요" }
                throw BusinessException(UserError.USER_RELATED_DATA_NOT_FOUND)
            }
}
