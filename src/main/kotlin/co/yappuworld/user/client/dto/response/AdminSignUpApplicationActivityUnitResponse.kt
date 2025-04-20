package co.yappuworld.user.client.dto.response

import co.yappuworld.operation.client.dto.response.PositionResponse
import co.yappuworld.user.domain.entity.ActivityUnitParam
import io.swagger.v3.oas.annotations.media.Schema

data class AdminSignUpApplicationActivityUnitResponse(
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "직군")
    val position: PositionResponse
) {

    constructor(param: ActivityUnitParam) : this(
        generation = param.generation,
        position = PositionResponse(param.position)
    )
}
