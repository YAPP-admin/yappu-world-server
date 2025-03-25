package co.yappuworld.support.fixture.operation

import co.yappuworld.operation.domain.Generation
import java.time.LocalDate

object OperationFixture {

    fun getGenerationFixture(
        value: Int = 2,
        startDate: LocalDate? = LocalDate.of(2025, 11, 13),
        endDate: LocalDate? = LocalDate.of(2025, 3, 8),
        isActive: Boolean = false
    ): Generation {
        val generation = Generation(
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
