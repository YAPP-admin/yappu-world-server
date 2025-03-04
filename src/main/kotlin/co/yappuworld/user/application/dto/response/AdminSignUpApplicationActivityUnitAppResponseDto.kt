package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.ActivityUnitParam
import co.yappuworld.user.domain.vo.Position

data class AdminSignUpApplicationActivityUnitAppResponseDto(
    val generation: Int,
    val position: Position
) {

    constructor(param: ActivityUnitParam) : this(
        generation = param.generation,
        position = param.position
    )
}
