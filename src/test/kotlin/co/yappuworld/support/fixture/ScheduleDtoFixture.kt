package co.yappuworld.support.fixture

import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.domain.vo.ScheduleType
import co.yappuworld.schedule.domain.vo.SessionType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

object ScheduleDtoFixture {

    fun getAdminSessionCreateRequestFixture(
        name: String = "성과공유회",
        description: String? = null,
        place: String? = null,
        date: LocalDate = LocalDate.of(2024, 12, 12),
        endDate: LocalDate = LocalDate.of(2024, 12, 12),
        time: LocalTime = LocalTime.of(11, 0),
        endTime: LocalTime = LocalTime.of(14, 0),
        generation: Int = 25,
        type: ScheduleType = ScheduleType.SESSION,
        sessionType: SessionType = SessionType.OFFLINE,
        userIds: List<UUID>? = emptyList()
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
            userIds = userIds
        )
}
