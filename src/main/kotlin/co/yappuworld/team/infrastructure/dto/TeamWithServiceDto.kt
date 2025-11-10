package co.yappuworld.team.infrastructure.dto

import co.yappuworld.team.infrastructure.entity.ServiceLinks
import java.util.UUID

data class TeamWithServiceDto(
    val teamId: UUID,
    val teamName: String,
    val generation: Int,
    val serviceId: UUID?,
    val serviceName: String?,
    val hasApp: Boolean?,
    val hasWeb: Boolean?,
    val serviceLinks: ServiceLinks?
)
