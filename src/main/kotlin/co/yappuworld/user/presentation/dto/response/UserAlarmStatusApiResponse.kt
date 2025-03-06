package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.UserAlarmStatusAppResponse
import io.swagger.v3.oas.annotations.media.Schema

data class UserAlarmStatusApiResponse(
    @Schema(description = "마스터 알림 활성화 여부")
    val isMasterEnabled: Boolean
) {

    companion object {
        fun of(response: UserAlarmStatusAppResponse): UserAlarmStatusApiResponse =
            UserAlarmStatusApiResponse(response.isMasterEnabled)
    }
}
