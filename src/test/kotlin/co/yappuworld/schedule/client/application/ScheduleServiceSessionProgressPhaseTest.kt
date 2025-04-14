package co.yappuworld.schedule.client.application

import co.yappuworld.attendance.infrastructure.AttendanceFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionProgressPhase.DONE
import co.yappuworld.schedule.domain.SessionProgressPhase.PENDING
import co.yappuworld.schedule.domain.SessionProgressPhase.TODAY
import co.yappuworld.schedule.domain.SessionProgressPhase.UPCOMING
import co.yappuworld.schedule.infrastructure.ScheduleRepository
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.support.fixture.ScheduleFixture
import co.yappuworld.user.infrastructure.UserFindService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class ScheduleServiceSessionProgressPhaseTest {

    private val scheduleRepository = mockk<ScheduleRepository>()
    private val userFindService = mockk<UserFindService>()
    private val sessionFindService = mockk<SessionFindService>()
    private val attendanceFindService = mockk<AttendanceFindService>()
    private val generationFindService = mockk<GenerationFindService>()
    private val scheduleService = ScheduleService(
        scheduleRepository = scheduleRepository,
        userFindService = userFindService,
        sessionFindService = sessionFindService,
        attendanceFindService = attendanceFindService,
        generationFindService = generationFindService
    )

    private val generation = 2
    private val now = LocalDate.of(2022, 5, 5)

    @BeforeEach
    fun mockGenerationRepository() {
        every { generationFindService.findActiveGeneration() } returns generation
    }

    fun mockScheduleRepository(schedules: List<SessionEntity>) {
        every { scheduleRepository.findAllSessionEntityByGeneration(generation) } returns schedules
    }

    @Test
    fun `지난 건 DONE, 오늘 건 TODAY, 남은 건 PENDING이다`() {
        mockScheduleRepository(
            listOf(
                ScheduleFixture.getSessionEntityFixture(date = now.minusDays(1)),
                ScheduleFixture.getSessionEntityFixture(date = now),
                ScheduleFixture.getSessionEntityFixture(date = now.plusDays(3))
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
                ScheduleFixture.getSessionEntityFixture(date = now.minusDays(2)),
                ScheduleFixture.getSessionEntityFixture(date = now.minusDays(1)),
                ScheduleFixture.getSessionEntityFixture(date = now.plusDays(1)),
                ScheduleFixture.getSessionEntityFixture(date = now.plusDays(3))
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
