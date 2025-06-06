package co.yappuworld.operation.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class AdminGenerationDeleteRequest(
    @Schema(description = "삭제할 기수 목록")
    val generations: List<Int>
)
