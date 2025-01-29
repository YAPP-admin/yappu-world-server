package co.yappuworld.operation.presentation.dto.response

import co.yappuworld.user.domain.vo.Position
import io.swagger.v3.oas.annotations.media.Schema

data class PositionApiResponseDto(
    @Schema(description = "요청 시 사용해야 하는 값")
    val name: String,
    @Schema(description = "화면에 노출되는 값")
    val label: String
) {

    companion object {
        fun of(position: Position): PositionApiResponseDto {
            return PositionApiResponseDto(
                position.name,
                position.label
            )
        }
    }
}

data class PositionsApiResponseDto(
    val positions: List<PositionApiResponseDto>
)
