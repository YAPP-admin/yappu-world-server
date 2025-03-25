package co.yappuworld.user.presentation.dto.response

import co.yappuworld.operation.client.dto.response.PositionResponse
import co.yappuworld.user.application.dto.response.ActivityUnitAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema

data class ActivityUnitApiResponseDto(
    @Schema(description = "기수", minContains = 1)
    val generation: Int,
    @Schema(description = "직군")
    val position: PositionResponse
) {

    constructor(response: ActivityUnitAppResponseDto) : this(
        generation = response.generation,
        position = PositionResponse(response.position)
    )
}
