package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.User
import java.util.UUID

data class UserProfileAppResponseDto(
    val id: UUID,
    val name: String,
    val role: String,
    val activityUnits: List<ActivityUnitAppResponseDto>
) {

    companion object {
        fun of(
            user: User,
            activityUnits: List<ActivityUnit>
        ): UserProfileAppResponseDto {
            return UserProfileAppResponseDto(
                user.id,
                user.name,
                user.role.label,
                activityUnits.map { ActivityUnitAppResponseDto.of(it) }
            )
        }
    }
}
