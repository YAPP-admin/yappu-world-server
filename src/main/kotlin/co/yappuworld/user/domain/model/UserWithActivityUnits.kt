package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import java.util.UUID

class UserWithActivityUnits(
    val id: UUID,
    val email: String,
    val name: String,
    val role: UserRole,
    val activityUnits: List<ActivityUnit>
) {

    companion object {
        fun of(elements: List<UserWithActivityUnit>): UserWithActivityUnits {
            check(elements.groupBy { it.userId }.size == 1)

            return UserWithActivityUnits(
                id = elements.first().userId,
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

    fun hasAttendeeActivityInGeneration(generation: Int): Boolean =
        activityUnits.any { it.generation == generation && it.position.isAttendeePosition() }
}
