package co.yappuworld.schedule.client.application

import co.yappuworld.schedule.domain.AttendanceStatus
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.domain.ScheduleError
import co.yappuworld.schedule.infrastructure.ScheduleFindService
import co.yappuworld.schedule.infrastructure.ScheduleRepository
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.support.fixture.AttendanceFixture
import co.yappuworld.support.fixture.ScheduleFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithLastActivityUnitFixture
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.UserFindService
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class UpcomingSessionFindTest :
    FeatureSpec({

        val scheduleRepository = mockk<ScheduleRepository>()
        val userFindService = mockk<UserFindService>()
        val sessionFindService = mockk<SessionFindService>()
        val attendanceFindService = mockk<AttendanceFindService>()
        val generationFindService = mockk<GenerationFindService>()
        val scheduleFindService = mockk<ScheduleFindService>()

        val scheduleService = ScheduleService(
            scheduleRepository = scheduleRepository,
            userFindService = userFindService,
            sessionFindService = sessionFindService,
            attendanceFindService = attendanceFindService,
            generationFindService = generationFindService,
            scheduleFindService = scheduleFindService
        )

        feature("가장 인접한 세션을 조회할 때") {

            val userId = UUID.randomUUID()
            val now = LocalDateTime.of(2024, 10, 22, 14, 0)
            val user = getUserWithLastActivityUnitFixture(userId = userId, generation = 25)
            val generation = 25
            val session = ScheduleFixture.getSessionEntityFixture(
                date = now.toLocalDate(),
                endDate = now.toLocalDate(),
                time = LocalTime.of(14, 0),
                endTime = LocalTime.of(18, 0),
                generation = generation
            )

            fun setCheckInPossibleCircumstance() {
                every { userFindService.findUserWithLastActivityUnit(any()) } returns
                    getUserWithLastActivityUnitFixture(
                        userId = userId,
                        generation = 25
                    )
                every { generationFindService.findActiveGeneration() } returns generation
                every { sessionFindService.findUpcomingSession(any(), any()) } returns session
                every { attendanceFindService.findSessionAttendance(any(), any()) } returns null
            }

            scenario("현재 테스트 조건에선 출석을 누를 수 있다.") {
                setCheckInPossibleCircumstance()
                scheduleService.getUpcomingSessionAttendance(userId, now).canCheckIn.shouldBeTrue()
            }

            scenario("활성화 된 기수가 없으면 예외가 발생한다.") {
                setCheckInPossibleCircumstance()
                every { generationFindService.findActiveGeneration() } returns null

                shouldThrowExactly<BusinessException> {
                    scheduleService.getUpcomingSessionAttendance(userId, now)
                }.error shouldBe ScheduleError.NO_SESSION_IN_BREAK_PERIOD
            }

            scenario("세션이 존재하지 않으면 예외가 발생한다.") {
                setCheckInPossibleCircumstance()
                every { sessionFindService.findUpcomingSession(any(), any()) } returns null

                shouldThrowExactly<BusinessException> {
                    scheduleService.getUpcomingSessionAttendance(userId, now)
                }.error shouldBe ScheduleError.NO_UPCOMING_SESSION
            }

            scenario("활성화 된 기수의 유저가 아니면 출석을 누를 수 없다.") {
                setCheckInPossibleCircumstance()
                every { userFindService.findUserWithLastActivityUnit(any()) } returns
                    user.copy(lastActiveGeneration = 24)

                scheduleService.getUpcomingSessionAttendance(userId, now).canCheckIn.shouldBeFalse()
            }

            scenario("유저의 권한이 활동 중인 상태가 아니라면 출석이 불가하다.") {
                setCheckInPossibleCircumstance()

                forAll(
                    row(UserRole.ALUMNI),
                    row(UserRole.GRADUATE),
                    row(UserRole.ADMIN),
                    row(UserRole.STAFF)
                ) { role ->
                    every { userFindService.findUserWithLastActivityUnit(any()) } returns user.copy(role = role)
                    scheduleService.getUpcomingSessionAttendance(userId, now).canCheckIn.shouldBeFalse()
                }
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
                    every { attendanceFindService.findSessionAttendance(any(), any()) } returns
                        AttendanceFixture.getAttendanceFixture(
                            userId = userId,
                            scheduleId = session.id,
                            status = status
                        )

                    scheduleService.getUpcomingSessionAttendance(userId, now).canCheckIn.shouldBeFalse()
                }
            }

            scenario("세션 시작 20분 전이거나 세션 종료 이후라면 출석 버튼을 누를 수 없다.") {
                setCheckInPossibleCircumstance()
                forAll(
                    row(
                        LocalDateTime.of(
                            session.date,
                            session.time?.minusMinutes(20)?.minusNanos(1) ?: LocalTime.MIN
                        )
                    ),
                    row(LocalDateTime.of(session.endDate, session.endTime ?: LocalTime.MAX))
                ) { now ->
                    scheduleService.getUpcomingSessionAttendance(userId, now).canCheckIn.shouldBeFalse()
                }
            }

            scenario("세션 시작 20분 전부터 세션 종료 전까지 출석 버튼을 누를 수 있다.") {
                setCheckInPossibleCircumstance()
                forAll(
                    row(LocalDateTime.of(session.date, session.time?.minusMinutes(20) ?: LocalTime.MIN)),
                    row(LocalDateTime.of(session.endDate, session.endTime?.minusNanos(1) ?: LocalTime.MAX))
                ) { now ->
                    scheduleService.getUpcomingSessionAttendance(userId, now).canCheckIn.shouldBeTrue()
                }
            }
        }
    })
