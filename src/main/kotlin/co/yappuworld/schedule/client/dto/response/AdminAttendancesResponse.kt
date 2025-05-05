package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.util.DatetimeUtils.korean
import co.yappuworld.schedule.domain.AttendanceBook
import co.yappuworld.schedule.domain.SessionAttendanceStatistics
import co.yappuworld.schedule.domain.UserAttendanceStatistics
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ABSENT
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class AdminAttendancesResponse(
    @Schema(description = "세션 목록")
    val sessions: List<AdminAttendanceSessionResponse>,
    @Schema(description = "유저 목록")
    val users: List<AdminAttendanceUserResponse>,
    @Schema(description = "세션별 출석 정보")
    val attendancesGroupedBySession: List<AdminSessionAttendanceGroupResponse>
) {

    companion object {

        fun from(attendanceBook: AttendanceBook): AdminAttendancesResponse =
            AdminAttendancesResponse(
                sessions = attendanceBook.sessions.map {
                    AdminAttendanceSessionResponse(it, attendanceBook.sessionAttendanceStatistics(it.id))
                },
                users = attendanceBook.users.map {
                    AdminAttendanceUserResponse(it, attendanceBook.userAttendanceStatistics(it.userId))
                },
                attendancesGroupedBySession = attendanceBook.sessions.map { session ->
                    AdminSessionAttendanceGroupResponse.from(
                        sessionId = session.id,
                        attendanceByUserId = attendanceBook.getSessionStatuses(session.id)
                    )
                }
            )
    }
}

data class AdminAttendanceSessionResponse(
    @Schema(description = "세션 ID")
    val sessionId: UUID,
    @Schema(description = "세션 이름")
    val name: String,
    @Schema(description = "세션 시작일")
    val startDate: LocalDate,
    @Schema(description = "세션 시작 요일")
    val startDayOfWeek: String,
    @Schema(description = "세션 종료일")
    val endDate: LocalDate,
    @Schema(description = "세션 종료 요일")
    val endDayOfWeek: String,
    @Schema(description = "세션 시작 시간")
    val startTime: LocalTime,
    @Schema(description = "세션 종료 시간")
    val endTime: LocalTime,
    @Schema(description = "총 인원")
    val totalPersonCount: Int,
    @Schema(description = "출석 인원")
    val totalOnTimeCount: Int,
    @Schema(description = "지각 인원")
    val totalLateCount: Int,
    @Schema(description = "결석 인원")
    val totalAbsentCount: Int,
    @Schema(description = "조퇴 인원")
    val totalEarlyCheckOutCount: Int,
    @Schema(description = "공결 인원")
    val totalExcusedAbsenceCount: Int
) {

    constructor(session: SessionEntity, statistics: SessionAttendanceStatistics) : this(
        sessionId = session.id,
        name = session.name,
        startDate = session.date,
        startDayOfWeek = session.date.dayOfWeek.korean(),
        endDate = session.endDate,
        endDayOfWeek = session.endDate.dayOfWeek.korean(),
        startTime = session.time,
        endTime = session.endTime,
        totalPersonCount = statistics.totalPersonCount,
        totalOnTimeCount = statistics.totalOnTimeCount,
        totalLateCount = statistics.totalLateCount,
        totalAbsentCount = statistics.totalAbsentCount,
        totalEarlyCheckOutCount = statistics.totalEarlyCheckOutCount,
        totalExcusedAbsenceCount = statistics.totalExcusedAbsenceCount
    )
}

data class AdminAttendanceUserResponse(
    @Schema(description = "유저 ID")
    val userId: UUID,
    @Schema(description = "이름")
    val name: String,
    @Schema(description = "직군", allowableValues = ["PM", "Design", "Web", "Android", "iOS", "Flutter", "Server", "운영진"])
    val position: String,
    @Schema(description = "출석 횟수")
    val onTimeCount: Int,
    @Schema(description = "지각 횟수")
    val lateCount: Int,
    @Schema(description = "결석 횟수")
    val absentCount: Int,
    @Schema(description = "조퇴 횟수")
    val earlyCheckOutCount: Int,
    @Schema(description = "공결 횟수")
    val excusedAbsenceCount: Int,
    @Schema(description = "지각 면제권 개수")
    val latePassCount: Int,
    @Schema(description = "총점, 100점이 최대")
    val totalPoint: Int,
    @Schema(description = "감점")
    val penaltyPoint: Int,
    @Schema(description = "가점")
    val bonusPoint: Int
) {

    constructor(
        user: UserWithActivityUnit,
        statistics: UserAttendanceStatistics
    ) : this(
        userId = user.userId,
        name = user.name,
        position = user.position.name,
        onTimeCount = statistics.onTimeCount,
        lateCount = statistics.lateCount,
        absentCount = statistics.absentCount,
        earlyCheckOutCount = statistics.earlyCheckOutCount,
        excusedAbsenceCount = statistics.excusedAbsenceCount,
        latePassCount = statistics.latePassCount,
        totalPoint = statistics.totalPoint,
        penaltyPoint = statistics.penaltyPoint,
        bonusPoint = statistics.bonusPoint
    )
}

data class AdminSessionAttendanceGroupResponse(
    @Schema(description = "세션 ID")
    val sessionId: UUID,
    @Schema(description = "유저별 출석 정보")
    val attendances: List<AdminUserAttendanceResponse>
) {

    companion object {

        fun from(
            session: SessionEntity,
            users: List<UserWithActivityUnit>,
            attendances: List<AttendanceEntity>,
            now: LocalDateTime
        ): AdminSessionAttendanceGroupResponse {
            val attendancesInSessionByUserId = attendances
                .filter { it.scheduleId == session.id }
                .associateBy { it.userId }
            val statusWithoutAttendanceData = if (session.isFinished(now)) ABSENT.label else null

            return AdminSessionAttendanceGroupResponse(
                sessionId = session.id,
                attendances = users.map { user ->
                    AdminUserAttendanceResponse(
                        userId = user.userId,
                        status = attendancesInSessionByUserId[user.userId]?.status?.label
                            ?: statusWithoutAttendanceData
                    )
                }
            )
        }

        fun from(
            sessionId: UUID,
            attendanceByUserId: Map<UUID, AttendanceStatus?>
        ): AdminSessionAttendanceGroupResponse =
            AdminSessionAttendanceGroupResponse(
                sessionId = sessionId,
                attendances = attendanceByUserId.map { (userId, status) ->
                    AdminUserAttendanceResponse(
                        userId = userId,
                        status = status?.label
                    )
                }
            )
    }
}

data class AdminUserAttendanceResponse(
    @Schema(description = "유저 ID")
    val userId: UUID,
    @Schema(description = "출석 정보", allowableValues = ["출석", "지각", "결석", "조퇴", "공결"], nullable = true)
    val status: String? = null
)
