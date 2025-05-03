package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.vo.Position
import java.util.UUID

class ActivityUnit(
    val generation: Int,
    val position: Position,
    val userId: UUID
)
