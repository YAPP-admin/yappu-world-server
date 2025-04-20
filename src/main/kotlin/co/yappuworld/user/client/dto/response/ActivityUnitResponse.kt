package co.yappuworld.user.client.dto.response

import co.yappuworld.operation.client.dto.response.PositionResponse
import co.yappuworld.user.domain.entity.ActivityUnitEntity
import io.swagger.v3.oas.annotations.media.Schema

data class ActivityUnitResponse(
    @Schema(description = "기수", minContains = 1)
    val generation: Int,
    @Schema(description = "직군")
    val position: PositionResponse
) {

    constructor(response: ActivityUnitEntity) : this(
        generation = response.generation,
        position = PositionResponse(response.position)
    )
}
