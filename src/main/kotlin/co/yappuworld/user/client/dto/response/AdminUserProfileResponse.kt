package co.yappuworld.user.client.dto.response

import co.yappuworld.user.domain.model.UserActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminUserProfileResponse(
    @Schema(description = "식별자")
    val id: UUID,
    @Schema(description = "이름")
    val name: String,
    @Schema(description = "역할 (권한)", allowableValues = ["관리자", "운영진", "정회원", "수료회원", "활동회원"])
    val role: String,
    @Schema(
        description = "최근 활동 기수의 직군",
        allowableValues = ["PM", "Design", "Web", "Android", "iOS", "Flutter", "Server", "운영진"]
    )
    val position: String
) {
    constructor(userLastActivityUnit: UserActivityUnit) : this(
        id = userLastActivityUnit.userId,
        name = userLastActivityUnit.name,
        role = userLastActivityUnit.role.label,
        position = userLastActivityUnit.position.label
    )
}
