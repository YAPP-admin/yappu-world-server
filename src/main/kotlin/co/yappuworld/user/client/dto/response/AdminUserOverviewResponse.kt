package co.yappuworld.user.client.dto.response

import co.yappuworld.user.domain.model.UserActivityUnit
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

    constructor(response: UserActivityUnit) : this(
        userId = response.userId,
        name = response.name,
        email = response.email,
        role = UserRoleResponse(response.role),
        registrationDate = response.createdAt.toLocalDate(),
        lastActivityUnit = AdminActivityUnitResponse(
            id = response.activityUnitId,
            generation = response.generation,
            position = response.position.label,
            isActive = response.isActive
        )
    )
}
