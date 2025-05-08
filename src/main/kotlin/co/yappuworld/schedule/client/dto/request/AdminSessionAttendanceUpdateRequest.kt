package co.yappuworld.schedule.client.dto.request

import co.yappuworld.schedule.domain.vo.AttendanceStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminSessionAttendanceUpdateRequest(
    @Schema(description = "일괄 업데이트 대상 세션 ID")
    val sessionId: UUID,
    @Schema(description = "업데이트 상태")
    val attendanceStatus: AttendanceStatus
)
