package co.yappuworld.operation.presentation.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class ActiveGenerationApiResponseDto(
    @Schema(description = "활동 여부")
    val isActive: Boolean,
    @Schema(description = "활동 기수", nullable = true)
    val generation: Int?
)
