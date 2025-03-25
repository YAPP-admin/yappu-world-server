package co.yappuworld.user.presentation.dto.response

import co.yappuworld.operation.client.dto.response.PositionResponse
import co.yappuworld.user.application.dto.response.AdminSignUpApplicationActivityUnitAppResponseDto
import co.yappuworld.user.domain.model.ActivityUnitParam
import io.swagger.v3.oas.annotations.media.Schema

data class AdminSignUpApplicationActivityUnitApiResponseDto(
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "직군")
    val position: PositionResponse
) {

    constructor(response: AdminSignUpApplicationActivityUnitAppResponseDto) : this(
        generation = response.generation,
        position = PositionResponse(response.position)
    )

    constructor(param: ActivityUnitParam) : this(
        generation = param.generation,
        position = PositionResponse(param.position)
    )
}
