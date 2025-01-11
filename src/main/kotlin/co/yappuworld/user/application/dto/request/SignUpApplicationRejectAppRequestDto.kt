package co.yappuworld.user.application.dto.request

import java.util.UUID

data class SignUpApplicationRejectAppRequestDto(
    val applicationId: UUID,
    val reason: String
)
