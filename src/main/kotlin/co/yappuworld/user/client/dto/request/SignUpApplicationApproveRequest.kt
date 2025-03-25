package co.yappuworld.user.client.dto.request

import co.yappuworld.user.domain.vo.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class SignUpApplicationApproveRequest(
    @Schema(description = "회원가입 승인할 신청서 ID 목록")
    val applicationIds: List<UUID>,
    @Schema(description = "회원 역할")
    val role: UserRole
)
