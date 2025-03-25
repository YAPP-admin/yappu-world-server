package co.yappuworld.support.fixture.operation

import co.yappuworld.operation.domain.GenerationEntity
import java.time.LocalDate

object OperationFixture {

    fun getGenerationFixture(
        value: Int = 2,
        startDate: LocalDate? = LocalDate.of(2025, 11, 13),
        endDate: LocalDate? = LocalDate.of(2025, 3, 8),
        isActive: Boolean = false
    ): GenerationEntity {
        val generation = GenerationEntity(
            value = value,
            startDate = startDate,
            endDate = endDate
        )

        if (isActive) {
            generation.activate()
        }

        return generation
    }
}
