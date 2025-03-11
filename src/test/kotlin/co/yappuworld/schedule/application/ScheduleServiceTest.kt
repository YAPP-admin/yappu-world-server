package co.yappuworld.schedule.application

import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionLifecycleStatus.DONE
import co.yappuworld.schedule.domain.SessionLifecycleStatus.PENDING
import co.yappuworld.schedule.domain.SessionLifecycleStatus.TODAY
import co.yappuworld.schedule.domain.SessionLifecycleStatus.UPCOMING
import co.yappuworld.schedule.infrastructure.repository.ScheduleJpaRepository
import co.yappuworld.support.fixture.operation.OperationFixture
import co.yappuworld.support.fixture.schedule.ScheduleFixture
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class ScheduleServiceTest {

    private val generationRepository = mockk<GenerationRepository>()
    private val scheduleRepository = mockk<ScheduleJpaRepository>()
    private val scheduleService = ScheduleService(generationRepository, scheduleRepository)

    private val generation = 2
    private val now = LocalDate.of(2022, 5, 5)

    @BeforeEach
    fun mockGenerationRepository() {
        every { generationRepository.getGenerationOrNullByIsActiveIsTrue() } returns
            OperationFixture.getGenerationFixture(
                value = generation
            )
    }

    fun mockScheduleRepository(schedules: List<SessionEntity>) {
        every { scheduleRepository.findSessionEntitiesByGeneration(generation) } returns schedules
    }

    @Test
    fun `지난 건 DONE, 오늘 건 TODAY, 남은 건 PENDING이다`() {
        mockScheduleRepository(
            listOf(
                ScheduleFixture.getSessionFixture(date = now.minusDays(1)),
                ScheduleFixture.getSessionFixture(date = now),
                ScheduleFixture.getSessionFixture(date = now.plusDays(3))
            )
        )

        val result = scheduleService.getCurrentGenerationSessions(now)
        assertEquals(result.sessions[0].status, DONE)
        assertEquals(result.sessions[1].status, TODAY)
        assertEquals(result.sessions[2].status, PENDING)
        assertEquals(result.upcomingSessionIndex, 1)
    }

    @Test
    fun `지난 건 DONE, 임박한 건 UPCOMING, 남은 건 PENDING이다`() {
        mockScheduleRepository(
            listOf(
                ScheduleFixture.getSessionFixture(date = now.minusDays(2)),
                ScheduleFixture.getSessionFixture(date = now.minusDays(1)),
                ScheduleFixture.getSessionFixture(date = now.plusDays(1)),
                ScheduleFixture.getSessionFixture(date = now.plusDays(3))
            )
        )

        val result = scheduleService.getCurrentGenerationSessions(now)
        assertEquals(result.sessions[0].status, DONE)
        assertEquals(result.sessions[1].status, DONE)
        assertEquals(result.sessions[2].status, UPCOMING)
        assertEquals(result.sessions[3].status, PENDING)
        assertEquals(result.upcomingSessionIndex, 2)
    }
}
