package co.yappuworld.team.client.dto.response

import co.yappuworld.team.infrastructure.dto.TeamMemberDetailDto
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class HistoricalServiceDetailResponse(
    @field:Schema(description = "서비스 ID")
    val serviceId: UUID,
    @field:Schema(description = "기수")
    val generation: Int,
    @field:Schema(description = "서비스명")
    val serviceName: String?,
    @field:Schema(description = "앱 플랫폼 여부")
    val hasApp: Boolean,
    @field:Schema(description = "웹 플랫폼 여부")
    val hasWeb: Boolean,
    @field:Schema(description = "한 줄 소개")
    val summary: String?,
    @field:Schema(description = "상세 설명")
    val description: String?,
    @field:Schema(description = "썸네일 이미지 URL")
    val thumbnailImageUrl: String?,
    @field:Schema(description = "구글 플레이 링크")
    val googlePlayLink: String?,
    @field:Schema(description = "앱스토어 링크")
    val appStoreLink: String?,
    @field:Schema(description = "웹 링크")
    val webLink: String?,
    @field:Schema(description = "팀원 목록")
    val members: List<HistoricalServiceMemberResponse>
) {
    companion object {
        fun of(
            service: TeamServiceEntity,
            members: List<TeamMemberDetailDto>,
            thumbnailImageUrl: String?
        ): HistoricalServiceDetailResponse =
            HistoricalServiceDetailResponse(
                serviceId = service.id,
                generation = service.team.generation,
                serviceName = service.name,
                hasApp = service.hasApp,
                hasWeb = service.hasWeb,
                summary = service.summary,
                description = service.description,
                thumbnailImageUrl = thumbnailImageUrl,
                googlePlayLink = service.serviceLinks?.googlePlay,
                appStoreLink = service.serviceLinks?.appStore,
                webLink = service.serviceLinks?.web,
                members = members
                    .sortedWith(compareBy({ it.position.order }, { it.userName }))
                    .map(HistoricalServiceMemberResponse::from)
            )
    }
}

data class HistoricalServiceMemberResponse(
    @field:Schema(description = "활동 이력 ID")
    val activityUnitId: UUID,
    @field:Schema(description = "이름")
    val name: String,
    @field:Schema(description = "직군명")
    val position: String
) {
    companion object {
        fun from(dto: TeamMemberDetailDto): HistoricalServiceMemberResponse =
            HistoricalServiceMemberResponse(
                activityUnitId = dto.activityUnitId,
                name = dto.userName,
                position = dto.position.label
            )
    }
}
