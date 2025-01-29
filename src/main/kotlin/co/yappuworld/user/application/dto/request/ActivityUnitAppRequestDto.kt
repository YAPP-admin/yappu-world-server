package co.yappuworld.user.application.dto.request

import co.yappuworld.user.domain.model.ActivityUnitParam
import co.yappuworld.user.domain.vo.Position

data class ActivityUnitAppRequestDto(
    val generation: Int,
    val position: Position
) {
    fun toActivityUnitParam(): ActivityUnitParam {
        return ActivityUnitParam(
            this.generation,
            this.position
        )
    }
}
