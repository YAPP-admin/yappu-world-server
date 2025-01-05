package co.yappuworld.user.presentation.dto.request

import co.yappuworld.global.vo.UserRole
import co.yappuworld.user.domain.User
import com.github.f4b6a3.ulid.UlidCreator
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

@Schema(description = "회원가입 요청")
data class UserSignUpRequestDto(
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
    val activityUnits: List<ActivityUnitRequestDto>,
    @Schema(description = "가입코드, 6자리 숫자", example = "000000")
    val signUpCode: String
) {
    fun toUser(role: UserRole): User {
        return User(
            UlidCreator.getMonotonicUlid().toUuid(),
            this.email,
            this.password,
            this.name,
            role
        )
    }
}
