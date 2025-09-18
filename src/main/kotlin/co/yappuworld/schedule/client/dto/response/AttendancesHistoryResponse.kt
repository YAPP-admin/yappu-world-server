package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendanceDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

data class AttendancesHistoryResponse(
    @field:Schema(description = "이력 목록")
    val histories: List<AttendanceHistoryResponse>
) {

    companion object {
        fun of(sessions: List<SessionWithAttendanceDto>): AttendancesHistoryResponse =
            AttendancesHistoryResponse(sessions.map { AttendanceHistoryResponse(it) })
    }
}

data class AttendanceHistoryResponse(
    @field:Schema(description = "세션 식별자")
    val sessionId: UUID,
    @field:Schema(description = "세션 이름")
    val name: String,
    @field:Schema(description = "출석 시간", nullable = true)
    val checkedInAt: LocalDateTime?,
    @field:Schema(description = "출석 상태", nullable = true, allowableValues = ["출석", "지각", "결석", "조퇴", "공결"])
    val attendanceStatus: String?
) {

    constructor(session: SessionWithAttendanceDto) : this(
        sessionId = session.id,
        name = session.name,
        checkedInAt = session.checkedInAt,
        attendanceStatus = session.attendanceStatus
    )
}
