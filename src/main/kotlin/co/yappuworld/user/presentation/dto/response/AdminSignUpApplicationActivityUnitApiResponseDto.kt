package co.yappuworld.user.presentation.dto.response

import co.yappuworld.operation.presentation.dto.response.PositionApiResponseDto
import co.yappuworld.user.application.dto.response.AdminSignUpApplicationActivityUnitAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema

data class AdminSignUpApplicationActivityUnitApiResponseDto(
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "직군")
    val position: PositionApiResponseDto
) {

    constructor(response: AdminSignUpApplicationActivityUnitAppResponseDto) : this(
        generation = response.generation,
        position = PositionApiResponseDto(response.position)
    )
}
