package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.AdminSignUpCodeUpdateAppRequestDto
import co.yappuworld.user.domain.vo.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class AdminSignUpCodeUpdateRequest(
    @Schema(description = "가입코드 업데이트 할 역할")
    val role: UserRole,
    @Schema(description = "가입코드 (000000~999999 / Int, String 어떤 형태든 상관 X)")
    @field:Min(value = 0L, message = "가입코드는 0보다 커야 합니다.")
    @field:Max(value = 999999L, message = "가입코드는 999999보다 작아야 합니다.")
    val code: Int
) {

    fun toAppRequest(): AdminSignUpCodeUpdateAppRequestDto =
        AdminSignUpCodeUpdateAppRequestDto(
            role = role,
            code = code.toString().padStart(6, '0')
        )
}
