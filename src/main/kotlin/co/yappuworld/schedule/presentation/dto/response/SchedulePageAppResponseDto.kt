package co.yappuworld.schedule.presentation.dto.response

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
    val schedules: List<ScheduleOverviewAppResponseDto>
) {
    companion object {
        fun from(
            schedules: List<ScheduleEntity>,
            now: LocalDateTime
        ): SchedulePageAppResponseDto {
            val scheduleByDate = schedules.groupBy { it.date }
            return SchedulePageAppResponseDto(
                scheduleByDate.keys.sortedBy { it.dayOfMonth }.map {
                    ScheduleOverviewAppResponseDto(it, scheduleByDate[it] ?: emptyList(), now)
                }
            )
        }
    }
}

data class ScheduleOverviewAppResponseDto(
    val date: LocalDate,
    val contents: List<ScheduleSimpleContentAppResponseDto>
) {
    constructor(date: LocalDate, schedules: List<ScheduleEntity>, now: LocalDateTime) : this(
        date = date,
        contents = schedules.map { ScheduleSimpleContentAppResponseDto.from(it, now) }
    )
}

data class ScheduleSimpleContentAppResponseDto(
    val id: UUID,
    val name: String,
    val place: String?,
    val date: LocalDate,
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
        ): ScheduleSimpleContentAppResponseDto =
            when (schedule) {
                is SessionEntity -> convertSession(schedule, now)
                is TaskEntity -> TODO()
                else -> TODO()
            }

        private fun convertSession(
            session: SessionEntity,
            now: LocalDateTime
        ): ScheduleSimpleContentAppResponseDto =
            ScheduleSimpleContentAppResponseDto(
                id = session.id,
                name = session.name,
                place = session.place,
                date = session.date,
                time = session.time,
                endTime = session.endTime,
                scheduleType = ScheduleType.SESSION,
                sessionType = session.sessionType,
                progressPhase = session.getProgressPhase(now)
            )
    }
}
