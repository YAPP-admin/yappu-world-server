package co.yappuworld.user.client.dto.response

import co.yappuworld.operation.domain.ConfigEntity
import co.yappuworld.user.domain.vo.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class AdminSignUpCodesResponse(
    @field:Schema(description = "인증 번호 목록")
    val codes: List<AdminSignUpCodeResponse>
)

data class AdminSignUpCodeResponse(
    @field:Schema(description = "인증 번호")
    @field:NotNull
    val code: String,
    @field:Schema(description = "역할")
    @field:NotNull
    val role: UserRoleResponse
) {

    constructor(config: ConfigEntity?, role: UserRole) : this(
        code = config?.value ?: "",
        role = UserRoleResponse(role)
    )
}
