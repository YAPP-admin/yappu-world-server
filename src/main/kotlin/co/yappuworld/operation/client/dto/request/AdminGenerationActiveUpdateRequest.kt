package co.yappuworld.operation.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class AdminGenerationActiveUpdateRequest(
    @Schema(description = "변경하려는 기수")
    val generation: Int,
    @Schema(description = "목표 상태")
    val targetActive: Boolean
)
