package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.UserSignUpAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

@Schema(description = "회원가입 요청")
data class UserSignUpApiRequestDto(
    @Email
    @Schema(description = "이메일", requiredMode = Schema.RequiredMode.REQUIRED)
    val email: String,
    @NotEmpty
    @Schema(description = "비밀번호, 8~20자")
    val password: String,
    @NotEmpty
    @Schema(description = "실명")
    val name: String,
    @NotEmpty
    @Schema(description = "활동한 기수와 직군 기입")
    val activityUnits: List<ActivityUnitApiRequestDto>,
    @Schema(description = "가입코드, 6자리 숫자", example = "000000")
    val signUpCode: String
) {
    fun toAppRequest(): UserSignUpAppRequestDto {
        return UserSignUpAppRequestDto(
            this.email,
            this.password,
            this.name,
            this.activityUnits.map { it.toAppRequest() },
            this.signUpCode
        )
    }
}
