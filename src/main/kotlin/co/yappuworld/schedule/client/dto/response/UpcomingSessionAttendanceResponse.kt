package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.util.TimeUtils.korean
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.UUID

data class UpcomingSessionAttendanceResponse(
    @Schema(description = "세션 식별자")
    val sessionId: UUID,
    @Schema(description = "세션 이름")
    val name: String,
    @Schema(description = "세션 시작 일자")
    val startDate: LocalDate,
    @Schema(description = "세션 시작 요일")
    val startDayOfWeek: String,
    @Schema(description = "세션 종료 일자")
    val endDate: LocalDate,
    @Schema(description = "세션 종료 요일")
    val endDayOfWeek: String,
    @Schema(description = "세션 시작 시간")
    val startTime: LocalTime?,
    @Schema(description = "세션 종료 시간")
    val endTime: LocalTime?,
    @Schema(description = "장소")
    val place: String?,
    @Schema(
        description = """
            세션 시작일 기준 상대 날짜. D-N 혹은 D+N 으로 표시되는 값.
            ex) -2(D-2): 세션 시작일 기준 2일 전
            ex) 0(D-0, D+0): 세션 당일
            ex) +3(D+3): 세션 시작일 기준 3일 후
        """
    )
    val relativeDays: Int,
    @Schema(
        description = """
            현재 출석이 가능한지 여부
            출석이 가능한 조건
            1. 유저의 기수가 활성화 기수와 동일 & 유저의 권한이 ACTIVE(활동 중)
            2. 세션 시작 20분 전 ~ 세션 종료 시간
            3. 아직 출석하지 않은 경우
        """
    )
    val canCheckIn: Boolean,
    @Schema(description = "현재 출석 상태", allowableValues = ["출석", "지각", "결석", "조퇴", "공결"], nullable = true)
    val status: String?
) {

    companion object {

        fun of(
            user: UserWithLastActivityUnit,
            activeGeneration: Int,
            session: SessionEntity,
            attendance: AttendanceEntity?,
            now: LocalDateTime
        ): UpcomingSessionAttendanceResponse {
            val canCheckIn = now in session.checkInRange &&
                attendance == null &&
                user.lastActiveGeneration == activeGeneration &&
                user.role == UserRole.ACTIVE

            return UpcomingSessionAttendanceResponse(
                sessionId = session.id,
                name = session.name,
                startDate = session.date,
                startDayOfWeek = session.date.dayOfWeek.korean(),
                endDate = session.endDate,
                endDayOfWeek = session.endDate.dayOfWeek.korean(),
                startTime = session.time,
                endTime = session.endTime,
                place = session.place,
                relativeDays = ChronoUnit.DAYS.between(session.date, now.toLocalDate()).toInt(),
                canCheckIn = canCheckIn,
                status = attendance?.status?.label
            )
        }
    }
}
