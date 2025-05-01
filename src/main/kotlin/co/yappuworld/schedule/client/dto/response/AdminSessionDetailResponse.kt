package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.entity.SessionEntity
import co.yappuworld.schedule.domain.SessionType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionDetailResponse(
    @Schema(description = "세션 ID")
    val id: UUID,
    @Schema(description = "세션 이름")
    val name: String,
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "세션 장소")
    val place: String?,
    @Schema(description = "세션 시작 일자")
    val date: LocalDate,
    @Schema(description = "세션 종료 일자")
    val endDate: LocalDate,
    @Schema(description = "세션 시작 시간")
    val time: LocalTime,
    @Schema(description = "세션 종료 시간")
    val endTime: LocalTime,
    @Schema(description = "세션 타입")
    val sessionType: SessionType
) {

    constructor(session: SessionEntity) : this(
        id = session.id,
        name = session.name,
        generation = session.generation,
        place = session.place,
        date = session.date,
        endDate = session.endDate,
        time = session.time,
        endTime = session.endTime,
        sessionType = session.sessionType
    )
}
