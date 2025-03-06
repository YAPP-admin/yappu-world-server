package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.MasterAlarmToggleAppResponse
import io.swagger.v3.oas.annotations.media.Schema

data class MasterAlarmToggleApiResponse(
    @Schema(description = "마스터 알림 활성화 여부")
    val isEnabled: Boolean
) {

    companion object {
        fun of(response: MasterAlarmToggleAppResponse): MasterAlarmToggleApiResponse =
            MasterAlarmToggleApiResponse(response.isEnabled)
    }
}
