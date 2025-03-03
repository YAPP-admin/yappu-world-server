package co.yappuworld.user.presentation.dto.response

import co.yappuworld.operation.presentation.dto.response.PositionApiResponseDto
import co.yappuworld.user.application.dto.response.ActivityUnitAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class ActivityUnitApiResponseDto(
    @Schema(description = "ID")
    val id: UUID,
    @Schema(description = "기수", minContains = 1)
    val generation: Int,
    @Schema(description = "직군")
    val position: PositionApiResponseDto
) {

    constructor(response: ActivityUnitAppResponseDto) : this(
        id = response.id,
        generation = response.generation,
        position = PositionApiResponseDto(response.position)
    )
}
