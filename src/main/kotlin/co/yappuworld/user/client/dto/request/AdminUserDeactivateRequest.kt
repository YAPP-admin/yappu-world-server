package co.yappuworld.user.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminUserDeactivateRequest(
    @param:Schema(description = "탈퇴 시킬 유저 ID")
    val userId: UUID
)
