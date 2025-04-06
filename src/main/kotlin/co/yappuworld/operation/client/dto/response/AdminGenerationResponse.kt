package co.yappuworld.operation.client.dto.response

import co.yappuworld.operation.domain.GenerationEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class AdminGenerationResponse(
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "시작일", nullable = true)
    val startDate: LocalDate?,
    @Schema(description = "종료일", nullable = true)
    val endDate: LocalDate?,
    @Schema(description = "현재 활동 중인지")
    val isActive: Boolean
) {

    constructor(generation: GenerationEntity) : this(
        generation = generation.value,
        startDate = generation.startDate,
        endDate = generation.endDate,
        isActive = generation.isActive
    )
}
