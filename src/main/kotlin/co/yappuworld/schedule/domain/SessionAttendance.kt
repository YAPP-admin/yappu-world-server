package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.util.DatetimeUtils.dDayFrom
import co.yappuworld.global.util.DatetimeUtils.isBeforeOrEqual
import co.yappuworld.global.util.DatetimeUtils.korean
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.jpa.AttendanceEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDate
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

/**
 * 특정 세션의 출석 정보
 */
class SessionAttendance(
    private val attendee: Attendee,
    private val session: SessionEntity,
    private var attendance: AttendanceEntity?
) {

    val sessionId = session.id
    val sessionName = session.name
    val sessionStartDate = session.date
    val sessionStartDayOfWeek = session.date.dayOfWeek.korean()
    val sessionEndDate = session.endDate
    val sessionEndDayOfWeek = session.endDate.dayOfWeek.korean()
    val sessionStartTime = session.time
    val sessionEndTime = session.endTime
    val sessionPlace = session.place

    private val hasAttendance: Boolean
        get() = attendance != null

    fun getRelativeDays(now: LocalDate): Int = sessionStartDate.dDayFrom(now).toInt()

    fun canCheckIn(now: LocalDateTime): Boolean = now.isBetweenCheckInTime() && !hasAttendance

    fun checkIn(now: LocalDateTime) {
        validateCheckInAvailability(now)
        attendance = AttendanceEntity(
            status = session.decideCheckInStatus(now),
            userId = attendee.id,
            scheduleId = session.id
        )
    }

    fun validateCheckInAvailability(now: LocalDateTime) {
        if (hasAttendance) {
            logger.warn { "이미 출석을 완료한 유저(${attendee.id})입니다." }
            throw BusinessException(AttendanceError.ALREADY_CHECKED_IN)
        }

        if (!now.isBetweenCheckInTime()) {
            logger.warn { "출석 체크 가능 시간이 아닙니다. now: $now" }
            throw BusinessException(AttendanceError.NOT_CHECK_IN_TIME)
        }
    }

    fun getAttendanceStatus(now: LocalDateTime): String? {
        val safeAttendance = attendance
        return when {
            safeAttendance != null -> safeAttendance.status.label
            session.isFinished(now) -> AttendanceStatus.ABSENT.label
            else -> null
        }
    }

    fun getNewAttendance(): AttendanceEntity {
        if (attendance == null) {
            logger.error { "출석을 위한 데이터가 없습니다. 로직을 확인해주세요." }
            throw BusinessException(AttendanceError.NO_ATTENDANCE_TO_CHECK_IN)
        }

        return checkNotNull(attendance)
    }

    private fun LocalDateTime.isBetweenCheckInTime(): Boolean =
        session.checkInTimeFrom.isBeforeOrEqual(this) && this.isBefore(session.checkInTimeUntil)
}
