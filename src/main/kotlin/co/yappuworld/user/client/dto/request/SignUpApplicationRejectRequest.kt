package co.yappuworld.user.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class SignUpApplicationRejectRequest(
    @Schema(description = "회원가입 거절할 신청서 ID 목록")
    val applicationIds: List<UUID>,
    @Schema(description = "거절 사유")
    val reason: String
)
