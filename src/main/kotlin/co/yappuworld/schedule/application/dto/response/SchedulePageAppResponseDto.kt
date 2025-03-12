package co.yappuworld.schedule.application.dto.response

import co.yappuworld.global.util.LocalDateRange
import co.yappuworld.schedule.application.dto.request.SchedulePageAppRequestDto
import co.yappuworld.schedule.domain.ScheduleEntity
import co.yappuworld.schedule.domain.ScheduleProgressPhase
import co.yappuworld.schedule.domain.ScheduleType
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionType
import co.yappuworld.schedule.domain.TaskEntity
import co.yappuworld.schedule.domain.getProgressPhase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class SchedulePageAppResponseDto(
    val dates: List<DateGroupedScheduleAppResponseDto>
) {
    companion object {
        fun from(
            schedules: List<ScheduleEntity>,
            request: SchedulePageAppRequestDto,
            now: LocalDateTime
        ): SchedulePageAppResponseDto {
            val scheduleByDate = schedules.groupBy { it.date }

            return LocalDateRange(request.from, request.to)
                .map { date ->
                    DateGroupedScheduleAppResponseDto(
                        date = date,
                        schedules = scheduleByDate[date] ?: emptyList(),
                        now = now
                    )
                }.let { SchedulePageAppResponseDto(it) }
        }
    }
}

data class DateGroupedScheduleAppResponseDto(
    val date: LocalDate,
    val schedules: List<SimpleScheduleAppResponseDto>
) {
    constructor(date: LocalDate, schedules: List<ScheduleEntity>, now: LocalDateTime) : this(
        date = date,
        schedules = schedules.map { SimpleScheduleAppResponseDto.from(it, now) }
    )
}

data class SimpleScheduleAppResponseDto(
    val id: UUID,
    val name: String,
    val place: String?,
    val date: LocalDate,
    val endDate: LocalDate?,
    val time: LocalTime?,
    val endTime: LocalTime?,
    val scheduleType: ScheduleType,
    val sessionType: SessionType?,
    val progressPhase: ScheduleProgressPhase
) {

    companion object {
        fun from(
            schedule: ScheduleEntity,
            now: LocalDateTime
        ): SimpleScheduleAppResponseDto =
            when (schedule) {
                is SessionEntity -> convertSession(schedule, now)
                is TaskEntity -> TODO()
                else -> TODO()
            }

        private fun convertSession(
            session: SessionEntity,
            now: LocalDateTime
        ): SimpleScheduleAppResponseDto =
            SimpleScheduleAppResponseDto(
                id = session.id,
                name = session.name,
                place = session.place,
                date = session.date,
                endDate = session.endDate,
                time = session.time,
                endTime = session.endTime,
                scheduleType = ScheduleType.SESSION,
                sessionType = session.sessionType,
                progressPhase = session.getProgressPhase(now)
            )
    }
}
