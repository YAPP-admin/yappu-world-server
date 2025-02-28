package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.User
import co.yappuworld.user.domain.vo.UserRole
import java.util.UUID

data class UserDetailsAppResponseDto(
    val userId: UUID,
    val email: String,
    val name: String,
    val role: UserRole,
    val isActive: Boolean,
    val activityUnits: List<ActivityUnitAppResponseDto>
) {

    constructor(user: User, activityUnits: List<ActivityUnit>) : this(
        userId = user.id,
        email = user.email,
        name = user.name,
        role = user.role,
        isActive = user.isActive,
        activityUnits = activityUnits.map { ActivityUnitAppResponseDto(it) }
    )
}
