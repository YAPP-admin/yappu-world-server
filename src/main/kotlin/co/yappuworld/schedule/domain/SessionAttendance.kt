package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.util.DatetimeUtils.dDayFrom
import co.yappuworld.global.util.DatetimeUtils.isBeforeOrEqual
import co.yappuworld.global.util.DatetimeUtils.korean
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import java.time.LocalDate
import java.time.LocalDateTime

class SessionAttendance(
    private val user: UserWithActivityUnit,
    private val session: SessionEntity,
    private val attendance: AttendanceEntity?
) {

    init {
        if (user.generation != session.generation) {
            throw BusinessException(AttendanceError.GENERATION_NOT_MATCH)
        }
    }

    companion object {

        fun from(
            user: UserWithActivityUnit,
            session: SessionEntity,
            attendance: AttendanceEntity?
        ): SessionAttendance =
            SessionAttendance(
                user = UserWithActivityUnit(
                    userId = user.userId,
                    email = user.email,
                    name = user.name,
                    role = user.role,
                    generation = user.generation,
                    position = user.position
                ),
                session = session,
                attendance = attendance
            )
    }

    val sessionId = session.id
    val sessionName = session.name
    val sessionStartDate = session.date
    val sessionStartDayOfWeek = session.date.dayOfWeek.korean()
    val sessionEndDate = session.endDate
    val sessionEndDayOfWeek = session.endDate.dayOfWeek.korean()
    val sessionStartTime = session.time
    val sessionEndTime = session.endTime
    val sessionPlace = session.place

    fun getRelativeDays(now: LocalDate): Int = sessionStartDate.dDayFrom(now).toInt()

    fun canCheckIn(now: LocalDateTime): Boolean =
        isBetweenCheckInTime(now) && isAlreadyCheckedId && hasCheckInAuthority(user.role)

    fun getAttendanceStatus(now: LocalDateTime): String? =
        when {
            attendance != null -> attendance.status.label
            session.isFinished(now) -> AttendanceStatus.ABSENT.label
            else -> null
        }

    private fun isBetweenCheckInTime(now: LocalDateTime): Boolean =
        session.checkInTimeFrom.isBeforeOrEqual(now) && now.isBeforeOrEqual(session.checkInTimeUntil)

    private val isAlreadyCheckedId: Boolean = attendance != null

    private fun hasCheckInAuthority(role: UserRole): Boolean = role in listOf(UserRole.ACTIVE)
}
