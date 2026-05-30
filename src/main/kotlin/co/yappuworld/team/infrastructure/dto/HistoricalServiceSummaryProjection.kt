package co.yappuworld.team.infrastructure.dto

import java.util.UUID

data class HistoricalServiceSummaryProjection(
    val serviceId: UUID,
    val generation: Int,
    val serviceName: String?,
    val hasApp: Boolean,
    val hasWeb: Boolean,
    val summary: String?
)
