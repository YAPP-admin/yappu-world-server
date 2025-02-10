package co.yappuworld.user.presentation.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class UpdateFcmTokenApiRequestDto(
    @Schema(description = "FCM 토큰")
    @field:NotNull(message = "FCM TOKEN 값으로 NULL은 허용되지 않습니다.")
    val fcmToken: String
)
