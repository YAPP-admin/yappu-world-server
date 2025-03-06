package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.AdminActivityUnitUpdateAppRequestDto
import co.yappuworld.user.domain.vo.Position
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class AdminActivityUnitUpdateApiRequestDto(
    @Schema(description = "ID", nullable = false)
    val id: UUID?,
    @Schema(description = "기수")
    @field:NotEmpty(message = "기수는 필수로 입력해야 합니다.")
    @field:Min(value = 1L)
    val generation: Int,
    @Schema(description = "직군")
    @field:NotEmpty(message = "직군은 필수로 입력해야 합니다.")
    val position: Position
) {
    fun toAppRequest(): AdminActivityUnitUpdateAppRequestDto =
        AdminActivityUnitUpdateAppRequestDto(id, generation, position)
}
