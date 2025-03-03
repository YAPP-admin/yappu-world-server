package co.yappuworld.user.application.dto.request

import java.util.UUID

data class AdminUserUpdateAppRequestDto(
    val userId: UUID,
    val name: String,
    val email: String,
    val activityUnits: List<AdminActivityUnitUpdateAppRequestDto>
)
