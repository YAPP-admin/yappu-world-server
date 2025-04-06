package co.yappuworld.operation.client.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class ActiveGenerationResponse(
    @Schema(description = "활동 여부")
    val isActive: Boolean,
    @Schema(description = "활동 기수", nullable = true)
    val generation: Int?
)
