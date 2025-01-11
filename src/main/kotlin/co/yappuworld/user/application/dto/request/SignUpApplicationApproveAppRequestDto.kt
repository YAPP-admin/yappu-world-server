package co.yappuworld.user.application.dto.request

import co.yappuworld.user.domain.UserRole
import java.util.UUID

data class SignUpApplicationApproveAppRequestDto(
    val applicationId: UUID,
    val role: UserRole
)
