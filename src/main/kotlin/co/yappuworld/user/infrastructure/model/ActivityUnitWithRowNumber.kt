package co.yappuworld.user.infrastructure.model

import co.yappuworld.user.domain.vo.Position
import java.util.UUID

data class ActivityUnitWithRowNumber(
    val activityUnitId: UUID,
    val generation: Int,
    val position: Position,
    val userId: UUID,
    val rowNumber: Long
)
