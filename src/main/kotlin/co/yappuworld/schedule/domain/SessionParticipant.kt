package co.yappuworld.schedule.domain

import co.yappuworld.user.domain.vo.Position
import java.util.UUID

class SessionParticipant(
    val activityUnitId: UUID,
    val generation: Int,
    val position: Position,
    val userId: UUID,
    val userName: String,
    val sessionId: UUID,
    val sessionName: String
)
