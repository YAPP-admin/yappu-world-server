package co.yappuworld.team.client.dto.request

import co.yappuworld.team.infrastructure.entity.ServiceLinks
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.URL
import java.util.UUID

data class AdminTeamCreateRequest(
    @field:Schema(description = "기수")
    @field:Positive
    val generation: Int,
    @field:Schema(description = "팀 이름")
    @field:NotBlank
    val name: String,
    @field:Schema(description = "서비스 이름")
    val serviceName: String?,
    @field:Schema(description = "앱 플랫폼 여부")
    val hasApp: Boolean,
    @field:Schema(description = "웹 플랫폼 여부")
    val hasWeb: Boolean,
    @field:Schema(description = "구글 플레이 스토어 링크")
    @field:URL(message = "유효한 URL 형식이어야 합니다")
    val googlePlayLink: String?,
    @field:Schema(description = "앱스토어 링크")
    @field:URL(message = "유효한 URL 형식이어야 합니다")
    val appStoreLink: String?,
    @field:Schema(description = "웹 사이트 링크")
    @field:URL(message = "유효한 URL 형식이어야 합니다")
    val webLink: String?,
    @field:Schema(description = "팀원 활동 이력 ID 목록")
    val activityUnitIds: List<UUID>? = null
) {
    fun toTeam(): TeamEntity =
        TeamEntity(
            generation = generation,
            name = name
        )

    fun toService(team: TeamEntity): TeamServiceEntity =
        TeamServiceEntity(
            team = team,
            name = serviceName,
            hasApp = hasApp,
            hasWeb = hasWeb,
            serviceLinks = ServiceLinks(
                appStore = appStoreLink,
                googlePlay = googlePlayLink,
                web = webLink
            )
        )
}
