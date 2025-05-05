package co.yappuworld.schedule.client.application

import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.domain.vo.SessionProgressPhase.DONE
import co.yappuworld.schedule.domain.vo.SessionProgressPhase.PENDING
import co.yappuworld.schedule.domain.vo.SessionProgressPhase.TODAY
import co.yappuworld.schedule.domain.vo.SessionProgressPhase.UPCOMING
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.ScheduleFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendance
import co.yappuworld.support.fixture.ScheduleFixture
import co.yappuworld.user.infrastructure.UserFindService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals

class ScheduleServiceSessionProgressPhaseTest {

    private val userFindService = mockk<UserFindService>()
    private val sessionFindService = mockk<SessionFindService>()
    private val attendanceFindService = mockk<AttendanceFindService>()
    private val generationFindService = mockk<GenerationFindService>()
    private val scheduleFindService = mockk<ScheduleFindService>()
    private val scheduleService = ScheduleService(
        userFindService = userFindService,
        sessionFindService = sessionFindService,
        attendanceFindService = attendanceFindService,
        generationFindService = generationFindService,
        scheduleFindService = scheduleFindService
    )

    private val generation = 2
    private val now = LocalDateTime.of(2022, 5, 5, 14, 0)

    @BeforeEach
    fun mockGenerationRepository() {
        every { generationFindService.findActiveGenerationOrNull() } returns generation
    }

    fun mockScheduleRepository(schedules: List<SessionWithAttendance>) {
        every { sessionFindService.findSessionsWithAttendanceStatus(generation, any(), any()) } returns schedules
    }

    @Test
    fun `지난 건 DONE, 오늘 건 TODAY, 남은 건 PENDING이다`() {
        mockScheduleRepository(
            listOf(
                ScheduleFixture.getSessionWithAttendanceFixture(
                    generation = 4,
                    date = now.toLocalDate().minusDays(1),
                    endDate = now.toLocalDate().minusDays(1)
                ),
                ScheduleFixture.getSessionWithAttendanceFixture(
                    generation = 4,
                    date = now.toLocalDate(),
                    endDate = now.toLocalDate(),
                    endTime = now.toLocalTime().plusSeconds(1)
                ),
                ScheduleFixture.getSessionWithAttendanceFixture(
                    generation = 4,
                    endDate = now.toLocalDate().plusDays(3)
                )
            )
        )

        val result = scheduleService.getCurrentGenerationSessions(UUID.randomUUID(), now)
        assertEquals(DONE, result.sessions[0].progressPhase)
        assertEquals(TODAY, result.sessions[1].progressPhase)
        assertEquals(PENDING, result.sessions[2].progressPhase)
        assertEquals(result.upcomingSessionId, result.sessions[1].id)
    }

    @Test
    fun `지난 건 DONE, 임박한 건 UPCOMING, 남은 건 PENDING이다`() {
        mockScheduleRepository(
            listOf(
                ScheduleFixture.getSessionWithAttendanceFixture(
                    generation = 4,
                    date = now.toLocalDate().minusDays(1),
                    endDate = now.toLocalDate().minusDays(1)
                ),
                ScheduleFixture.getSessionWithAttendanceFixture(
                    generation = 4,
                    date = now.toLocalDate().plusDays(1),
                    endDate = now.toLocalDate().plusDays(1),
                    endTime = now.toLocalTime().plusSeconds(1)
                ),
                ScheduleFixture.getSessionWithAttendanceFixture(
                    generation = 4,
                    date = now.toLocalDate().plusDays(3),
                    endDate = now.toLocalDate().plusDays(3)
                )
            )
        )

        val result = scheduleService.getCurrentGenerationSessions(UUID.randomUUID(), now)
        assertEquals(DONE, result.sessions[0].progressPhase)
        assertEquals(UPCOMING, result.sessions[1].progressPhase)
        assertEquals(PENDING, result.sessions[2].progressPhase)
        assertEquals(result.upcomingSessionId, result.sessions[1].id)
    }
}
