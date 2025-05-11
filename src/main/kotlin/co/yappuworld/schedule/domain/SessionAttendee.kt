package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.domain.model.UserWithActivityUnits
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import java.util.UUID

class SessionAttendee(
    userWithActivityUnits: UserWithActivityUnits,
    session: SessionEntity
) {

    val id: UUID = userWithActivityUnits.userId
    val name: String = userWithActivityUnits.name
    val role: UserRole = userWithActivityUnits.role
    val position: Position

    init {
        if (userWithActivityUnits.role !in listOf(UserRole.ACTIVE, UserRole.STAFF)) {
            throw BusinessException(AttendanceError.UNAUTHORIZED_CHECK_IN)
        }

        val sessionGenerationActivityUnits = userWithActivityUnits.activityUnits
            .filter { it.generation == session.generation }

        if (sessionGenerationActivityUnits.isEmpty()) {
            throw BusinessException(AttendanceError.NO_ATTENDEE_ACTIVITY_IN_GENERATION)
        }

        position = try {
            sessionGenerationActivityUnits.single { it.position.isAttendeePosition() }.position
        } catch (e: RuntimeException) {
            throw BusinessException(AttendanceError.NO_ATTENDEE_POSITION_ACTIVITY_IN_GENERATION)
        }
    }

}
