package co.yappuworld.team.client.dto.response

import co.yappuworld.team.infrastructure.dto.TeamMemberDetailDto
import co.yappuworld.team.infrastructure.dto.TeamWithServiceDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminTeamDetailResponse(
    @field:Schema(description = "팀 ID")
    val id: UUID,
    @field:Schema(description = "기수")
    val generation: Int,
    @field:Schema(description = "팀 이름")
    val name: String,
    @field:Schema(description = "서비스 정보")
    val service: AdminTeamServiceResponse?,
    @field:Schema(description = "팀원 목록")
    val members: List<AdminTeamMemberResponse>
) {
    companion object {
        fun of(
            teamWithService: TeamWithServiceDto,
            members: List<TeamMemberDetailDto>
        ): AdminTeamDetailResponse =
            AdminTeamDetailResponse(
                id = teamWithService.teamId,
                generation = teamWithService.generation,
                name = teamWithService.teamName,
                service = AdminTeamServiceResponse.from(teamWithService),
                members = members.map { AdminTeamMemberResponse.from(it) }
            )
    }
}

data class AdminTeamServiceResponse(
    @field:Schema(description = "서비스 ID")
    val id: UUID,
    @field:Schema(description = "서비스 이름")
    val name: String?,
    @field:Schema(description = "앱 플랫폼 여부")
    val hasApp: Boolean,
    @field:Schema(description = "웹 플랫폼 여부")
    val hasWeb: Boolean,
    @field:Schema(description = "구글 플레이 스토어 링크")
    val googlePlayLink: String?,
    @field:Schema(description = "앱스토어 링크")
    val appStoreLink: String?,
    @field:Schema(description = "웹 사이트 링크")
    val webLink: String?
) {
    companion object {
        fun from(dto: TeamWithServiceDto): AdminTeamServiceResponse? =
            dto.serviceId?.let {
                AdminTeamServiceResponse(
                    id = it,
                    name = dto.serviceName,
                    hasApp = dto.hasApp ?: false,
                    hasWeb = dto.hasWeb ?: false,
                    googlePlayLink = dto.serviceLinks?.googlePlay,
                    appStoreLink = dto.serviceLinks?.appStore,
                    webLink = dto.serviceLinks?.web
                )
            }
    }
}

data class AdminTeamMemberResponse(
    @field:Schema(description = "활동 이력 ID")
    val activityUnitId: UUID,
    @field:Schema(description = "이름")
    val name: String,
    @field:Schema(description = "직군")
    val position: String
) {
    companion object {
        fun from(dto: TeamMemberDetailDto): AdminTeamMemberResponse =
            AdminTeamMemberResponse(
                activityUnitId = dto.activityUnitId,
                name = dto.userName,
                position = dto.position.label
            )
    }
}
