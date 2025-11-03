package co.yappuworld.team.client.dto.response

import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminTeamResponse(
    @field:Schema(description = "팀 ID")
    val teamId: UUID,
    @field:Schema(description = "기수", example = "23")
    val generation: Int,
    @field:Schema(description = "팀 이름", example = "야뿌")
    val name: String,
    @field:Schema(description = "서비스명", example = "YAPP World")
    val serviceName: String?,
    @field:Schema(description = "앱 플랫폼 여부", example = "true")
    val hasApp: Boolean,
    @field:Schema(description = "웹 플랫폼 여부", example = "false")
    val hasWeb: Boolean,
    @field:Schema(description = "플랫폼 링크")
    val platformLinks: PlatformLinksResponse?
) {
    companion object {
        fun from(
            team: TeamEntity,
            service: TeamServiceEntity? = null
        ): AdminTeamResponse =
            AdminTeamResponse(
                teamId = team.id,
                generation = team.generation,
                name = team.name,
                serviceName = service?.name,
                hasApp = service?.hasApp ?: false,
                hasWeb = service?.hasWeb ?: false,
                platformLinks = service?.serviceLinks?.let {
                    PlatformLinksResponse.from(it)
                }
            )
    }
}
