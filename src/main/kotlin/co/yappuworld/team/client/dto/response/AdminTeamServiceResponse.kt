package co.yappuworld.team.client.dto.response

import co.yappuworld.team.infrastructure.dto.TeamServiceSummary
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminTeamServiceResponse(
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
    @field:Schema(description = "서비스 소개")
    val summary: String?,
    @field:Schema(description = "운영 상태")
    val isOperating: Boolean
) {
    companion object {
        fun from(summary: TeamServiceSummary): AdminTeamServiceResponse =
            AdminTeamServiceResponse(
                serviceId = summary.serviceId,
                generation = summary.generation,
                teamName = summary.teamName,
                serviceName = summary.serviceName,
                hasApp = summary.hasApp,
                hasWeb = summary.hasWeb,
                summary = summary.summary,
                isOperating = summary.isOperating
            )
    }
}
