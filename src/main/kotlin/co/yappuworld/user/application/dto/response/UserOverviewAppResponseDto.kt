package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import java.time.LocalDate
import java.util.UUID

data class UserOverviewAppResponseDto(
    val userId: UUID,
    val name: String,
    val email: String,
    val role: UserRole,
    val registrationDate: LocalDate,
    val lastActivityUnit: ActivityUnitAppResponseDto
) {

    constructor(
        userWithLastActivityUnit: UserWithLastActivityUnit
    ) : this(
        userId = userWithLastActivityUnit.userId,
        name = userWithLastActivityUnit.name,
        email = userWithLastActivityUnit.email,
        role = userWithLastActivityUnit.role,
        registrationDate = userWithLastActivityUnit.createdAt.toLocalDate(),
        lastActivityUnit = ActivityUnitAppResponseDto(userWithLastActivityUnit.activityUnit)
    )
}
