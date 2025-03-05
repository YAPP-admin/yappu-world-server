package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.vo.Position
import java.util.UUID

data class ActivityUnitAppResponseDto(
    val id: UUID,
    val generation: Int,
    val position: Position
) {

    constructor(
        activityUnit: ActivityUnit
    ) : this(
        id = activityUnit.id,
        generation = activityUnit.generation,
        position = activityUnit.position
    )
}
