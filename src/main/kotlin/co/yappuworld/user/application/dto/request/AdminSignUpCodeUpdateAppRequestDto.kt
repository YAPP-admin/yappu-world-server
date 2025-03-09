package co.yappuworld.user.application.dto.request

import co.yappuworld.user.domain.vo.UserRole

data class AdminSignUpCodeUpdateAppRequestDto(
    val role: UserRole,
    val code: String
)
