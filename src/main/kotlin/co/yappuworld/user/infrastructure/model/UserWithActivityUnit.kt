package co.yappuworld.user.infrastructure.model

import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import java.util.UUID

data class UserWithActivityUnit(
    val userId: UUID,
    val email: String,
    val name: String,
    val role: UserRole,
    val generation: Int,
    val position: Position
)
