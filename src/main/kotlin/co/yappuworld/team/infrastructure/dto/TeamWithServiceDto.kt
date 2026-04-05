package co.yappuworld.team.infrastructure.dto

import java.util.UUID

data class TeamWithServiceDto(
    val teamId: UUID,
    val teamName: String,
    val generation: Int,
    val hasApp: Boolean,
    val hasWeb: Boolean,
    val serviceId: UUID?,
    val serviceName: String?
)
