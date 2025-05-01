package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.util.TimeUtils.LocalDateRange
import co.yappuworld.global.util.TimeUtils.korean
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.domain.entity.AttendanceEntity
import co.yappuworld.schedule.domain.AttendanceStatus.ABSENT
import co.yappuworld.schedule.domain.entity.ScheduleEntity
import co.yappuworld.schedule.domain.ScheduleProgressPhase
import co.yappuworld.schedule.domain.ScheduleType
import co.yappuworld.schedule.domain.entity.SessionEntity
import co.yappuworld.schedule.domain.SessionType
import co.yappuworld.schedule.domain.entity.TaskEntity
import co.yappuworld.schedule.domain.getProgressPhase
import co.yappuworld.user.domain.model.UserWithActivityUnits
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
            userWithActivityUnits: UserWithActivityUnits,
            schedules: List<ScheduleEntity>,
            attendances: List<AttendanceEntity>,
            request: SchedulePageRequest,
            now: LocalDateTime
        ): SchedulePageResponse {
            val attendanceBySessionId = attendances.associateBy { it.scheduleId }
            val scheduleWithAttendance = schedules.map { Pair(it, attendanceBySessionId[it.id]) }
            val scheduleByDate = scheduleWithAttendance.groupBy { (schedule, _) -> schedule.date }

            return LocalDateRange(request.from, request.toInclusive.plusDays(1))
                .map { date ->
                    DateGroupedScheduleResponse(
                        date = date,
                        userWithActivityUnits = userWithActivityUnits,
                        scheduleWithAttendance = scheduleByDate[date] ?: emptyList(),
                        now = now
                    )
                }.let { SchedulePageResponse(it) }
        }
    }
}

data class DateGroupedScheduleResponse(
    @Schema(description = "날짜")
    val date: LocalDate,
    @Schema(description = "요일")
    val dayOfTheWeek: String,
    @Schema(description = "당일 여부")
    val isToday: Boolean,
    @Schema(description = "일정 목록")
    val schedules: List<SimpleScheduleResponse>
) {

    constructor(
        date: LocalDate,
        userWithActivityUnits: UserWithActivityUnits,
        scheduleWithAttendance: List<Pair<ScheduleEntity, AttendanceEntity?>>,
        now: LocalDateTime
    ) : this(
        date = date,
        isToday = date.isEqual(now.toLocalDate()),
        dayOfTheWeek = date.dayOfWeek.korean(),
        schedules = scheduleWithAttendance.map { SimpleScheduleResponse.from(it, userWithActivityUnits, now) }
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
    val scheduleProgressPhase: ScheduleProgressPhase,
    @Schema(description = "출석 상태", nullable = true, allowableValues = ["출석", "지각", "결석", "조퇴", "공결"])
    val attendanceStatus: String?
) {

    companion object {
        fun from(
            scheduleWithAttendance: Pair<ScheduleEntity, AttendanceEntity?>,
            userWithActivityUnits: UserWithActivityUnits,
            now: LocalDateTime
        ): SimpleScheduleResponse =
            when (scheduleWithAttendance.first) {
                is SessionEntity -> convertSession(scheduleWithAttendance, userWithActivityUnits, now)
                is TaskEntity -> TODO()
                else -> TODO()
            }

        private fun convertSession(
            sessionWithAttendance: Pair<ScheduleEntity, AttendanceEntity?>,
            userWithActivityUnits: UserWithActivityUnits,
            now: LocalDateTime
        ): SimpleScheduleResponse =
            sessionWithAttendance.let { (s, attendance) ->
                val session = s as SessionEntity
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
                    scheduleProgressPhase = session.getProgressPhase(now),
                    attendanceStatus = ABSENT.label.takeIf {
                        attendance == null &&
                            session.isFinished(now) &&
                            session.generation in userWithActivityUnits.activityUnits.map { it.generation }
                    }
                )
            }
    }
}
