package co.yappuworld.schedule.presentation.dto.request

import co.yappuworld.schedule.application.dto.request.SchedulePageAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class SchedulePageApiRequestDto(
    @field:Schema(description = "년도", required = true)
    val year: Int,
    @field:Schema(description = "월", required = true)
    @field:Min(value = 1L, message = "월은 1월부터 12월까지입니다.")
    @field:Max(value = 12L, message = "월은 1월부터 12월까지입니다.")
    val month: Int
) {

    fun toAppRequestDto() =
        SchedulePageAppRequestDto(
            year = year,
            month = month
        )
}
