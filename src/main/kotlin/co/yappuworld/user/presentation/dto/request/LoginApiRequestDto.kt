package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.LoginAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema

data class LoginApiRequestDto(
    @Schema(description = "이메일")
    val email: String,
    @Schema(description = "비밀번호")
    val password: String
) {
    fun toAppRequest(): LoginAppRequestDto {
        return LoginAppRequestDto(
            this.email,
            this.password
        )
    }
}
