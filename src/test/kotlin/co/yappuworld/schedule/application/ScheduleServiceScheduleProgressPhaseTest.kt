package co.yappuworld.schedule.application

import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.schedule.client.application.ScheduleService
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.domain.ScheduleEntity
import co.yappuworld.schedule.infrastructure.repository.ScheduleJpaRepository
import co.yappuworld.support.fixture.schedule.ScheduleFixture.getSessionFixture
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class ScheduleServiceScheduleProgressPhaseTest {

    private val generationRepository = mockk<GenerationRepository>()
    private val scheduleRepository = mockk<ScheduleJpaRepository>()
    private val scheduleService = ScheduleService(generationRepository, scheduleRepository)

    fun mockScheduleRepository(vararg schedules: ScheduleEntity) {
        every { scheduleRepository.findScheduleEntitiesByDateBetween(any(), any()) } returns schedules.toList()
    }

    @Test
    fun `같은 날 2개의 데이터가 있으면 해당 일자 schedules가 하나의 배열로 묶인다`() {
        val targetDate = LocalDate.of(2021, 5, 5)
        mockScheduleRepository(
            getSessionFixture(date = LocalDate.of(2021, 5, 4)),
            getSessionFixture(date = targetDate),
            getSessionFixture(date = targetDate)
        )

        val result = scheduleService.getSchedules(
            request = SchedulePageRequest(2021, 5),
            now = targetDate.atStartOfDay()
        )

        val size = result.dates
            .single { it.date == targetDate }
            .schedules
            .size

        assertEquals(size, 2)
    }
}
