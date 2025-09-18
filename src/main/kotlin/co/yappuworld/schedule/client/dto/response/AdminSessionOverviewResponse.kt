package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.schedule.domain.vo.SessionType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionOverviewResponse(
    @field:Schema(description = "세션 ID")
    val id: UUID,
    @field:Schema(description = "기수")
    val generation: Int,
    @field:Schema(description = "세션 종류")
    val type: SessionType,
    @field:Schema(description = "이름")
    val title: String,
    @field:Schema(description = "장소")
    val place: String?,
    @field:Schema(description = "날짜")
    val date: LocalDate,
    @field:Schema(description = "종료 날짜")
    val endDate: LocalDate,
    @field:Schema(description = "시작 시간")
    val time: LocalTime,
    @field:Schema(description = "종료 시간")
    val endTime: LocalTime?
) {

    constructor(session: SessionEntity) : this(
        id = session.id,
        generation = session.generation,
        type = session.sessionType,
        title = session.name,
        place = session.place,
        date = session.date,
        endDate = session.endDate,
        time = session.time,
        endTime = session.endTime
    )
}
