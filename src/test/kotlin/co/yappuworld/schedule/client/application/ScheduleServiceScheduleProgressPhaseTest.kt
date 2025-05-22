package co.yappuworld.schedule.client.application

import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.ScheduleFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.user.domain.model.UserWithActivityUnits
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.UserFindService
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import java.util.UUID

class ScheduleServiceScheduleProgressPhaseTest :
    FeatureSpec({

        val userFindService = mockk<UserFindService>()
        val sessionFindService = mockk<SessionFindService>()
        val attendanceFindService = mockk<AttendanceFindService>()
        val generationFindService = mockk<GenerationFindService>()
        val scheduleFindService = mockk<ScheduleFindService>()

        val scheduleService = ScheduleService(
            userFindService = userFindService,
            sessionFindService = sessionFindService,
            attendanceFindService = attendanceFindService,
            generationFindService = generationFindService,
            scheduleFindService = scheduleFindService
        )

        feature("스케줄 조회") {

            scenario("같은 날 2개의 데이터가 있으면 해당 일자 schedules가 하나의 배열로 묶인다") {
                val targetDate = LocalDate.of(2021, 5, 5)
                every { userFindService.findUserWithActivities(any()) } returns UserWithActivityUnits(
                    id = UUID.randomUUID(),
                    "email",
                    "name",
                    UserRole.ACTIVE,
                    emptyList()
                )
                every {
                    scheduleFindService.findSchedulesBetween(
                        LocalDate.of(2021, 5, 1),
                        LocalDate.of(2021, 6, 1).minusDays(1)
                    )
                } returns listOf(
                    getSessionEntityFixture(date = LocalDate.of(2021, 5, 4)),
                    getSessionEntityFixture(date = targetDate),
                    getSessionEntityFixture(date = targetDate)
                )
                every { attendanceFindService.findAttendancesBySchedules(any(), any()) } returns emptyList()

                val result = scheduleService.getSchedules(
                    request = SchedulePageRequest(2021, 5),
                    userId = UUID.randomUUID(),
                    now = targetDate.atStartOfDay()
                )

                result.dates
                    .single { it.date == targetDate }
                    .schedules
                    .shouldHaveSize(2)
            }
        }
    })
