package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.model.ActivityUnit

data class ActivityUnitAppResponseDto(
    val generation: Int,
    val position: Position
) {

    companion object {
        fun of(activityUnit: ActivityUnit): ActivityUnitAppResponseDto {
            return ActivityUnitAppResponseDto(
                activityUnit.generation,
                activityUnit.position
            )
        }
    }
}
