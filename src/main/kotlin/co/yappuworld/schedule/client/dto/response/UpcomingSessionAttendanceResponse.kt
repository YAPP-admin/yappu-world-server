package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.SessionAttendance
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
            1. 세션 참가자로 초대를 받았고
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
            sessionAttendance: SessionAttendance,
            now: LocalDateTime
        ): UpcomingSessionAttendanceResponse =
            sessionAttendance.let {
                UpcomingSessionAttendanceResponse(
                    sessionId = it.sessionId,
                    name = it.sessionName,
                    startDate = it.sessionStartDate,
                    startDayOfWeek = it.sessionStartDayOfWeek,
                    endDate = it.sessionEndDate,
                    endDayOfWeek = it.sessionEndDayOfWeek,
                    startTime = it.sessionStartTime,
                    endTime = it.sessionEndTime,
                    place = it.sessionPlace,
                    relativeDays = it.getRelativeDays(now.toLocalDate()),
                    canCheckIn = it.canCheckIn(now),
                    status = it.getAttendanceStatus(now)
                )
            }
    }
}
