package co.yappuworld.user.client.dto.request

import co.yappuworld.user.domain.model.ApplicationDetails
import co.yappuworld.user.domain.model.SignUpApplication
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length

@Schema(description = "회원가입 요청")
data class UserSignUpRequest(
    @Schema(description = "이메일", example = "email@email.com")
    @field:Email(message = "올바르지 않은 이메일 형식입니다.")
    @field:NotEmpty(message = "이메일은 필수로 입력해야 합니다.")
    val email: String,
    @Schema(description = "비밀번호, 8~20자", example = "abcabC!!")
    @field:NotEmpty(message = "비밀번호는 필수로 입력해야 합니다.")
    @field:Length(min = 8, max = 20, message = "올바르지 않은 비밀번호입니다.")
    val password: String,
    @Schema(description = "실명", example = "홍길동")
    @field:NotEmpty(message = "실명은 필수로 입력해야 합니다.")
    val name: String,
    @Schema(description = "활동한 기수와 직군 기입")
    @field:NotEmpty
    val activityUnits: List<ActivityUnitRegistrationRequest>,
    @Schema(description = "가입코드, 6자리 숫자", example = "000000", nullable = true)
    val signUpCode: String?,
    @Schema(description = "FCM 토큰")
    @field:NotEmpty(message = "FCM 토큰은 필수로 전달되어야 합니다.")
    val fcmToken: String,
    @Schema(description = "기기에서 알림 설정을 켰는지 여부")
    val deviceAlarmToggle: Boolean
) {

    fun toApplication(): SignUpApplication =
        SignUpApplication(
            ApplicationDetails(
                this.email,
                this.password,
                this.name,
                this.activityUnits.map { it.toActivityUnitParam() },
                this.fcmToken,
                this.deviceAlarmToggle
            )
        )
}
