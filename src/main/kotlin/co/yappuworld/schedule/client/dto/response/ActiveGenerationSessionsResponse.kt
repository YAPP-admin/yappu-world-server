package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.util.DatetimeUtils.korean
import co.yappuworld.schedule.domain.vo.SessionProgressPhase
import co.yappuworld.schedule.domain.vo.SessionProgressPhase.DONE
import co.yappuworld.schedule.domain.vo.SessionProgressPhase.PENDING
import co.yappuworld.schedule.domain.vo.SessionProgressPhase.TODAY
import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendanceDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.UUID

data class ActiveGenerationSessionsResponse(
    @field:Schema(description = "활동 중인 기수의 세션 목록, 데이터가 없다면 빈 리스트 반환")
    val sessions: List<ActiveGenerationSessionResponse>,
    @field:Schema(
        description = """
            가장 가까이 예정된 세션의 인덱스
            모든 세션이 종료됐다면 마지막 인덱스 반환
            빈 리스트인 경우 null
        """,
        nullable = true
    )
    val upcomingSessionId: UUID? = null
) {

    companion object {
        fun from(
            sessions: List<SessionWithAttendanceDto>,
            now: LocalDateTime
        ): ActiveGenerationSessionsResponse {
            if (sessions.isEmpty()) return ActiveGenerationSessionsResponse(emptyList(), null)

            val orderedSessions = sessions.sortedWith(compareBy({ it.date }, { it.time }))
            val (upcomingSessionIndex, upcomingSessionStatus) = getUpcomingSessionIndexAndStatus(orderedSessions, now)
                ?: return ActiveGenerationSessionsResponse(
                    orderedSessions.map { ActiveGenerationSessionResponse(it, DONE, now) },
                    orderedSessions.last().id
                )

            return ActiveGenerationSessionsResponse(
                sessions = orderedSessions.mapIndexed { index, session ->
                    when {
                        index < upcomingSessionIndex -> ActiveGenerationSessionResponse(session, DONE, now)
                        index > upcomingSessionIndex -> ActiveGenerationSessionResponse(session, PENDING, now)
                        else -> ActiveGenerationSessionResponse(session, upcomingSessionStatus, now)
                    }
                },
                upcomingSessionId = orderedSessions[upcomingSessionIndex].id
            )
        }

        private fun getUpcomingSessionIndexAndStatus(
            orderedSessions: List<SessionWithAttendanceDto>,
            now: LocalDateTime
        ): Pair<Int, SessionProgressPhase>? {
            val upcomingSession = orderedSessions.firstOrNull { !it.isFinished(now) }
                ?: return null
            val upcomingSessionIndex = orderedSessions.indexOf(upcomingSession)

            return when {
                upcomingSession.isToday(now) -> upcomingSessionIndex to TODAY
                else -> upcomingSessionIndex to PENDING
            }
        }
    }
}

data class ActiveGenerationSessionResponse(
    @field:Schema(description = "세션 식별자")
    val id: UUID,
    @field:Schema(description = "세션 이름")
    val name: String,
    @field:Schema(description = "세션 장소", nullable = true)
    val place: String?,
    @field:Schema(description = "주소", example = "서울특별시 종로구 종로 33")
    val address: String?,
    @field:Schema(description = "경도", example = "126.981437983842")
    val longitude: Double?,
    @field:Schema(description = "위도", example = "37.5720065838703")
    val latitude: Double?,
    @field:Schema(description = "세션 시작일")
    val date: LocalDate,
    @field:Schema(description = "세션 시작 요일")
    val startDayOfWeek: String,
    @field:Schema(description = "세션 종료일", nullable = true)
    val endDate: LocalDate?,
    @field:Schema(description = "세션 종료 요일", nullable = true)
    val endDayOfWeek: String?,
    @field:Schema(
        description = """
            세션 시작일 기준 상대 날짜. D-N 혹은 D+N 으로 표시되는 값.
            ex) -2(D-2): 세션 시작일 기준 2일 전
            ex) 0(D-0, D+0): 세션 당일
            ex) +3(D+3): 세션 시작일 기준 3일 후
        """
    )
    val relativeDays: Int,
    @field:Schema(description = "세션 시작 시간", nullable = true)
    val time: LocalTime?,
    @field:Schema(description = "세션 종료 시간", nullable = true)
    val endTime: LocalTime?,
    @field:Schema(description = "세션 타입")
    val type: SessionType,
    @field:Schema(description = "세션 진행 상태")
    val progressPhase: SessionProgressPhase,
    @field:Schema(description = "출석 상태", nullable = true, allowableValues = ["출석", "지각", "결석", "조퇴", "공결"])
    val attendanceStatus: String?
) {

    constructor(
        session: SessionWithAttendanceDto,
        status: SessionProgressPhase,
        now: LocalDateTime
    ) : this(
        id = session.id,
        name = session.name,
        place = session.place,
        address = session.address,
        latitude = session.latitude,
        longitude = session.longitude,
        date = session.date,
        startDayOfWeek = session.date.dayOfWeek.korean(),
        endDate = session.endDate,
        endDayOfWeek = session.endDate.dayOfWeek?.korean(),
        relativeDays = ChronoUnit.DAYS.between(session.date, now.toLocalDate()).toInt(),
        time = session.time,
        endTime = session.endTime,
        type = session.sessionType,
        progressPhase = status,
        attendanceStatus = session.attendanceStatus
    )
}
