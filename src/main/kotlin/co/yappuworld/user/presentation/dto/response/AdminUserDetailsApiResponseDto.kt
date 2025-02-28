package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.UserDetailsAppResponseDto
import java.util.UUID

data class AdminUserDetailsApiResponseDto(
    val userId: UUID,
    val name: String,
    val email: String,
    val role: UserRoleApiResponseDto,
    val isActive: Boolean,
    val activityUnits: List<ActivityUnitApiResponseDto>
) {

    constructor(response: UserDetailsAppResponseDto) : this(
        userId = response.userId,
        name = response.name,
        email = response.email,
        role = UserRoleApiResponseDto(response.role),
        isActive = response.isActive,
        activityUnits = response.activityUnits
            .map { ActivityUnitApiResponseDto(it) }
            .sortedByDescending { it.generation }
    )
}
