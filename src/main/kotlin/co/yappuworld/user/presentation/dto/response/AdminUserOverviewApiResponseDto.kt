package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.UserOverviewAppResponseDto
import java.util.UUID

data class AdminUserOverviewApiResponseDto(
    val userId: UUID,
    val name: String,
    val email: String,
    val role: UserRoleApiResponseDto,
    val lastActivityUnit: ActivityUnitApiResponseDto
) {

    constructor(
        response: UserOverviewAppResponseDto
    ) : this(
        userId = response.userId,
        name = response.name,
        email = response.email,
        role = UserRoleApiResponseDto(response.role),
        lastActivityUnit = ActivityUnitApiResponseDto(response.lastActivityUnit)
    )
}
