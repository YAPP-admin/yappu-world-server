package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.UserAttendanceStatistics
import io.swagger.v3.oas.annotations.media.Schema

data class AttendanceStatisticsResponse(
    @field:Schema(description = "전체 세션 수")
    val totalSessionCount: Int,
    @field:Schema(description = "남은 세션 수")
    val remainingSessionCount: Int,
    @field:Schema(description = "세션 진행률 (소수 첫째자리까지 표현)")
    val sessionProgressRate: Int,
    @field:Schema(description = "출석 점수")
    val attendancePoint: Int,
    @field:Schema(description = "출석한 세션 수")
    val attendanceCount: Int,
    @field:Schema(description = "지각한 세션 수")
    val lateCount: Int,
    @field:Schema(description = "결석한 세션 수")
    val absenceCount: Int,
    @field:Schema(description = "지각 면제권 수")
    val latePassCount: Int
) {

    companion object {
        fun from(userAttendanceStatistics: UserAttendanceStatistics): AttendanceStatisticsResponse =
            userAttendanceStatistics.let {
                AttendanceStatisticsResponse(
                    totalSessionCount = it.totalSessionCount,
                    remainingSessionCount = it.leftSessionCount,
                    sessionProgressRate = ((1.0 - it.leftSessionCount.toDouble() / it.totalSessionCount) * 100).toInt(),
                    attendancePoint = it.totalPoint,
                    attendanceCount = it.onTimeCount,
                    lateCount = it.lateCount,
                    absenceCount = it.absentCount,
                    latePassCount = it.latePassCount
                )
            }
    }
}
