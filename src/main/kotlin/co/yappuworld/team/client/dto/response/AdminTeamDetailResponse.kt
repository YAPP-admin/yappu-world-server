package co.yappuworld.team.client.dto.response

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
    val members: List<AdminTeamMemberResponse?>?
)

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
)

data class AdminTeamMemberResponse(
    @field:Schema(description = "활동 이력 ID")
    val activityUnitId: UUID,
    @field:Schema(description = "이름")
    val name: String,
    @field:Schema(description = "직군")
    val position: String
)
