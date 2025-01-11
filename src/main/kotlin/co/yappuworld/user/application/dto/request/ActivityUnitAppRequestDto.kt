package co.yappuworld.user.application.dto.request

import co.yappuworld.user.domain.ActivityUnit
import co.yappuworld.user.presentation.vo.PositionApiType

data class ActivityUnitAppRequestDto(
    val generation: Int,
    val position: PositionApiType
) {
    fun toDomain(): ActivityUnit {
        return ActivityUnit(
            this.generation,
            this.position.toDomainType()
        )
    }
}
