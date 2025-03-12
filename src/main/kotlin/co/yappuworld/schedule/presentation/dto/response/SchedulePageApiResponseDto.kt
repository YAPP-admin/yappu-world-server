package co.yappuworld.schedule.presentation.dto.response

import co.yappuworld.schedule.domain.ScheduleProgressPhase
import co.yappuworld.schedule.domain.ScheduleType
import co.yappuworld.schedule.domain.SessionType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class SchedulePageApiResponseDto(
    val dates: List<DateGroupedScheduleApiResponseDto>
) {

    constructor(
        response: SchedulePageAppResponseDto
    ) : this(
        dates = response.schedules.map { DateGroupedScheduleApiResponseDto(it) }
    )
}

data class DateGroupedScheduleApiResponseDto(
    val date: LocalDate,
    val schedules: List<SimpleScheduleApiResponseDto>
) {

    constructor(overview: ScheduleOverviewAppResponseDto) : this(
        date = overview.date,
        schedules = overview.contents.map { SimpleScheduleApiResponseDto(it) }.sortedBy { it.time }
    )
}

data class SimpleScheduleApiResponseDto(
    val id: UUID,
    val name: String,
    val place: String?,
    val date: LocalDate,
    val time: LocalTime?,
    val endTime: LocalTime?,
    val scheduleType: ScheduleType,
    val sessionType: SessionType?,
    val scheduleProgressPhase: ScheduleProgressPhase
) {

    constructor(content: ScheduleSimpleContentAppResponseDto) : this(
        id = content.id,
        name = content.name,
        place = content.place,
        date = content.date,
        time = content.time,
        endTime = content.endTime,
        scheduleType = content.scheduleType,
        sessionType = content.sessionType,
        scheduleProgressPhase = content.progressPhase
    )
}
