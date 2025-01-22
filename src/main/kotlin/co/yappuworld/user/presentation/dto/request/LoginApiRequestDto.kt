package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.LoginAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length

data class LoginApiRequestDto(
    @Schema(description = "이메일")
    @field:Email(message = "이메일 형식이 아닙니다.")
    @field:NotEmpty(message = "이메일은 필수로 입력해야 합니다.")
    val email: String,
    @Schema(description = "비밀번호")
    @field:Length(min = 8, max = 20, message = "올바르지 않은 비밀번호입니다.")
    val password: String
) {
    fun toAppRequest(): LoginAppRequestDto {
        return LoginAppRequestDto(
            this.email,
            this.password
        )
    }
}
