package co.yappuworld.schedule.infrastructure.dto

import co.yappuworld.user.domain.vo.Position
import java.util.UUID

data class SessionAttendeeDto(
    val sessionId: UUID,
    val userId: UUID,
    val generation: Int,
    val position: Position,
    val name: String
)
