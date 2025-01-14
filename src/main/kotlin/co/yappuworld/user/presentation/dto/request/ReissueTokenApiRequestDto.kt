package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.ReissueTokenAppRequestDto
import java.time.LocalDateTime

data class ReissueTokenApiRequestDto(
    val accessToken: String,
    val refreshToken: String
) {

    fun toAppRequest(now: LocalDateTime): ReissueTokenAppRequestDto {
        return ReissueTokenAppRequestDto(
            this.accessToken,
            this.refreshToken,
            now
        )
    }
}
