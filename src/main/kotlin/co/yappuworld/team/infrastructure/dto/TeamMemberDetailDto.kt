package co.yappuworld.team.infrastructure.dto

import co.yappuworld.user.domain.vo.Position
import java.util.UUID

data class TeamMemberDetailDto(
    val userId: UUID,
    val activityUnitId: UUID,
    val generation: Int,
    val position: Position,
    val userName: String
)
