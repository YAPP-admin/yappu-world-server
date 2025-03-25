package co.yappuworld.user.client.dto.request

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.vo.Position
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class AdminActivityUnitUpdateRequest(
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

    fun toActivityUnit(userId: UUID): ActivityUnit =
        ActivityUnit(
            generation = generation,
            position = position,
            userId = userId
        )
}
