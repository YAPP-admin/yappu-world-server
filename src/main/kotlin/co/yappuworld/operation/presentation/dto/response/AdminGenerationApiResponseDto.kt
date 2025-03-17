package co.yappuworld.operation.presentation.dto.response

import co.yappuworld.operation.application.dto.response.AdminGenerationAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class AdminGenerationApiResponseDto(
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "시작일", nullable = true)
    val startDate: LocalDate?,
    @Schema(description = "종료일", nullable = true)
    val endDate: LocalDate?,
    @Schema(description = "현재 활동 중인지")
    val isActive: Boolean
) {

    constructor(response: AdminGenerationAppResponseDto) : this(
        generation = response.generation,
        startDate = response.startDate,
        endDate = response.endDate,
        isActive = response.isActive
    )
}
