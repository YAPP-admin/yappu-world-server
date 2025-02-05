package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.UserAlarmSetting

data class UserAlarmStatusAppResponse(
    val isMasterEnabled: Boolean
) {
    companion object {
        fun of(setting: UserAlarmSetting): UserAlarmStatusAppResponse {
            return UserAlarmStatusAppResponse(
                isMasterEnabled = setting.master
            )
        }
    }
}
