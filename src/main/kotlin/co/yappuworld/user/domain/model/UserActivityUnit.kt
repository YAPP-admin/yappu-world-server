package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import java.util.UUID

class UserActivityUnit(
    val userId: UUID,
    val email: String,
    val name: String,
    val role: UserRole,
    val activityUnitId: UUID,
    val generation: Int,
    val position: Position
)
