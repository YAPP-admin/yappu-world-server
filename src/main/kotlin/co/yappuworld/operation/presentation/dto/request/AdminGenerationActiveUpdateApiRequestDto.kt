package co.yappuworld.operation.presentation.dto.request

import co.yappuworld.operation.application.dto.request.AdminGenerationActiveUpdateAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema

data class AdminGenerationActiveUpdateApiRequestDto(
    @Schema(description = "변경하려는 기수")
    val generation: Int,
    @Schema(description = "목표 상태")
    val targetActive: Boolean
) {

    fun toAppRequest(): AdminGenerationActiveUpdateAppRequestDto =
        AdminGenerationActiveUpdateAppRequestDto(
            generation = generation,
            targetActive = targetActive
        )
}
