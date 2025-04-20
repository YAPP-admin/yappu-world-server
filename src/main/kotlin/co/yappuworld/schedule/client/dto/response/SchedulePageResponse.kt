package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.util.TimeUtils.LocalDateRange
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.domain.ScheduleEntity
import co.yappuworld.schedule.domain.ScheduleProgressPhase
import co.yappuworld.schedule.domain.ScheduleType
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionType
import co.yappuworld.schedule.domain.TaskEntity
import co.yappuworld.schedule.domain.getProgressPhase
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class SchedulePageResponse(
    @Schema(description = "날짜 목록")
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
    @Schema(description = "날짜")
    val date: LocalDate,
    @Schema(description = "일정 목록")
    val schedules: List<SimpleScheduleResponse>
) {

    constructor(date: LocalDate, schedules: List<ScheduleEntity>, now: LocalDateTime) : this(
        date = date,
        schedules = schedules.map { SimpleScheduleResponse.from(it, now) }
    )
}

data class SimpleScheduleResponse(
    @Schema(description = "일정 ID")
    val id: UUID,
    @Schema(description = "일정 이름")
    val name: String,
    @Schema(description = "일정 장소", nullable = true)
    val place: String?,
    @Schema(description = "일정 날짜")
    val date: LocalDate,
    @Schema(description = "일정 종료 날짜")
    val endDate: LocalDate,
    @Schema(description = "일정 시작 시간", nullable = true)
    val time: LocalTime?,
    @Schema(description = "일정 종료 시간", nullable = true)
    val endTime: LocalTime?,
    @Schema(description = "일정 종류")
    val scheduleType: ScheduleType,
    @Schema(description = "세션 종류", nullable = true)
    val sessionType: SessionType?,
    @Schema(description = "일정 진행 상태")
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
