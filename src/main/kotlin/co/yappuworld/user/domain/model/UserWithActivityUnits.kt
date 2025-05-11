package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import java.util.UUID

class UserWithActivityUnits(
    val userId: UUID,
    val email: String,
    val name: String,
    val role: UserRole,
    val activityUnits: List<ActivityUnit>
) {

    companion object {
        fun of(elements: List<UserWithActivityUnit>): UserWithActivityUnits {
            check(elements.groupBy { it.userId }.size == 1)

            return UserWithActivityUnits(
                userId = elements.single().userId,
                email = elements.single().email,
                name = elements.single().name,
                role = elements.single().role,
                activityUnits = elements.map { ActivityUnit(it.generation, it.position, it.userId) }
            )
        }
    }

    val activityGenerations
        get() = activityUnits.map { it.generation }.distinct()
}
