package co.yappuworld.user.infrastructure.dto

import co.yappuworld.user.domain.vo.Position
import java.util.UUID

data class ActivityUnitWithRowNumber(
    val activityUnitId: UUID,
    val generation: Int,
    val position: Position,
    val userId: UUID,
    val rowNumber: Long
)
