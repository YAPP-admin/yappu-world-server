package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.vo.Position

data class ActivityUnitAppResponseDto(
    val generation: Int,
    val position: Position
) {

    constructor(
        activityUnit: ActivityUnit
    ) : this(
        generation = activityUnit.generation,
        position = activityUnit.position
    )
}
