package co.yappuworld.team.client.dto.request

import co.yappuworld.team.infrastructure.entity.ServiceLinks
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.URL
import java.util.UUID

data class AdminTeamServiceCreateRequest(
    @field:Schema(description = "팀 ID")
    val teamId: UUID,
    @field:Schema(description = "서비스명")
    val name: String?,
    @field:Schema(description = "앱 플랫폼 여부")
    val hasApp: Boolean,
    @field:Schema(description = "웹 플랫폼 여부")
    val hasWeb: Boolean,
    @field:Schema(description = "구글 플레이 링크")
    @field:URL(message = "유효한 URL 형식이어야 합니다")
    val googlePlayLink: String?,
    @field:Schema(description = "앱스토어 링크")
    @field:URL(message = "유효한 URL 형식이어야 합니다")
    val appStoreLink: String?,
    @field:Schema(description = "웹 링크")
    @field:URL(message = "유효한 URL 형식이어야 합니다")
    val webLink: String?,
    @field:Schema(description = "서비스 소개")
    @field:Length(max = 500, message = "소개 글은 500자 이하여야 합니다.")
    val summary: String?,
    @field:Schema(description = "상세설명")
    val description: String?,
    @field:Schema(description = "운영 상태")
    val isOperating: Boolean = false
) {
    fun toService(team: TeamEntity): TeamServiceEntity =
        TeamServiceEntity(
            team = team,
            name = name,
            hasApp = hasApp,
            hasWeb = hasWeb,
            serviceLinks = ServiceLinks(
                googlePlay = googlePlayLink,
                appStore = appStoreLink,
                web = webLink
            ),
            summary = summary,
            description = description,
            isOperating = isOperating
        )
}
