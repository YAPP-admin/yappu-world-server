package co.yappuworld.schedule.client.dto.request

import co.yappuworld.schedule.domain.vo.AttendanceStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminAttendanceUpdateRequest(
    @param:Schema(description = "기수")
    val generation: Int,
    @param:Schema(description = "업데이트 목록")
    val attendances: List<AdminAttendanceUpdateTargetRequest>,
    @param:Schema(description = "지각 면제권 업데이트 목록")
    val latePasses: List<AdminLatePassUpdateTargetRequest>
) {

    @JsonIgnore
    fun getSessionAndUserIdPairs(): List<Pair<UUID, UUID>> = attendances.map { it.sessionId to it.userId }
}

data class AdminAttendanceUpdateTargetRequest(
    @param:Schema(description = "유저 ID")
    val userId: UUID,
    @param:Schema(description = "세션 ID")
    val sessionId: UUID,
    @param:Schema(description = "업데이트 상태")
    val attendanceStatus: AttendanceStatus
)

data class AdminLatePassUpdateTargetRequest(
    @param:Schema(description = "유저 ID")
    val userId: UUID,
    @param:Schema(description = "지각 면제권 개수")
    val latePassCount: Int
)
