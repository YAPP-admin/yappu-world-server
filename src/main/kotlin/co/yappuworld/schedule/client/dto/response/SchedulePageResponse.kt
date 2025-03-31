package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.util.LocalDateRange
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
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

data class SchedulePageResponse(
    val dates: List<DateGroupedScheduleResponse>
) {

    companion object {
        fun from(
            schedules: List<ScheduleEntity>,
            request: SchedulePageRequest,
            now: LocalDateTime
        ): SchedulePageResponse {
            val scheduleByDate = schedules.groupBy { it.date }

            return LocalDateRange(request.from, request.to)
                .map { date ->
                    DateGroupedScheduleResponse(
                        date = date,
                        schedules = scheduleByDate[date] ?: emptyList(),
                        now = now
                    )
                }.let { SchedulePageResponse(it) }
        }
    }
}

data class DateGroupedScheduleResponse(
    val date: LocalDate,
    val schedules: List<SimpleScheduleResponse>
) {

    constructor(date: LocalDate, schedules: List<ScheduleEntity>, now: LocalDateTime) : this(
        date = date,
        schedules = schedules.map { SimpleScheduleResponse.from(it, now) }
    )
}

data class SimpleScheduleResponse(
    val id: UUID,
    val name: String,
    val place: String?,
    val date: LocalDate,
    val endDate: LocalDate?,
    val time: LocalTime?,
    val endTime: LocalTime?,
    val scheduleType: ScheduleType,
    val sessionType: SessionType?,
    val scheduleProgressPhase: ScheduleProgressPhase
) {

    companion object {
        fun from(
            schedule: ScheduleEntity,
            now: LocalDateTime
        ): SimpleScheduleResponse =
            when (schedule) {
                is SessionEntity -> convertSession(schedule, now)
                is TaskEntity -> TODO()
                else -> TODO()
            }

        private fun convertSession(
            session: SessionEntity,
            now: LocalDateTime
        ): SimpleScheduleResponse =
            SimpleScheduleResponse(
                id = session.id,
                name = session.name,
                place = session.place,
                date = session.date,
                endDate = session.endDate,
                time = session.time,
                endTime = session.endTime,
                scheduleType = ScheduleType.SESSION,
                sessionType = session.sessionType,
                scheduleProgressPhase = session.getProgressPhase(now)
            )
    }
}
