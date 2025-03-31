package co.yappuworld.operation.client.dto.request

import co.yappuworld.operation.domain.GenerationEntity
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class AdminGenerationRegisterRequest(
    @Schema(description = "기수")
    @field:Min(value = 1L, message = "기수는 1 이상이어야 합니다.")
    val generation: Int,
    @Schema(description = "시작일")
    @field:NotNull(message = "시작일은 필수 값입니다.")
    val startDate: LocalDate,
    @Schema(description = "종료일")
    @field:NotNull(message = "종료일은 필수 값입니다.")
    val endDate: LocalDate,
    @Schema(description = "활동 중 여부")
    @field:NotNull(message = "활동 중 여부는 필수 값입니다.")
    val isActive: Boolean
) {

    fun toDomain(): GenerationEntity =
        GenerationEntity(
            value = generation,
            startDate = startDate,
            endDate = endDate
        )
}
