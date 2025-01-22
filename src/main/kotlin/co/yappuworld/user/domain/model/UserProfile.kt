package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.UserRole
import java.util.UUID

data class UserProfile(
    val id: UUID,
    val name: String,
    val role: UserRole
)
