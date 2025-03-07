package co.yappuworld.operation.presentation.dto.response

import co.yappuworld.operation.application.dto.response.AdminGenerationActiveUpdateAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema

data class AdminGenerationActiveUpdateApiResponseDto(
    @Schema(description = "활성화 된 기수", nullable = true)
    val activatedGeneration: Int? = null,
    @Schema(description = "비활성화 된 기수", nullable = true)
    val deactivatedGeneration: Int? = null
) {

    constructor(response: AdminGenerationActiveUpdateAppResponseDto) : this(
        activatedGeneration = response.activatedGeneration,
        deactivatedGeneration = response.deactivatedGeneration
    )
}
