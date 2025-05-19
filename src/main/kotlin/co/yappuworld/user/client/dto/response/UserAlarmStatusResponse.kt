package co.yappuworld.user.client.dto.response

import co.yappuworld.user.infrastructure.jpa.UserAlarmSettingEntity
import io.swagger.v3.oas.annotations.media.Schema

data class UserAlarmStatusResponse(
    @Schema(description = "마스터 알림 활성화 여부")
    val isMasterEnabled: Boolean
) {

    companion object {
        fun of(setting: UserAlarmSettingEntity): UserAlarmStatusResponse =
            UserAlarmStatusResponse(
                isMasterEnabled = setting.master
            )
    }
}
