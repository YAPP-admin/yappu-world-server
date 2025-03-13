package co.yappuworld.schedule.presentation.dto.response

import co.yappuworld.schedule.application.dto.response.AdminSessionOverviewAppResponseDto
import co.yappuworld.schedule.domain.SessionType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionOverviewApiResponseDto(
    @Schema(description = "세션 ID")
    val id: UUID,
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "세션 종류")
    val type: SessionType,
    @Schema(description = "이름")
    val title: String,
    @Schema(description = "장소")
    val place: String?,
    @Schema(description = "날짜")
    val date: LocalDate,
    @Schema(description = "시작 시간")
    val time: LocalTime?,
    @Schema(description = "종료 시간")
    val endTime: LocalTime?
) {

    constructor(response: AdminSessionOverviewAppResponseDto) : this(
        id = response.id,
        generation = response.generation,
        type = response.type,
        title = response.name,
        place = response.place,
        date = response.date,
        time = response.time,
        endTime = response.endTime
    )
}
