package co.yappuworld.operation.application.dto.response

import co.yappuworld.operation.domain.Generation
import org.springframework.data.domain.Page
import java.time.LocalDate

data class AdminGenerationsAppParamDto(
    val generations: List<AdminGenerationAppResponseDto>,
    val totalElements: Long,
    val totalPages: Int
) {

    constructor(response: Page<Generation>) : this(
        generations = response.content.map { AdminGenerationAppResponseDto(it) },
        totalElements = response.totalElements,
        totalPages = response.totalPages
    )
}

data class AdminGenerationAppResponseDto(
    val generation: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val isActive: Boolean
) {

    constructor(generation: Generation) : this(
        generation = generation.value,
        startDate = generation.startDate,
        endDate = generation.endDate,
        isActive = generation.isActive
    )
}
