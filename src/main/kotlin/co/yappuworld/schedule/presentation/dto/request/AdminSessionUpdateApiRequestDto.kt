package co.yappuworld.schedule.presentation.dto.request

import co.yappuworld.schedule.application.dto.request.AdminSessionUpdateAppRequestDto
import co.yappuworld.schedule.domain.SessionType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionUpdateApiRequestDto(
    @Schema(description = "세션 ID")
    val id: UUID,
    @Schema(description = "세션 이름")
    val name: String,
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "장소")
    val place: String?,
    @Schema(description = "세션 날짜")
    val date: LocalDate,
    @Schema(description = "시작 시간")
    val time: LocalTime?,
    @Schema(description = "종료 시간")
    val endTime: LocalTime?,
    @Schema(description = "세션 종류")
    val sessionType: SessionType
) {

    fun toAppRequest(): AdminSessionUpdateAppRequestDto =
        AdminSessionUpdateAppRequestDto(
            id = id,
            name = name,
            generation = generation,
            place = place,
            date = date,
            time = time,
            endTime = endTime,
            sessionType = sessionType
        )
}
