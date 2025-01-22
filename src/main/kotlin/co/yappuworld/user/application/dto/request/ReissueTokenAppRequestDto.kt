package co.yappuworld.user.application.dto.request

import java.time.LocalDateTime

data class ReissueTokenAppRequestDto(
    val accessToken: String,
    val refreshToken: String,
    val now: LocalDateTime
)
