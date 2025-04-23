package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.AttendanceEntity
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class UpcomingSessionAttendanceResponse(
    @Schema(description = "세션 식별자")
    val sessionId: UUID,
    @Schema(description = "세션 이름")
    val name: String,
    @Schema(description = "세션 일자")
    val date: LocalDate,
    @Schema(description = "세션 시간")
    val time: LocalTime?,
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
                user.generation == activeGeneration &&
                user.role == UserRole.ACTIVE

            return UpcomingSessionAttendanceResponse(
                sessionId = session.id,
                name = session.name,
                date = session.date,
                time = session.time,
                canCheckIn = canCheckIn,
                status = attendance?.status?.label
            )
        }
    }
}
