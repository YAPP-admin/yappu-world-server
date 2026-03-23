package co.yappuworld.schedule.infrastructure.dto

import co.yappuworld.global.util.DatetimeUtils.isBeforeOrEqual
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase
import co.yappuworld.schedule.domain.vo.SessionProgressPhase
import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class UserSessionAttendance(
    val id: UUID,
    val name: String,
    val description: String?,
    val place: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val date: LocalDate,
    val endDate: LocalDate,
    val time: LocalTime,
    val endTime: LocalTime,
    val generation: Int,
    val sessionType: SessionType,
    val checkedInAt: LocalDateTime?,
    private val _attendanceStatus: AttendanceStatus?
) {

    companion object {

        fun from(
            session: SessionEntity,
            attendance: AttendanceEntity?
        ): UserSessionAttendance =
            UserSessionAttendance(
                id = session.id,
                name = session.name,
                description = session.description,
                place = session.place,
                address = session.address,
                latitude = session.latitude,
                longitude = session.longitude,
                date = session.date,
                endDate = session.endDate,
                time = session.time,
                endTime = session.endTime,
                generation = session.generation,
                sessionType = session.sessionType,
                checkedInAt = attendance?.userCheckedInAt,
                _attendanceStatus = attendance?.status
            )
    }

    var attendanceStatus: String? = _attendanceStatus?.label
        private set

    val attendanceStatusType: AttendanceStatus? = _attendanceStatus

    fun resolveAttendanceStatus(now: LocalDateTime): AttendanceStatus? =
        when {
            attendanceStatusType == null -> null
            attendanceStatusType == AttendanceStatus.PENDING && !isFinished(now) -> null
            attendanceStatusType == AttendanceStatus.PENDING && isFinished(now) -> AttendanceStatus.ABSENT
            else -> attendanceStatusType
        }

    fun resolveAttendanceStatusOfPastSessions(now: LocalDateTime) {
        if (attendanceStatusType == AttendanceStatus.PENDING && checkedInAt == null && isFinished(now)) {
            attendanceStatus = AttendanceStatus.ABSENT.label
        }
    }

    fun isFinished(now: LocalDateTime): Boolean =
        endDate.isBefore(now.toLocalDate()) ||
            (endDate.isEqual(now.toLocalDate()) && endTime.isBefore(now.toLocalTime()))

    fun isOnGoing(now: LocalDateTime): Boolean {
        val start = LocalDateTime.of(date, time)
        val end = LocalDateTime.of(endDate, endTime)

        return start.isBeforeOrEqual(now) && now.isBeforeOrEqual(end)
    }

    fun isToday(now: LocalDateTime): Boolean =
        date.isBeforeOrEqual(now.toLocalDate()) && now.toLocalDate().isBeforeOrEqual(endDate)

    fun getScheduleProgressPhase(now: LocalDateTime): ScheduleProgressPhase =
        when {
            isFinished(now) -> ScheduleProgressPhase.DONE
            isOnGoing(now) -> ScheduleProgressPhase.ONGOING
            isToday(now) -> ScheduleProgressPhase.TODAY
            else -> ScheduleProgressPhase.PENDING
        }

    fun getSessionProgressPhase(now: LocalDateTime): SessionProgressPhase =
        when {
            isFinished(now) -> SessionProgressPhase.DONE
            isOnGoing(now) -> SessionProgressPhase.ONGOING
            isToday(now) -> SessionProgressPhase.TODAY
            else -> SessionProgressPhase.PENDING
        }
}
