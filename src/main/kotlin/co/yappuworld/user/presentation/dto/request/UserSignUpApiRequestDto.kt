package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.UserSignUpAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length

@Schema(description = "회원가입 요청")
data class UserSignUpApiRequestDto(
    @Schema(description = "이메일", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:Email(message = "올바르지 않은 이메일 형식입니다.")
    @field:NotEmpty(message = "이메일은 필수로 입력해야 합니다.")
    val email: String,
    @Schema(description = "비밀번호, 8~20자")
    @field:NotEmpty(message = "비밀번호는 필수로 입력해야 합니다.")
    @field:Length(min = 8, max = 20, message = "올바르지 않은 비밀번호입니다.")
    val password: String,
    @Schema(description = "실명")
    @field:NotEmpty(message = "실명은 필수로 입력해야 합니다.")
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
