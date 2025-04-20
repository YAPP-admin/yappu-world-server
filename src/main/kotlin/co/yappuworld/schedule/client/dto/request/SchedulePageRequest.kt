package co.yappuworld.schedule.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.LocalDate

data class SchedulePageRequest(
    @field:Schema(description = "년도", required = true)
    val year: Int,
    @field:Schema(description = "월", required = true)
    @field:Min(value = 1L, message = "월은 1월부터 12월까지입니다.")
    @field:Max(value = 12L, message = "월은 1월부터 12월까지입니다.")
    val month: Int
) {

    val from: LocalDate = LocalDate.of(year, month, 1)
    val toInclusive: LocalDate = from.plusMonths(1).minusDays(1)
}
