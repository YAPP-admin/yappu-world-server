package co.yappuworld.schedule.client.application

import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.post.infrastructure.PostFindService
import co.yappuworld.schedule.domain.SessionAttendance
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.ScheduleFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitsFixture
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.UserFindService
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class UpcomingSessionFindTest :
    FeatureSpec({

        val userFindService = mockk<UserFindService>()
        val sessionFindService = mockk<SessionFindService>()
        val attendanceFindService = mockk<AttendanceFindService>()
        val generationFindService = mockk<GenerationFindService>()
        val scheduleFindService = mockk<ScheduleFindService>()
        val postFindService = mockk<PostFindService>()

        val scheduleService = ScheduleService(
            userFindService = userFindService,
            sessionFindService = sessionFindService,
            attendanceFindService = attendanceFindService,
            generationFindService = generationFindService,
            scheduleFindService = scheduleFindService,
            postFindService = postFindService
        )

        feature("가장 인접한 세션을 조회할 때") {

            val generation = 25
            val now = LocalDateTime.of(2024, 10, 22, 14, 0)
            val userId = UUID.randomUUID()
            val user = getUserWithActivityUnitsFixture(
                userId = userId,
                activityUnits = listOf(
                    getActivityUnitFixture(generation = generation, position = Position.PM, userId = userId)
                )
            )
            val session = ScheduleFixture.getSessionEntityFixture(
                date = now.toLocalDate(),
                endDate = now.toLocalDate(),
                time = LocalTime.of(14, 0),
                endTime = LocalTime.of(18, 0),
                generation = generation
            )
            val attendance = getAttendanceEntityFixture(
                userId = user.id,
                session = session,
                status = AttendanceStatus.PENDING
            )

            fun setCheckInPossibleCircumstance() {
                attendance.updateStatus(AttendanceStatus.PENDING)
                every { sessionFindService.findUpcomingSessionAttendance(any(), any()) } returns
                    SessionAttendance(session, attendance)
                every { postFindService.findNoticesTargetingSession(any(), any()) } returns emptyList()
            }

            scenario("현재 테스트 조건에선 출석을 누를 수 있다.") {
                setCheckInPossibleCircumstance()
                scheduleService.findUpcomingSessionWithAttendance(user.id, now).canCheckIn.shouldBeTrue()
            }

            scenario("이미 출석을 했다면 출석을 누를 수 없다.") {
                setCheckInPossibleCircumstance()

                forAll(
                    row(AttendanceStatus.ON_TIME),
                    row(AttendanceStatus.LATE),
                    row(AttendanceStatus.ABSENT),
                    row(AttendanceStatus.EARLY_CHECK_OUT),
                    row(AttendanceStatus.EXCUSED_ABSENCE)
                ) { status ->
                    attendance.updateStatus(status)
                    every { sessionFindService.findUpcomingSessionAttendance(any(), any()) } returns
                        SessionAttendance(session, attendance)

                    val result = scheduleService.findUpcomingSessionWithAttendance(user.id, now)
                    withClue(
                        "Status: $status, canCheckIn: ${result.canCheckIn}, attendance.status: ${attendance.status}"
                    ) {
                        result.canCheckIn.shouldBeFalse()
                    }
                }
            }

            scenario("세션 시작 20분 전이거나 세션 종료 이후라면 출석 버튼을 누를 수 없다.") {
                setCheckInPossibleCircumstance()
                forAll(
                    row(LocalDateTime.of(session.date, session.time.minusMinutes(20).minusNanos(1))),
                    row(LocalDateTime.of(session.endDate, session.endTime))
                ) { now ->
                    scheduleService.findUpcomingSessionWithAttendance(user.id, now).canCheckIn.shouldBeFalse()
                }
            }

            scenario("세션 시작 20분 전부터 세션 종료 전까지 출석 버튼을 누를 수 있다.") {
                setCheckInPossibleCircumstance()
                forAll(
                    row(LocalDateTime.of(session.date, session.time.minusMinutes(20) ?: LocalTime.MIN)),
                    row(LocalDateTime.of(session.endDate, session.endTime.minusNanos(1) ?: LocalTime.MAX))
                ) { now ->
                    scheduleService.findUpcomingSessionWithAttendance(user.id, now).canCheckIn.shouldBeTrue()
                }
            }
        }
    })
