package co.yappuworld.user.client.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class CheckingEmailAvailabilityRequest(
    @field:Email(message = "이메일 형식이 잘못되었습니다.")
    @field:NotEmpty(message = "이메일은 필수로 입력해야 합니다.")
    val email: String
)
