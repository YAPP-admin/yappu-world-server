package co.yappuworld.user.application.dto.request

import co.yappuworld.user.domain.model.ActivityUnitParam
import co.yappuworld.user.presentation.vo.PositionApiType

data class ActivityUnitAppRequestDto(
    val generation: Int,
    val position: PositionApiType
) {
    fun toActivityUnitParam(): ActivityUnitParam {
        return ActivityUnitParam(
            this.generation,
            this.position.toDomainType()
        )
    }
}
