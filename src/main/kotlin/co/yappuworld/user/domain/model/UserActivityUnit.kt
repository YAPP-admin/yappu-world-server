package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import java.time.LocalDateTime
import java.util.UUID

data class UserActivityUnit(
    val userId: UUID,
    val createdAt: LocalDateTime,
    val email: String,
    val name: String,
    val role: UserRole,
    val isActive: Boolean,
    val activityUnitId: UUID,
    val generation: Int,
    val position: Position
) {

    fun getActivityUnit() =
        ActivityUnit(
            generation = generation,
            position = position,
            userId = userId
        )
}
