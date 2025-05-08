package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.UserAttendanceStatistics
import io.swagger.v3.oas.annotations.media.Schema

data class AttendanceStatisticsResponse(
    @Schema(description = "전체 세션 수")
    val totalSessionCount: Int,
    @Schema(description = "남은 세션 수")
    val remainingSessionCount: Int,
    @Schema(description = "세션 진행률")
    val sessionProgressRate: Int,
    @Schema(description = "출석 점수")
    val attendancePoint: Int,
    @Schema(description = "출석한 세션 수")
    val attendanceCount: Int,
    @Schema(description = "지각한 세션 수")
    val lateCount: Int,
    @Schema(description = "결석한 세션 수")
    val absenceCount: Int,
    @Schema(description = "지각 면제권 수")
    val latePassCount: Int
) {

    companion object {
        fun from(userAttendanceStatistics: UserAttendanceStatistics): AttendanceStatisticsResponse =
            userAttendanceStatistics.let {
                AttendanceStatisticsResponse(
                    totalSessionCount = it.totalSessionCount,
                    remainingSessionCount = it.leftSessionCount,
                    sessionProgressRate = ((it.totalSessionCount - it.leftSessionCount) / it.totalSessionCount) * 100,
                    attendancePoint = it.totalPoint,
                    attendanceCount = it.onTimeCount,
                    lateCount = it.lateCount,
                    absenceCount = it.absentCount,
                    latePassCount = it.latePassCount
                )
            }
    }
}
