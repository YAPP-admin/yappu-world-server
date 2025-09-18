package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.UserAttendanceStatistics
import io.swagger.v3.oas.annotations.media.Schema
import kotlin.math.roundToInt

data class AttendanceStatisticsResponseV2(
    @field:Schema(description = "전체 세션 수")
    val totalSessionCount: Int,
    @field:Schema(description = "남은 세션 수")
    val remainingSessionCount: Int,
    @field:Schema(description = "세션 진행률 (소수 첫째자리까지 표현)")
    val sessionProgressRate: Double,
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
        fun from(userAttendanceStatistics: UserAttendanceStatistics): AttendanceStatisticsResponseV2 =
            userAttendanceStatistics.let {
                val sessionProgressRatio = 1.0 - it.leftSessionCount.toDouble() / it.totalSessionCount
                val sessionProgressRate = (sessionProgressRatio * 100 * 10).roundToInt() / 10.0

                AttendanceStatisticsResponseV2(
                    totalSessionCount = it.totalSessionCount,
                    remainingSessionCount = it.leftSessionCount,
                    sessionProgressRate = sessionProgressRate,
                    attendancePoint = it.totalPoint,
                    attendanceCount = it.onTimeCount,
                    lateCount = it.lateCount,
                    absenceCount = it.absentCount,
                    latePassCount = it.latePassCount
                )
            }
    }
}
