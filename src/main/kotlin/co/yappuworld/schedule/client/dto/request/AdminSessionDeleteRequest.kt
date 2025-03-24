package co.yappuworld.schedule.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminSessionDeleteRequest(
    @Schema(description = "삭제할 세션 ID")
    val id: UUID
)
