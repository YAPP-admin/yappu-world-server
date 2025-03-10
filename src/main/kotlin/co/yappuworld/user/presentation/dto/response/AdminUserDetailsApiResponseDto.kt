package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.UserDetailsAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

data class AdminUserDetailsApiResponseDto(
    @Schema(description = "ID")
    val userId: UUID,
    @Schema(description = "이름")
    val name: String,
    @Schema(description = "이메일")
    val email: String,
    @Schema(description = "역할")
    val role: UserRoleApiResponseDto,
    @Schema(description = "계정 살아있는 여부(F = 탈퇴)")
    val isActive: Boolean,
    @Schema(description = "가입일")
    val registrationDate: LocalDate,
    @Schema(description = "활동 내역")
    val activityUnits: List<AdminActivityUnitApiResponseDto>
) {

    constructor(response: UserDetailsAppResponseDto) : this(
        userId = response.userId,
        name = response.name,
        email = response.email,
        role = UserRoleApiResponseDto(response.role),
        isActive = response.isActive,
        registrationDate = response.registrationDate,
        activityUnits = response.activityUnits
            .map { AdminActivityUnitApiResponseDto(it) }
            .sortedByDescending { it.generation }
    )
}
