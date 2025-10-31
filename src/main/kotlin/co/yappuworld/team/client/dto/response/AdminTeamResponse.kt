package co.yappuworld.team.client.dto.response

import co.yappuworld.team.infrastructure.entity.TeamEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminTeamResponse(
    @Schema(description = "팀 ID")
    val teamId: UUID,
    @Schema(description = "기수", example = "23")
    val generation: Int,
    @Schema(description = "팀 이름", example = "야뿌")
    val name: String,
    @Schema(description = "서비스명", example = "YAPP World")
    val serviceName: String,
    @Schema(description = "앱 플랫폼 여부", example = "true")
    val hasApp: Boolean,
    @Schema(description = "웹 플랫폼 여부", example = "false")
    val hasWeb: Boolean,
    @Schema(description = "플랫폼 링크")
    val platformLinks: PlatformLinksResponse?
) {
    constructor(team: TeamEntity) : this(
        teamId = team.id,
        generation = team.generation,
        name = team.name,
        serviceName = team.service?.name ?: "",
        hasApp = team.service?.hasApp ?: false,
        hasWeb = team.service?.hasWeb ?: false,
        platformLinks = PlatformLinksResponse.from(team.service?.serviceLinks)
    )
}
