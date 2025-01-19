package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.CheckingEmailAvailabilityAppRequestDto
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class CheckingEmailAvailabilityApiRequestDto(
    @field:Email(message = "이메일 형식이 잘못되었습니다.")
    @field:NotEmpty(message = "이메일은 필수로 입력해야 합니다.")
    val email: String
) {
    fun toAppRequest(): CheckingEmailAvailabilityAppRequestDto {
        return CheckingEmailAvailabilityAppRequestDto(this.email)
    }
}
