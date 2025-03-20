package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.ActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminActivityUnitResponse(
    @Schema(description = "ID")
    val id: UUID,
    @Schema(description = "기수", minContains = 1)
    val generation: Int,
    @Schema(description = "직군")
    val position: String,
    @Schema(description = "활동 중인지 여부")
    val isActive: Boolean
) {

    constructor(response: ActivityUnitAppResponseDto) : this(
        id = response.id,
        generation = response.generation,
        position = response.position.label,
        // TODO : 수정 필요
        isActive = false
    )

    constructor(activityUnit: ActivityUnit, activeGeneration: Int?) : this(
        id = activityUnit.id,
        generation = activityUnit.generation,
        position = activityUnit.position.label,
        isActive = activityUnit.generation == activeGeneration
    )
}
