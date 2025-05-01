package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.util.TimeUtils.korean
import co.yappuworld.schedule.domain.entity.AttendanceEntity
import co.yappuworld.schedule.domain.AttendanceStatus.ABSENT
import co.yappuworld.schedule.domain.entity.SessionEntity
import co.yappuworld.user.domain.vo.Position
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

        fun from(
            sessions: List<SessionEntity>,
            users: List<UserWithActivityUnit>,
            attendances: List<AttendanceEntity>,
            now: LocalDateTime
        ): AdminAttendancesResponse {
            val sortedSessions = sessions.sortedBy { it.date }

            return AdminAttendancesResponse(
                sessions = sortedSessions.map { AdminAttendanceSessionResponse(it) },
                users = users
                    .map { AdminAttendanceUserResponse(it) }
                    .sortedBy { Position.valueOf(it.position).ordinal },
                attendancesGroupedBySession = sortedSessions.map { session ->
                    AdminSessionAttendanceGroupResponse.from(
                        session = session,
                        users = users,
                        attendances = attendances,
                        now = now
                    )
                }
            )
        }
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
    val endTime: LocalTime
) {

    constructor(session: SessionEntity) : this(
        sessionId = session.id,
        name = session.name,
        startDate = session.date,
        startDayOfWeek = session.date.dayOfWeek.korean(),
        endDate = session.endDate,
        endDayOfWeek = session.endDate.dayOfWeek.korean(),
        startTime = session.time,
        endTime = session.endTime
    )
}

data class AdminAttendanceUserResponse(
    @Schema(description = "유저 ID")
    val userId: UUID,
    @Schema(description = "이름")
    val name: String,
    @Schema(description = "직군", allowableValues = ["PM", "Design", "Web", "Android", "iOS", "Flutter", "Server", "운영진"])
    val position: String
) {

    constructor(user: UserWithActivityUnit) : this(
        userId = user.userId,
        name = user.name,
        position = user.position.name
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
    }
}

data class AdminUserAttendanceResponse(
    @Schema(description = "유저 ID")
    val userId: UUID,
    @Schema(description = "출석 정보", allowableValues = ["출석", "지각", "결석", "조퇴", "공결"], nullable = true)
    val status: String? = null
)
