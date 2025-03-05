package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.AdminActivityUnitUpdateAppRequestDto
import co.yappuworld.user.application.dto.request.AdminUserUpdateAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AdminUserUpdateApiRequestDto(
    @Schema(description = "변경 대상 유저 ID")
    @field:NotNull(message = "유저 ID는 필수 입력 값입니다.")
    val userId: UUID,
    @Schema(description = "이름")
    @field:NotBlank(message = "유저 이름은 필수 입력 값입니다.")
    val name: String,
    @Schema(description = "이메일")
    @field:NotBlank(message = "유저 이메일은 필수 입력 값입니다.")
    val email: String,
    @Schema(description = "활동내역")
    @field:NotEmpty(message = "유저 활동내역은 필수 입력 값입니다.")
    val activityUnits: List<AdminActivityUnitUpdateApiRequestDto>
) {

    fun toAppRequest(): AdminUserUpdateAppRequestDto {
        return AdminUserUpdateAppRequestDto(
            userId = userId,
            name = name,
            email = email,
            activityUnits = activityUnits.map { AdminActivityUnitUpdateAppRequestDto(it) }
        )
    }
}
