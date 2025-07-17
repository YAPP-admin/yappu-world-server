package co.yappuworld.support.fixture

import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.domain.vo.ScheduleType
import co.yappuworld.schedule.domain.vo.SessionType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

object ScheduleDtoFixture {

    fun getAdminSessionCreateRequestFixture(
        name: String = "세션 이름",
        description: String? = null,
        place: String? = "세션 장소",
        date: LocalDate = LocalDate.of(2024, 12, 12),
        endDate: LocalDate = LocalDate.of(2024, 12, 12),
        time: LocalTime = LocalTime.of(10, 0),
        endTime: LocalTime = LocalTime.of(12, 0),
        generation: Int = 25,
        type: ScheduleType = ScheduleType.SESSION,
        sessionType: SessionType = SessionType.OFFLINE,
        attendeeIds: List<UUID> = emptyList(),
        noticeIds: List<UUID> = emptyList()
    ): AdminSessionCreateRequest =
        AdminSessionCreateRequest(
            name = name,
            description = description,
            place = place,
            date = date,
            endDate = endDate,
            time = time,
            endTime = endTime,
            generation = generation,
            type = type,
            sessionType = sessionType,
            sessionAttendeeIds = attendeeIds,
            noticeIds = noticeIds
        )

    fun getAdminSessionUpdateRequestFixture(
        id: UUID = UUID.randomUUID(),
        name: String = "name",
        generation: Int = 25,
        place: String = "장소",
        date: LocalDate = LocalDate.of(2024, 12, 12),
        endDate: LocalDate = LocalDate.of(2024, 12, 12),
        time: LocalTime = LocalTime.of(10, 0),
        endTime: LocalTime = LocalTime.of(11, 0),
        sessionType: SessionType = SessionType.ONLINE,
        sessionAttendeeIds: List<UUID> = emptyList(),
        noticeIds: List<UUID> = emptyList()
    ) = AdminSessionUpdateRequest(
        id = id,
        name = name,
        generation = generation,
        place = place,
        date = date,
        endDate = endDate,
        time = time,
        endTime = endTime,
        sessionType = sessionType,
        sessionAttendeeIds = sessionAttendeeIds,
        noticeIds = noticeIds
    )
}
