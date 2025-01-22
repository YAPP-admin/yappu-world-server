package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.vo.Position
import java.util.UUID

data class ActivityUnitParam(
    val generation: Int,
    val position: Position
) {
    fun toActivityUnit(userId: UUID): ActivityUnit {
        return ActivityUnit(
            generation,
            position,
            userId
        )
    }
}
