package co.yappuworld.user.presentation.dto.response

import co.yappuworld.operation.domain.Config
import co.yappuworld.user.domain.vo.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class AdminSignUpAuthenticationCodesApiResponseDto(
    @Schema(description = "인증 번호 목록")
    val codes: List<AdminSignUpAuthenticationCodeApiResponseDto>
)

data class AdminSignUpAuthenticationCodeApiResponseDto(
    @Schema(description = "인증 번호")
    @field:NotNull
    val code: String,
    @Schema(description = "역할")
    @field:NotNull
    val role: UserRoleApiResponseDto
) {

    constructor(config: Config?, role: UserRole) : this(
        code = config?.value ?: "",
        role = UserRoleApiResponseDto(role)
    )
}
