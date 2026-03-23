package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.SessionProgressPhase
import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.dto.UserSessionAttendance
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

data class AttendancesHistoryResponseV2(
    @field:Schema(description = "세션 출석 이력 목록")
    val histories: List<AttendanceHistoryResponseV2>
) {

    companion object {
        fun of(
            sessions: List<UserSessionAttendance>,
            now: LocalDateTime
        ): AttendancesHistoryResponseV2 =
            AttendancesHistoryResponseV2(
                histories = sessions
                    .sortedWith(compareBy({ it.date }, { it.time }, { it.endDate }, { it.endTime }, { it.id }))
                    .map { AttendanceHistoryResponseV2(it, now) }
            )
    }
}

data class AttendanceHistoryResponseV2(
    @field:Schema(description = "세션 식별자")
    val sessionId: UUID,
    @field:Schema(description = "세션 제목")
    val title: String,
    @field:Schema(description = "세션 종류")
    val sessionType: SessionType,
    @field:Schema(description = "세션 시작 일시")
    val startAt: LocalDateTime,
    @field:Schema(description = "세션 종료 일시")
    val endAt: LocalDateTime,
    @field:Schema(description = "출석 시간", nullable = true)
    val checkedInAt: LocalDateTime?,
    @field:Schema(
        description = "출석 상태",
        nullable = true,
        allowableValues = ["PENDING", "ON_TIME", "LATE", "ABSENT", "EARLY_CHECK_OUT", "EXCUSED_ABSENCE"]
    )
    val attendanceStatus: AttendanceStatus?,
    @field:Schema(description = "세션 진행 상태")
    val progressPhase: SessionProgressPhase
) {

    constructor(session: UserSessionAttendance, now: LocalDateTime) : this(
        sessionId = session.id,
        title = session.name,
        sessionType = session.sessionType,
        startAt = LocalDateTime.of(session.date, session.time),
        endAt = LocalDateTime.of(session.endDate, session.endTime),
        checkedInAt = session.checkedInAt,
        attendanceStatus = session.resolveAttendanceStatus(now),
        progressPhase = session.getSessionProgressPhase(now)
    )
}
