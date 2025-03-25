package co.yappuworld.user.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class UpdateDeviceAlarmRequest(
    @Schema(description = "기기 알림 설정 정보 (On: T / Off: F)")
    val deviceToggle: Boolean
)
