package co.yappuworld.user.client.application

import co.yappuworld.user.client.dto.request.UpdateDeviceAlarmRequest
import co.yappuworld.user.client.dto.response.MasterAlarmToggleResponse
import co.yappuworld.user.client.dto.response.UserAlarmStatusResponse
import co.yappuworld.user.infrastructure.UserAlarmSettingFindService
import co.yappuworld.user.infrastructure.UserDeviceFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserAlarmService(
    private val userDeviceFindService: UserDeviceFindService,
    private val userAlarmSettingFindService: UserAlarmSettingFindService
) {

    @Transactional(readOnly = true)
    fun getAlarmStatus(userId: UUID): UserAlarmStatusResponse =
        UserAlarmStatusResponse.of(userAlarmSettingFindService.findUserAlarmSetting(userId))

    @Transactional
    fun updateDeviceAlarm(
        userId: UUID,
        request: UpdateDeviceAlarmRequest
    ) {
        userAlarmSettingFindService
            .findUserAlarmSetting(userId)
            .apply { updateDeviceAlarm(request.deviceToggle) }
    }

    @Transactional
    fun toggleMasterAlarm(userId: UUID): MasterAlarmToggleResponse =
        userAlarmSettingFindService
            .findUserAlarmSetting(userId)
            .apply { toggleMaster() }
            .let { setting -> MasterAlarmToggleResponse(setting.master) }

    @Transactional
    fun updateFcmToken(
        userId: UUID,
        fcmToken: String
    ) {
        userDeviceFindService
            .findUserDevice(userId)
            .apply { updateFcmToken(fcmToken) }
    }
}
