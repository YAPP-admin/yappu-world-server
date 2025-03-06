package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.UpdateDeviceAlarmAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateDeviceAlarmApiRequestDto(
    @Schema(description = "기기 알림 설정 정보 (On: T / Off: F)")
    val deviceToggle: Boolean
) {
    fun toAppRequest(): UpdateDeviceAlarmAppRequestDto = UpdateDeviceAlarmAppRequestDto(deviceToggle)
}
