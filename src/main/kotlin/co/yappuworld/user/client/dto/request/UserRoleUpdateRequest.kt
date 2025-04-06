package co.yappuworld.user.client.dto.request

import co.yappuworld.user.domain.vo.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class UserRoleUpdateRequest(
    @Schema(description = "대상 유저 ID", nullable = false)
    @field:NotNull(message = "대상 유저 ID는 필수입니다.")
    val userId: UUID,
    @Schema(description = "변경할 권한", nullable = false)
    @field:NotNull(message = "변경할 권한은 필수입니다.")
    val role: UserRole
)
