package co.yappuworld.support.fixture

import co.yappuworld.schedule.domain.AttendanceStatus
import co.yappuworld.schedule.domain.entity.SessionEntity
import co.yappuworld.schedule.domain.SessionType
import co.yappuworld.schedule.domain.entity.TaskEntity
import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendance
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

object ScheduleFixture {

    fun getSessionEntityFixture(
        name: String = "시범 세션",
        description: String? = "시범 세션이니깐 걱정 마세요",
        place: String? = "공덕 창업 허브",
        date: LocalDate = LocalDate.of(2025, 2, 15),
        endDate: LocalDate = LocalDate.of(2025, 2, 15),
        time: LocalTime = LocalTime.of(14, 0),
        endTime: LocalTime = LocalTime.of(18, 0),
        generation: Int = 25,
        sessionType: SessionType = SessionType.OFFLINE
    ) = SessionEntity(
        name = name,
        description = description,
        place = place,
        date = date,
        endDate = endDate,
        isAllDay = false,
        time = time,
        endTime = endTime,
        generation = generation,
        sessionType = sessionType
    )

    fun getTaskEntityFixture(
        name: String = "시범 과제",
        description: String? = "시범 과제이니깐 걱정 마세요",
        place: String? = "공덕 창업 허브",
        date: LocalDate = LocalDate.of(2025, 2, 15),
        endDate: LocalDate = LocalDate.of(2025, 2, 15),
        time: LocalTime = LocalTime.of(14, 0),
        endTime: LocalTime = LocalTime.of(18, 0)
    ) = TaskEntity(
        name = name,
        description = description,
        place = place,
        date = date,
        endDate = endDate,
        isAllDay = false,
        time = time,
        endTime = endTime
    )

    fun getSessionWithAttendanceFixture(
        id: UUID = UUID.randomUUID(),
        name: String = "세션 이름",
        description: String? = "세션 설명",
        place: String? = "세션 장소",
        date: LocalDate = LocalDate.of(2025, 2, 15),
        endDate: LocalDate = LocalDate.of(2025, 2, 15),
        time: LocalTime? = LocalTime.of(14, 0),
        endTime: LocalTime? = LocalTime.of(18, 0),
        generation: Int = 25,
        sessionType: SessionType = SessionType.OFFLINE,
        attendanceStatus: AttendanceStatus? = AttendanceStatus.ON_TIME,
        checkedInAt: LocalDateTime? = LocalDateTime.of(2025, 2, 15, 14, 0)
    ): SessionWithAttendance =
        SessionWithAttendance(
            id = id,
            name = name,
            description = description,
            place = place,
            date = date,
            endDate = endDate,
            time = time,
            endTime = endTime,
            generation = generation,
            sessionType = sessionType,
            attendanceStatus = attendanceStatus,
            checkedInAt = checkedInAt
        )
}
