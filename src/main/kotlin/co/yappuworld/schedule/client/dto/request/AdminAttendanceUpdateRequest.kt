package co.yappuworld.schedule.client.dto.request

import co.yappuworld.schedule.domain.AttendanceStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminAttendanceUpdateRequest(
    @Schema(description = "업데이트 목록")
    val targets: List<AdminAttendanceUpdateTargetRequest>
) {

    @JsonIgnore
    val size = targets.size

    @JsonIgnore
    fun getSessionAndUserIdPairs(): List<Pair<UUID, UUID>> = targets.map { it.sessionId to it.userId }
}

data class AdminAttendanceUpdateTargetRequest(
    @Schema(description = "유저 ID")
    val userId: UUID,
    @Schema(description = "세션 ID")
    val sessionId: UUID,
    @Schema(description = "업데이트 상태")
    val attendanceStatus: AttendanceStatus
)
