package co.yappuworld.operation.presentation.dto.request

import co.yappuworld.operation.domain.Generation
import java.time.LocalDate

data class AdminGenerationRegisterAppRequestDto(
    val generation: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val isActive: Boolean
) {

    fun toDomain(): Generation =
        Generation(
            value = generation,
            startDate = startDate,
            endDate = endDate,
            isActive = isActive
        )
}
