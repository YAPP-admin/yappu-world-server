package co.yappuworld.user.client.dto.response

import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import java.time.LocalDate
import java.util.UUID

data class AdminUserOverviewResponse(
    val userId: UUID,
    val name: String,
    val email: String,
    val role: UserRoleResponse,
    val registrationDate: LocalDate,
    val lastActivityUnit: AdminActivityUnitResponse
) {

    constructor(response: UserWithLastActivityUnit) : this(
        userId = response.userId,
        name = response.name,
        email = response.email,
        role = UserRoleResponse(response.role),
        registrationDate = response.createdAt.toLocalDate(),
        lastActivityUnit = AdminActivityUnitResponse(
            id = response.activityUnitId,
            generation = response.lastActiveGeneration,
            position = response.lastActivePosition.label,
            // todo : isActive 값 할당
            isActive = false
        )
    )
}
