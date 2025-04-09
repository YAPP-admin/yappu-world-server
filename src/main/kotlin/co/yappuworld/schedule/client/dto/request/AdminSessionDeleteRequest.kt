package co.yappuworld.schedule.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class AdminSessionDeleteRequest(
    @Schema(description = "삭제할 세션 ID 목록")
    @field:NotEmpty(message = "삭제할 ID는 하나 이상이어야 합니다.")
    val ids: List<UUID>
)
