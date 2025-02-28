package co.yappuworld.user.application.dto.request

import co.yappuworld.user.domain.vo.UserRole
import java.util.UUID

data class UserRoleUpdateAppRequestDto(
    val userId: UUID,
    val role: UserRole
)
