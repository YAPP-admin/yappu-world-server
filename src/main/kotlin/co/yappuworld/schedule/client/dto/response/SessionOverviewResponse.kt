package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.util.DatetimeUtils.dDayFrom
import co.yappuworld.global.util.DatetimeUtils.korean
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase
import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.domain.model.UserWithActivityUnits
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class SessionOverviewResponse(
    @Schema(description = "세션 목록, 데이터가 없다면 빈 리스트")
    val sessions: List<SessionOverviewItemResponse>,
    @Schema(
        description = """
            가장 가까이 예정된 세션의 인덱스
            세션이 없거나, 모든 세션이 종료됐다면 null
        """,
        nullable = true
    )
    val upcomingSessionId: UUID? = null
) {

    companion object {

        fun from(
            user: UserWithActivityUnits,
            sessions: List<SessionEntity>,
            attendances: List<AttendanceEntity>,
            now: LocalDateTime
        ): SessionOverviewResponse {
            val attendanceByScheduleId = attendances.associateBy { it.scheduleId }
            val result = sessions
                .map { session ->
                    SessionOverviewItemResponse.from(
                        user = user,
                        session = session,
                        attendance = attendanceByScheduleId[session.id],
                        now = now
                    )
                }.sortedWith(
                    compareBy(
                        { it.scheduleProgressPhase.order },
                        { it.date },
                        { it.time },
                        { it.endDate },
                        { it.endTime },
                        { it.generation },
                        { it.id }
                    )
                )

            return SessionOverviewResponse(
                sessions = result,
                upcomingSessionId = result.firstOrNull { it.progressPhase != ScheduleProgressPhase.DONE.label }?.id
            )
        }
    }
}

data class SessionOverviewItemResponse(
    @Schema(description = "세션 식별자")
    val id: UUID,
    @Schema(description = "세션 이름")
    val name: String,
    @Schema(description = "세션 장소", nullable = true)
    val place: String?,
    @Schema(description = "세션 기수")
    val generation: Int,
    @Schema(description = "세션 시작일")
    val date: LocalDate,
    @Schema(description = "세션 시작 요일")
    val startDayOfWeek: String,
    @Schema(description = "세션 종료일", nullable = true)
    val endDate: LocalDate?,
    @Schema(description = "세션 종료 요일", nullable = true)
    val endDayOfWeek: String?,
    @Schema(
        description = """
            세션 시작일 기준 상대 날짜. D-N 혹은 D+N 으로 표시되는 값.
            ex) -2(D-2): 세션 시작일 기준 2일 전
            ex) 0(D-0, D+0): 세션 당일
            ex) +3(D+3): 세션 시작일 기준 3일 후
        """
    )
    val relativeDays: Int,
    @Schema(description = "세션 시작 시간", nullable = true)
    val time: LocalTime?,
    @Schema(description = "세션 종료 시간", nullable = true)
    val endTime: LocalTime?,
    @Schema(description = "세션 타입")
    val type: SessionType,
    @Schema(description = "세션 진행 상태", allowableValues = ["종료", "당일", "진행 중", "예정"])
    val progressPhase: String,
    @Schema(description = "출석 상태", nullable = true, allowableValues = ["출석", "지각", "결석", "조퇴", "공결"])
    val attendanceStatus: String?
) {

    @JsonIgnore
    val scheduleProgressPhase = ScheduleProgressPhase.entries.single { it.label == progressPhase }

    companion object {

        fun from(
            user: UserWithActivityUnits,
            session: SessionEntity,
            attendance: AttendanceEntity? = null,
            now: LocalDateTime
        ): SessionOverviewItemResponse =
            SessionOverviewItemResponse(
                id = session.id,
                name = session.name,
                place = session.place,
                generation = session.generation,
                date = session.date,
                startDayOfWeek = session.date.dayOfWeek.korean(),
                endDate = session.endDate,
                endDayOfWeek = session.endDate.dayOfWeek?.korean(),
                relativeDays = session.date.dDayFrom(now.toLocalDate()).toInt(),
                time = session.time,
                endTime = session.endTime,
                type = session.sessionType,
                progressPhase = session.getProgressPhase(now).label,
                attendanceStatus = AttendanceStatus.from(user, session, attendance, now)?.label
            )
    }
}
