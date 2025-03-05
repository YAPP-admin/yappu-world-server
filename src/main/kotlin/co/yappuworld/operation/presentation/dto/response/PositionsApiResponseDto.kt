package co.yappuworld.operation.presentation.dto.response

import co.yappuworld.user.domain.vo.Position
import io.swagger.v3.oas.annotations.media.Schema

data class PositionApiResponseDto(
    @Schema(
        description = "요청 시 사용해야 하는 값",
        allowableValues = ["PM", "DESIGN", "WEB", "ANDROID", "IOS", "FLUTTER", "SERVER", "STAFF"]
    )
    val name: String,
    @Schema(
        description = "화면에 노출되는 값",
        allowableValues = ["PM", "Design", "Web", "Android", "iOS", "Flutter", "Server", "운영진"]
    )
    val label: String
) {

    constructor(position: Position) : this(
        name = position.name,
        label = position.label
    )
}

data class PositionsApiResponseDto(
    val positions: List<PositionApiResponseDto>
)
