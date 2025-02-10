package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.User
import co.yappuworld.user.domain.vo.UserRole
import java.util.UUID

data class UserProfileAppResponseDto(
    val id: UUID,
    val name: String,
    val role: UserRole,
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
                user.role,
                activityUnits.map { ActivityUnitAppResponseDto.of(it) }
            )
        }
    }
}
