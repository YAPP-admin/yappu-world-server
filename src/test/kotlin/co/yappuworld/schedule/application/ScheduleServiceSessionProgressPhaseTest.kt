package co.yappuworld.schedule.application

import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionProgressPhase.DONE
import co.yappuworld.schedule.domain.SessionProgressPhase.PENDING
import co.yappuworld.schedule.domain.SessionProgressPhase.TODAY
import co.yappuworld.schedule.domain.SessionProgressPhase.UPCOMING
import co.yappuworld.schedule.infrastructure.repository.ScheduleJpaRepository
import co.yappuworld.support.fixture.operation.OperationFixture
import co.yappuworld.support.fixture.schedule.ScheduleFixture
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class ScheduleServiceSessionProgressPhaseTest {

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
        assertEquals(result.sessions[0].progressPhase, DONE)
        assertEquals(result.sessions[1].progressPhase, TODAY)
        assertEquals(result.sessions[2].progressPhase, PENDING)
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
        assertEquals(result.sessions[0].progressPhase, DONE)
        assertEquals(result.sessions[1].progressPhase, DONE)
        assertEquals(result.sessions[2].progressPhase, UPCOMING)
        assertEquals(result.sessions[3].progressPhase, PENDING)
        assertEquals(result.upcomingSessionIndex, 2)
    }
}
