package co.yappuworld.user.client.dto.response

import co.yappuworld.user.domain.model.ActivityUnitEntity
import co.yappuworld.user.domain.model.UserEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class UserProfileResponse(
    @Schema(description = "유저 식별자")
    val id: UUID,
    @Schema(description = "실명")
    val name: String,
    @Schema(
        description = "유저 유형",
        allowableValues = ["관리자", "운영진", "정회원", "수료회원", "활동회원"]
    )
    val role: String,
    @Schema(description = "기수, 직군 목록")
    val activityUnits: List<ActivityUnitResponse>
) {

    constructor(
        user: UserEntity,
        activityUnits: List<ActivityUnitEntity>
    ) : this(
        user.id,
        user.name,
        user.role.label,
        activityUnits
            .map { ActivityUnitResponse(it) }
            .sortedByDescending { it.generation }
    )
}
