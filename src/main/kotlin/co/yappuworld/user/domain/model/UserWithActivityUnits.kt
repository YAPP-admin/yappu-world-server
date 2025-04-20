package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.vo.UserRole
import java.util.UUID

class UserWithActivityUnits(
    val userId: UUID,
    val email: String,
    val name: String,
    val role: UserRole,
    val activityUnits: List<ActivityUnit>
) {

    val activityGenerations
        get() = activityUnits.map { it.generation }.distinct()
}
