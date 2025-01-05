package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.domain.ActivityUnit
import co.yappuworld.user.presentation.vo.PositionApiType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty

data class ActivityUnitRequestDto(
    @NotEmpty
    @Schema(description = "기수")
    val generation: Int,
    @NotEmpty
    @Schema(description = "직군")
    val position: PositionApiType
) {
    fun toDomain(): ActivityUnit {
        return ActivityUnit(
            this.generation,
            this.position.toDomainType()
        )
    }
}
