package co.yappuworld.operation.client.dto.response

import co.yappuworld.operation.domain.GenerationEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class GenerationsResponse(
    val generations: List<GenerationResponse>
)

data class GenerationResponse(
    @field:Schema(description = "기수")
    val generation: Int,
    @field:Schema(description = "시작일", nullable = true)
    val startDate: LocalDate?,
    @field:Schema(description = "종료일", nullable = true)
    val endDate: LocalDate?,
    @field:Schema(description = "현재 활동 중인지")
    val isActive: Boolean
) {

    companion object {
        fun from(generation: GenerationEntity): GenerationResponse =
            GenerationResponse(
                generation = generation.value,
                startDate = generation.startDate,
                endDate = generation.endDate,
                isActive = generation.isActive
            )
    }
}
