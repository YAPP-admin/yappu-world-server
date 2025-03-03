package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.domain.vo.UserRole
import io.swagger.v3.oas.annotations.media.Schema

data class UserRoleApiResponseDto(
    @Schema(
        description = "요청 시 사용해야 하는 값",
        allowableValues = ["ADMIN", "STAFF", "ALUMNI", "GRADUATE", "ACTIVE"]
    )
    val name: String,
    @Schema(
        description = "화면에 노출해야 하는 값",
        allowableValues = ["관리자", "운영진", "정회원", "수료회원", "활동회원"]
    )
    val label: String
) {

    constructor(role: UserRole) : this(
        name = role.name,
        label = role.label
    )
}
