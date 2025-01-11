package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.ActivityUnitAppRequestDto
import co.yappuworld.user.presentation.vo.PositionApiType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty

data class ActivityUnitApiRequestDto(
    @NotEmpty
    @Schema(description = "기수")
    val generation: Int,
    @NotEmpty
    @Schema(description = "직군")
    val position: PositionApiType
) {
    fun toAppRequest(): ActivityUnitAppRequestDto {
        return ActivityUnitAppRequestDto(generation, position)
    }
}
