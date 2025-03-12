package co.yappuworld.schedule.presentation.dto.response

import co.yappuworld.schedule.application.dto.response.SessionAppResponseDto
import co.yappuworld.schedule.application.dto.response.SessionsAppResponseDto
import co.yappuworld.schedule.domain.SessionProgressPhase
import co.yappuworld.schedule.domain.SessionType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class ActiveGenerationSessionsApiResponseDto(
    @Schema(description = "활동 중인 기수의 세션 목록, 데이터가 없다면 빈 리스트 반환")
    val sessions: List<ActiveGenerationSessionApiResponseDto>,
    @Schema(
        description = """
            가장 가까이 예정된 세션의 인덱스
            모든 세션이 종료됐다면 마지막 인덱스 반환
            빈 리스트인 경우 null
        """,
        nullable = true
    )
    val upcomingSessionIndex: Int? = null
) {
    constructor(response: SessionsAppResponseDto) : this(
        sessions = response.sessions.map { ActiveGenerationSessionApiResponseDto(it) },
        upcomingSessionIndex = response.upcomingSessionIndex
    )
}

data class ActiveGenerationSessionApiResponseDto(
    val id: UUID,
    val name: String,
    val place: String?,
    val date: LocalDate,
    val endDate: LocalDate?,
    val time: LocalTime?,
    val endTime: LocalTime?,
    val type: SessionType,
    val progressPhase: SessionProgressPhase
) {
    constructor(session: SessionAppResponseDto) : this(
        id = session.id,
        name = session.name,
        place = session.place,
        date = session.date,
        endDate = session.endDate,
        time = session.time,
        endTime = session.endTime,
        type = session.type,
        progressPhase = session.progressPhase
    )
}
