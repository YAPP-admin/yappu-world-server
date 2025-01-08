package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.domain.UserRole
import co.yappuworld.user.application.dto.request.SignUpApplicationApproveAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class SignUpApplicationApproveApiRequestDto(
    @Schema(description = "회원가입 신청 ID")
    val applicationId: UUID,
    @Schema(description = "회원 역할")
    val role: UserRole
) {
    fun toAppRequest(): SignUpApplicationApproveAppRequestDto {
        return SignUpApplicationApproveAppRequestDto(
            applicationId,
            role
        )
    }
}
