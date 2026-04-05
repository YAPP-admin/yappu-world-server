package co.yappuworld.team.client.dto.request

import co.yappuworld.team.infrastructure.entity.TeamEntity
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.util.UUID

data class AdminTeamCreateRequest(
    @field:Schema(description = "기수")
    @field:Positive
    val generation: Int,
    @field:Schema(description = "팀 이름")
    @field:NotBlank
    val name: String,
    @field:Schema(description = "앱 플랫폼 여부")
    val hasApp: Boolean,
    @field:Schema(description = "웹 플랫폼 여부")
    val hasWeb: Boolean,
    @field:Schema(description = "팀원 활동 이력 ID 목록")
    val activityUnitIds: List<UUID>? = null
) {
    fun toTeam(): TeamEntity =
        TeamEntity(
            generation = generation,
            name = name,
            hasApp = hasApp,
            hasWeb = hasWeb
        )
}
