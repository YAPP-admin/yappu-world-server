package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.ActivityUnitAppResponseDto
import co.yappuworld.user.domain.Position
import io.swagger.v3.oas.annotations.media.Schema

data class ActivityUnitApiResponseDto(
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "직군")
    val position: Position
) {

    companion object {
        fun of(response: ActivityUnitAppResponseDto): ActivityUnitApiResponseDto {
            return ActivityUnitApiResponseDto(
                response.generation,
                response.position
            )
        }
    }
}
