package co.yappuworld.team.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminTeamUpdateRequest(
    @Schema(description = "팀 ID")
    val id: UUID,
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "팀 이름")
    val name: String,
    @Schema(description = "서비스 정보")
    val service: AdminServiceUpdateRequest?,
    @Schema(description = "팀원 활동 이력 ID 목록")
    val activityUnitIds: List<UUID>
)

data class AdminServiceUpdateRequest(
    @Schema(description = "서비스 이름")
    val name: String,
    @Schema(description = "앱 플랫폼 여부")
    val hasApp: Boolean,
    @Schema(description = "웹 플랫폼 여부")
    val hasWeb: Boolean,
    @Schema(description = "서비스 링크 정보")
    val serviceLinks: AdminServiceLinksUpdateRequest?
)

data class AdminServiceLinksUpdateRequest(
    @Schema(description = "구글 스토어 링크")
    val googleStore: String?,
    @Schema(description = "앱스토어 링크")
    val appStore: String?,
    @Schema(description = "웹 사이트 링크")
    val web: String?
)
