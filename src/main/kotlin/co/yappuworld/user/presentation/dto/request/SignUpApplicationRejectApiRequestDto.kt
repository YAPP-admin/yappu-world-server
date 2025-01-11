package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.SignUpApplicationRejectAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class SignUpApplicationRejectApiRequestDto(
    @Schema(description = "신청서 ID")
    val applicationId: UUID,
    @Schema(description = "거절 사유")
    val reason: String
) {
    fun toAppRequest(): SignUpApplicationRejectAppRequestDto {
        return SignUpApplicationRejectAppRequestDto(
            applicationId,
            reason
        )
    }
}
