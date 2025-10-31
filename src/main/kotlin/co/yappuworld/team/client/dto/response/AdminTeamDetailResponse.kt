package co.yappuworld.team.client.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminTeamDetailResponse(
    @Schema(description = "팀 ID")
    val id: UUID,
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "팀 이름")
    val name: String,
    @Schema(description = "서비스 정보")
    val service: AdminServiceResponse?,
    @Schema(description = "팀원 목록")
    val members: List<AdminTeamMemberResponse>
)

@Schema(description = "서비스 정보")
data class AdminServiceResponse(
    @Schema(description = "서비스 ID")
    val id: UUID,
    @Schema(description = "서비스 이름")
    val name: String?,
    @Schema(description = "앱 보유 여부")
    val hasApp: Boolean,
    @Schema(description = "웹 보유 여부")
    val hasWeb: Boolean,
    @Schema(description = "서비스 링크 정보")
    val serviceLinks: AdminServiceLinksResponse?
)

@Schema(description = "서비스 링크 정보")
data class AdminServiceLinksResponse(
    @Schema(description = "구글 스토어 링크")
    val googleStore: String?,
    @Schema(description = "앱스토어 링크")
    val appStore: String?,
    @Schema(description = "웹 사이트 링크")
    val web: String?
)

@Schema(description = "팀원 정보")
data class AdminTeamMemberResponse(
    @Schema(description = "활동 내역 ID")
    val activityUnitId: UUID
)
