package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.ScheduleFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.support.fixture.AttendanceFixture
import co.yappuworld.support.fixture.ScheduleFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitsFixture
import co.yappuworld.user.domain.vo.Position
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

        feature("가장 인접한 세션을 조회할 때") {

            val generation = 25
            val now = LocalDateTime.of(2024, 10, 22, 14, 0)
            val userId = UUID.randomUUID()
            val user = getUserWithActivityUnitsFixture(
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

            fun setCheckInPossibleCircumstance() {
                every { generationFindService.findActiveGenerationOrNull() } returns generation
                every { userFindService.findUserWithActivities(any()) } returns getUserWithActivityUnitsFixture(
                    userId = userId,
                    activityUnits = listOf(
                        getActivityUnitFixture(
                            generation = generation,
                            position = Position.PM,
                            userId = userId
                        )
                    )
                )
                every { sessionFindService.findUpcomingSession(any(), any()) } returns session
                every { attendanceFindService.findSessionAttendance(any(), any()) } returns null
            }

            scenario("현재 테스트 조건에선 출석을 누를 수 있다.") {
                setCheckInPossibleCircumstance()
                scheduleService.getUpcomingSessionAttendance(userId, now).canCheckIn.shouldBeTrue()
            }

            scenario("활성화 된 기수가 없으면 예외가 발생한다.") {
                setCheckInPossibleCircumstance()
                every { generationFindService.findActiveGenerationOrNull() } returns null

                shouldThrowExactly<BusinessException> {
                    scheduleService.getUpcomingSessionAttendance(userId, now)
                }.error shouldBe ScheduleError.NO_SESSION_WITHOUT_ACTIVE_GENERATION
            }

            scenario("세션의 기수에 유저 활동기록이 없으면 예외가 발생한다.") {
                setCheckInPossibleCircumstance()
                every { userFindService.findUserWithActivities(any()) } returns
                    getUserWithActivityUnitsFixture(
                        activityUnits = listOf(
                            getActivityUnitFixture(
                                generation = generation + 1,
                                position = Position.PM,
                                userId = userId
                            )
                        )
                    )

                shouldThrowExactly<BusinessException> {
                    scheduleService.getUpcomingSessionAttendance(userId, now).canCheckIn.shouldBeFalse()
                }.error shouldBe AttendanceError.NO_ATTENDEE_ACTIVITY_IN_GENERATION
            }

            scenario("운영진이나 활동회원이 아니면 출석이 불가하다.") {
                setCheckInPossibleCircumstance()

                forAll(
                    row(UserRole.ALUMNI),
                    row(UserRole.GRADUATE),
                    row(UserRole.ADMIN)
                ) { role ->
                    every { userFindService.findUserWithActivities(any()) } returns
                        getUserWithActivityUnitsFixture(role = role)

                    shouldThrowExactly<BusinessException> {
                        scheduleService.getUpcomingSessionAttendance(userId, now)
                    }.error shouldBe AttendanceError.UNAUTHORIZED_CHECK_IN
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
                        AttendanceFixture.getAttendanceEntityFixture(
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
                            session.time.minusMinutes(20)?.minusNanos(1) ?: LocalTime.MIN
                        )
                    ),
                    row(LocalDateTime.of(session.endDate, session.endTime))
                ) { now ->
                    scheduleService.getUpcomingSessionAttendance(userId, now).canCheckIn.shouldBeFalse()
                }
            }

            scenario("세션 시작 20분 전부터 세션 종료 전까지 출석 버튼을 누를 수 있다.") {
                setCheckInPossibleCircumstance()
                forAll(
                    row(LocalDateTime.of(session.date, session.time.minusMinutes(20) ?: LocalTime.MIN)),
                    row(LocalDateTime.of(session.endDate, session.endTime.minusNanos(1) ?: LocalTime.MAX))
                ) { now ->
                    scheduleService.getUpcomingSessionAttendance(userId, now).canCheckIn.shouldBeTrue()
                }
            }
        }
    })
