package co.yappuworld.team.infrastructure.dto

import java.util.UUID

data class TeamMemberDetailDto(
    val activityUnitId: UUID,
    val generation: Int,
    val position: String,
    val userName: String
)
