package co.yappuworld.team.client.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "회원 팀 정보")
data class UserTeamResponse(
    @Schema(description = "팀 ID")
    val id: UUID,
    @Schema(description = "팀 이름")
    val name: String
)
