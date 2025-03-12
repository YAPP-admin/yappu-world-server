package co.yappuworld.schedule.application.dto.request

import java.time.LocalDate

data class SchedulePageAppRequestDto(
    val year: Int,
    val month: Int
) {

    val from: LocalDate = LocalDate.of(year, month, 1)
    val to: LocalDate = from.plusMonths(1).minusDays(1)
}
