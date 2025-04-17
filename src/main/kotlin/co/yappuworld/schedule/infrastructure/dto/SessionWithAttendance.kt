package co.yappuworld.schedule.infrastructure.dto

import co.yappuworld.schedule.domain.AttendanceStatus
import co.yappuworld.schedule.domain.SessionType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class SessionWithAttendance(
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
    private val _attendanceStatus: AttendanceStatus?
) {

    val attendanceStatus: String
        get() = _attendanceStatus?.label ?: AttendanceStatus.ABSENT.label
}
