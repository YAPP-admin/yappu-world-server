package co.yappuworld.user.infrastructure.model

import co.yappuworld.user.domain.vo.Position
import java.time.LocalDate
import java.util.UUID

data class UserPersonProfileHistoryProjection(
    val activityUnitId: UUID,
    val generation: Int,
    val position: Position,
    val activityStartDate: LocalDate?,
    val activityEndDate: LocalDate?,
    val teamName: String?,
    val serviceId: UUID?,
    val serviceName: String?,
    val summary: String?,
    val hasApp: Boolean?,
    val hasWeb: Boolean?
)
