package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.util.DatetimeUtils.korean
import co.yappuworld.global.util.LocalDateRange
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase
import co.yappuworld.schedule.domain.vo.ScheduleType
import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendanceDto
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.ScheduleEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class SchedulePageResponse(
    @field:Schema(description = "날짜 목록")
    val dates: List<DateGroupedScheduleResponse>
) {

    companion object {
        fun from(
            schedules: List<ScheduleEntity>,
            attendances: List<AttendanceEntity>,
            request: SchedulePageRequest,
            now: LocalDateTime
        ): SchedulePageResponse {
            val attendanceBySessionId = attendances.associateBy { it.session.id }
            // TODO: 일단 세션 뿐이니, 세션으로만 변환, 추후에 다른 Schedule도 처리하도록 변경 필요
            val scheduleWithAttendance = schedules.map {
                SessionWithAttendanceDto.from(it as SessionEntity, attendanceBySessionId[it.id])
            }
            val scheduleByDate = scheduleWithAttendance.groupBy { it.date }

            return LocalDateRange(request.from, request.toInclusive.plusDays(1))
                .map { date ->
                    DateGroupedScheduleResponse(
                        date = date,
                        scheduleWithAttendance = scheduleByDate[date] ?: emptyList(),
                        now = now
                    )
                }.let { SchedulePageResponse(it) }
        }
    }
}

data class DateGroupedScheduleResponse(
    @field:Schema(description = "날짜")
    val date: LocalDate,
    @field:Schema(description = "요일")
    val dayOfTheWeek: String,
    @field:Schema(description = "당일 여부")
    val isToday: Boolean,
    @field:Schema(description = "일정 목록")
    val schedules: List<SimpleScheduleResponse>
) {

    constructor(
        date: LocalDate,
        scheduleWithAttendance: List<SessionWithAttendanceDto>,
        now: LocalDateTime
    ) : this(
        date = date,
        isToday = date.isEqual(now.toLocalDate()),
        dayOfTheWeek = date.dayOfWeek.korean(),
        schedules = scheduleWithAttendance.map { SimpleScheduleResponse.from(it, now) }
    )
}

data class SimpleScheduleResponse(
    @field:Schema(description = "일정 ID")
    val id: UUID,
    @field:Schema(description = "일정 이름")
    val name: String,
    @field:Schema(description = "일정 장소", nullable = true)
    val place: String?,
    @field:Schema(description = "일정 날짜")
    val date: LocalDate,
    @field:Schema(description = "일정 시작 요일")
    val startDayOfTheWeek: String,
    @field:Schema(description = "일정 종료 날짜")
    val endDate: LocalDate,
    @field:Schema(description = "일정 종료 요일")
    val endDayOfTheWeek: String,
    @field:Schema(description = "일정 시작 시간", nullable = true)
    val time: LocalTime?,
    @field:Schema(description = "일정 종료 시간", nullable = true)
    val endTime: LocalTime?,
    @field:Schema(description = "일정 종류")
    val scheduleType: ScheduleType,
    @field:Schema(description = "세션 종류", nullable = true)
    val sessionType: SessionType?,
    @field:Schema(description = "일정 진행 상태")
    val scheduleProgressPhase: ScheduleProgressPhase,
    @field:Schema(description = "출석 상태", nullable = true, allowableValues = ["출석", "지각", "결석", "조퇴", "공결"])
    val attendanceStatus: String?
) {

    companion object {
        fun from(
            scheduleWithAttendance: SessionWithAttendanceDto,
            now: LocalDateTime
        ): SimpleScheduleResponse = convertSession(scheduleWithAttendance, now)

        private fun convertSession(
            sessionWithAttendance: SessionWithAttendanceDto,
            now: LocalDateTime
        ): SimpleScheduleResponse =
            sessionWithAttendance.let {
                it.resolveAttendanceStatusOfPastSessions(now)
                SimpleScheduleResponse(
                    id = it.id,
                    name = it.name,
                    place = it.place,
                    date = it.date,
                    startDayOfTheWeek = it.date.dayOfWeek.korean(),
                    endDate = it.endDate,
                    endDayOfTheWeek = it.endDate.dayOfWeek.korean(),
                    time = it.time,
                    endTime = it.endTime,
                    scheduleType = ScheduleType.SESSION,
                    sessionType = it.sessionType,
                    scheduleProgressPhase = it.getScheduleProgressPhase(now),
                    attendanceStatus = it.attendanceStatus
                )
            }
    }
}
