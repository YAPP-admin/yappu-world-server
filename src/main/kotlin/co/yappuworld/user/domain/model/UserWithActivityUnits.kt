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

    constructor(user: UserWithActivityUnit) : this(
        userId = user.userId,
        email = user.email,
        name = user.name,
        role = user.role,
        activityUnits = listOf(
            ActivityUnit(
                generation = user.generation,
                position = user.position,
                userId = user.userId
            )
        )
    )

    companion object {
        fun of(elements: List<UserWithActivityUnit>): UserWithActivityUnits {
            check(elements.groupBy { it.userId }.size == 1)

            return UserWithActivityUnits(
                userId = elements.first().userId,
                email = elements.first().email,
                name = elements.first().name,
                role = elements.first().role,
                activityUnits = elements
                    .map { ActivityUnit(it.generation, it.position, it.userId) }
                    .sortedByDescending { au -> au.generation }
            )
        }
    }

    val activityGenerations
        get() = activityUnits.map { it.generation }.distinct()
}
