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
    @field:Schema(description = "앱 플랫폼 여부")
    val hasApp: Boolean,
    @field:Schema(description = "웹 플랫폼 여부")
    val hasWeb: Boolean,
    @field:Schema(description = "서비스 ID")
    val serviceId: UUID?,
    @field:Schema(description = "서비스명")
    val serviceName: String?,
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
                hasApp = teamWithService.hasApp,
                hasWeb = teamWithService.hasWeb,
                serviceId = teamWithService.serviceId,
                serviceName = teamWithService.serviceName,
                members = members.map { AdminTeamMemberResponse.from(it) }
            )
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
