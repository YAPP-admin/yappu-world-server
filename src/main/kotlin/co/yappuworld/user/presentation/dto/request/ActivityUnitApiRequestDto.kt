package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.ActivityUnitAppRequestDto
import co.yappuworld.user.presentation.vo.PositionApiType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty

data class ActivityUnitApiRequestDto(
    @Schema(description = "기수")
    @field:NotEmpty(message = "기수는 필수로 입력해야 합니다.")
    val generation: Int,
    @Schema(description = "직군")
    @field:NotEmpty(message = "직군은 필수로 입력해야 합니다.")
    val position: PositionApiType
) {
    fun toAppRequest(): ActivityUnitAppRequestDto {
        return ActivityUnitAppRequestDto(generation, position)
    }
}
