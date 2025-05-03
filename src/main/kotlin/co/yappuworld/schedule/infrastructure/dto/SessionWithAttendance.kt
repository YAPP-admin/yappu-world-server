package co.yappuworld.schedule.infrastructure.dto

import co.yappuworld.global.util.DatetimeUtils.isBeforeOrEqual
import co.yappuworld.schedule.domain.AttendanceStatus
import co.yappuworld.schedule.domain.SessionType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class SessionWithAttendance(
    val id: UUID,
    val name: String,
    val description: String?,
    val place: String?,
    val date: LocalDate,
    val endDate: LocalDate,
    val time: LocalTime?,
    val endTime: LocalTime?,
    val generation: Int,
    val sessionType: SessionType,
    val checkedInAt: LocalDateTime?,
    attendanceStatus: AttendanceStatus?
) {

    var attendanceStatus: String? = attendanceStatus?.label
        private set

    fun resolveAttendanceStatusOfPastSessions(now: LocalDateTime) {
        if (attendanceStatus == null && isFinished(now)) {
            attendanceStatus = AttendanceStatus.ABSENT.label
        }
    }

    fun isFinished(now: LocalDateTime): Boolean =
        endDate.isBefore(now.toLocalDate()) ||
            (endDate.isEqual(now.toLocalDate()) && (endTime?.isBefore(now.toLocalTime()) == true))

    fun isToday(now: LocalDateTime): Boolean =
        date.isBeforeOrEqual(now.toLocalDate()) && now.toLocalDate().isBeforeOrEqual(endDate)
}
