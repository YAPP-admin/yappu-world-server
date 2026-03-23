package co.yappuworld.support.fixture

import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.dto.UserSessionAttendance
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
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

    fun getSessionWithAttendanceFixture(
        id: UUID = UUID.randomUUID(),
        name: String = "세션 이름",
        description: String? = "세션 설명",
        place: String? = "세션 장소",
        address: String? = "서울특별시 종로구 종로 33",
        latitude: Double? = 37.5720065838703,
        longitude: Double? = 126.981437983842,
        date: LocalDate = LocalDate.of(2025, 2, 15),
        endDate: LocalDate = LocalDate.of(2025, 2, 15),
        time: LocalTime = LocalTime.of(14, 0),
        endTime: LocalTime = LocalTime.of(18, 0),
        generation: Int = 25,
        sessionType: SessionType = SessionType.OFFLINE,
        attendanceStatus: AttendanceStatus? = AttendanceStatus.ON_TIME,
        checkedInAt: LocalDateTime? = LocalDateTime.of(2025, 2, 15, 14, 0)
    ): UserSessionAttendance =
        UserSessionAttendance(
            id = id,
            name = name,
            description = description,
            place = place,
            address = address,
            latitude = latitude,
            longitude = longitude,
            date = date,
            endDate = endDate,
            time = time,
            endTime = endTime,
            generation = generation,
            sessionType = sessionType,
            _attendanceStatus = attendanceStatus,
            checkedInAt = checkedInAt
        )
}
