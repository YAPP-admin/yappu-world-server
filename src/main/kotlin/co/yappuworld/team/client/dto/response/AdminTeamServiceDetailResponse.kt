package co.yappuworld.team.client.dto.response

import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminTeamServiceDetailResponse(
    @field:Schema(description = "서비스 ID")
    val serviceId: UUID,
    @field:Schema(description = "기수")
    val generation: Int,
    @field:Schema(description = "팀 이름")
    val teamName: String,
    @field:Schema(description = "서비스명")
    val serviceName: String?,
    @field:Schema(description = "앱 플랫폼 여부")
    val hasApp: Boolean,
    @field:Schema(description = "웹 플랫폼 여부")
    val hasWeb: Boolean,
    @field:Schema(description = "구글 플레이 링크")
    val googlePlayLink: String?,
    @field:Schema(description = "앱스토어 링크")
    val appStoreLink: String?,
    @field:Schema(description = "웹 링크")
    val webLink: String?,
    @field:Schema(description = "서비스 소개")
    val summary: String?,
    @field:Schema(description = "상세설명")
    val description: String?,
    @field:Schema(description = "운영 상태")
    val isOperating: Boolean
) {
    companion object {
        fun from(entity: TeamServiceEntity): AdminTeamServiceDetailResponse =
            AdminTeamServiceDetailResponse(
                serviceId = entity.id,
                generation = entity.team.generation,
                teamName = entity.team.name,
                serviceName = entity.name,
                hasApp = entity.hasApp,
                hasWeb = entity.hasWeb,
                googlePlayLink = entity.serviceLinks?.googlePlay,
                appStoreLink = entity.serviceLinks?.appStore,
                webLink = entity.serviceLinks?.web,
                summary = entity.summary,
                description = entity.description,
                isOperating = entity.isOperating
            )
    }
}
