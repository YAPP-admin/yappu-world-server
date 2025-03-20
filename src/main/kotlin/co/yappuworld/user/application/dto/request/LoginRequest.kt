package co.yappuworld.user.application.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length

data class LoginRequest(
    @Schema(description = "이메일", example = "admin@admin.com")
    @field:Email(message = "이메일 형식이 아닙니다.")
    @field:NotEmpty(message = "이메일은 필수로 입력해야 합니다.")
    val email: String,
    @Schema(description = "비밀번호", example = "abcabC!!")
    @field:Length(min = 8, max = 20, message = "올바르지 않은 비밀번호입니다.")
    val password: String
)
