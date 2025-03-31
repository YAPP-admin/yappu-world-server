package co.yappuworld.user.client.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class MasterAlarmToggleResponse(
    @Schema(description = "마스터 알림 활성화 여부")
    val isEnabled: Boolean
)
