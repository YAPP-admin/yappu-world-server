package co.yappuworld.user.client.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminActivityUnitResponse(
    @field:Schema(description = "ID")
    val id: UUID,
    @Schema(description = "기수", minContains = 1)
    val generation: Int,
    @Schema(description = "직군")
    val position: String,
    @Schema(description = "활동 중인지 여부")
    val isActive: Boolean
)
