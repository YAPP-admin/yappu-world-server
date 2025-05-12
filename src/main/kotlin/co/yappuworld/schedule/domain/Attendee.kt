package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.UserWithActivityUnits
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import java.util.UUID

class Attendee(
    val id: UUID,
    val name: String,
    val role: UserRole,
    val position: Position
) {

    companion object {

        fun from(
            userWithActivityUnit: UserWithActivityUnit,
            generation: Int
        ): Attendee {
            checkRole(userWithActivityUnit.role)
            checkActivityUnit(userWithActivityUnit.getActivityUnit(), generation)

            return Attendee(
                id = userWithActivityUnit.userId,
                name = userWithActivityUnit.name,
                role = userWithActivityUnit.role,
                position = userWithActivityUnit.position
            )
        }

        fun from(
            userWithActivityUnits: UserWithActivityUnits,
            generation: Int
        ): Attendee {
            checkRole(userWithActivityUnits.role)

            val sessionGenerationActivityUnits = userWithActivityUnits.activityUnits
                .filter { it.generation == generation }

            if (sessionGenerationActivityUnits.isEmpty()) {
                throw BusinessException(AttendanceError.NO_ATTENDEE_ACTIVITY_IN_GENERATION)
            }

            val position = try {
                sessionGenerationActivityUnits.single { it.position.isAttendeePosition() }.position
            } catch (e: RuntimeException) {
                throw BusinessException(AttendanceError.NO_ATTENDEE_POSITION_ACTIVITY_IN_GENERATION)
            }

            return Attendee(
                id = userWithActivityUnits.userId,
                name = userWithActivityUnits.name,
                role = userWithActivityUnits.role,
                position = position
            )
        }

        private fun checkRole(role: UserRole) {
            if (role !in listOf(UserRole.ACTIVE, UserRole.STAFF)) {
                throw BusinessException(AttendanceError.UNAUTHORIZED_CHECK_IN)
            }
        }

        private fun checkActivityUnit(
            activityUnit: ActivityUnit,
            generation: Int
        ) {
            if (activityUnit.generation != generation) {
                throw BusinessException(AttendanceError.NO_ATTENDEE_ACTIVITY_IN_GENERATION)
            }

            if (!activityUnit.position.isAttendeePosition()) {
                throw BusinessException(AttendanceError.NO_ATTENDEE_POSITION_ACTIVITY_IN_GENERATION)
            }
        }
    }
}
