package co.yappuworld.schedule.presentation.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminSessionDeleteApiRequestDto(
    @Schema(description = "삭제할 세션 ID")
    val id: UUID
)
