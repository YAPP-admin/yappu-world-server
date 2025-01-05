package co.yappuworld.user.presentation.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

@Schema(description = "회원가입 요청")
data class UserSignUpRequestDto(
    @Email
    @Schema(description = "이메일", required = true, requiredMode = Schema.RequiredMode.REQUIRED)
    val email: String,
    @NotEmpty
    @Schema(description = "비밀번호, 8~20자", required = true)
    val password: String,
    @NotEmpty
    @Schema(description = "실명", required = true)
    val name: String,
    @NotEmpty
    @Schema(description = "활동한 기수와 직군 기입", required = true)
    val activityUnits: List<ActivityUnitRequestDto>
)
