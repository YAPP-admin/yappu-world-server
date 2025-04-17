package co.yappuworld.schedule.infrastructure.dto

import co.yappuworld.attendance.domain.AttendanceStatus
import co.yappuworld.schedule.domain.SessionType
import java.time.LocalDate
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
    val attendanceStatus: AttendanceStatus?
)
