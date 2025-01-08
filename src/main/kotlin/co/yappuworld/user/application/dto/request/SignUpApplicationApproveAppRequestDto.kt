package co.yappuworld.user.application.dto.request

import co.yappuworld.global.vo.UserRole
import java.util.UUID

data class SignUpApplicationApproveAppRequestDto(
    val applicationId: UUID,
    val role: UserRole
)
