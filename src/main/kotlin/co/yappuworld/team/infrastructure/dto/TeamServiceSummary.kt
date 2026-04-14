package co.yappuworld.team.infrastructure.dto

import java.util.UUID

data class TeamServiceSummary(
    val serviceId: UUID,
    val teamName: String,
    val generation: Int,
    val serviceName: String?,
    val hasApp: Boolean,
    val hasWeb: Boolean,
    val summary: String?,
    val isOperating: Boolean
)
