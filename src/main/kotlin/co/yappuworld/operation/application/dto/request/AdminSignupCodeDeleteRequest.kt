package co.yappuworld.operation.application.dto.request

import co.yappuworld.user.domain.vo.UserRole
import io.swagger.v3.oas.annotations.media.Schema

data class AdminSignupCodeDeleteRequest(
    @Schema(description = "가입 코드 삭제할 역할")
    val role: UserRole
)
