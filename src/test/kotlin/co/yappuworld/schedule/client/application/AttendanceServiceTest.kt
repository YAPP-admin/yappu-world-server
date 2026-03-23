package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.AttendanceRequest
import co.yappuworld.schedule.client.dto.response.AttendancesHistoryResponseV2
import co.yappuworld.schedule.domain.SessionAttendance
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.AttendanceCommandService
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.LatePassFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.support.fixture.AttendanceFixture
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.AttendanceFixture.getAttendeeFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionWithAttendanceFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitsFixture
import co.yappuworld.user.infrastructure.UserFindService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import java.time.LocalDateTime

class AttendanceServiceTest :
    FeatureSpec({

        val attendanceFindService = mockk<AttendanceFindService>()
        val attendanceCommandService = mockk<AttendanceCommandService>()
        val configFindService = mockk<ConfigFindService>()
        val sessionFindService = mockk<SessionFindService>()
        val generationFindService = mockk<GenerationFindService>()
        val userFindService = mockk<UserFindService>()
        val latePassFindService = mockk<LatePassFindService>()
        val attendanceService = AttendanceService(
            attendanceFindService = attendanceFindService,
            configFindService = configFindService,
            sessionFindService = sessionFindService,
            generationFindService = generationFindService,
            userFindService = userFindService,
            latePassFindService = latePassFindService
        )

        val activeGeneration = 25
        val attendanceCode = "1234"
        val user = getUserWithActivityUnitsFixture(
            activityUnits = listOf(getActivityUnitFixture(generation = activeGeneration))
        )
        val session = getSessionEntityFixture()
        val request = AttendanceFixture.getAttendRequestFixture(
            attendanceCode = attendanceCode,
            sessionId = session.id
        )
        val attendance = getAttendanceEntityFixture(session = session)

        fun successConditionMocking() {
            every { sessionFindService.findSessionAttendance(user.id, session.id) } returns
                SessionAttendance(session, attendance)
            every { configFindService.findAttendanceCodeValue() } returns attendanceCode
            justRun { attendanceCommandService.checkIn(any()) }
        }

        feature("출석 체크") {

            feature("활성화 된 기수가 있을 때") {

                feature("출석 관련 검증을 진행한다.") {

                    scenario("이미 출석을 완료한 유저면 예외가 발생한다.") {
                        successConditionMocking()
                        every { sessionFindService.findSessionAttendance(user.id, session.id) } returns
                            SessionAttendance(
                                session,
                                getAttendanceEntityFixture(status = AttendanceStatus.ON_TIME)
                            )

                        shouldThrow<BusinessException> {
                            attendanceService.checkIn(request, user.id, LocalDateTime.now())
                        }.error.shouldBe(AttendanceError.ALREADY_CHECKED_IN)
                    }

                    scenario("출석 코드가 등록되지 않은 상황에는 예외가 발생한다.") {
                        successConditionMocking()
                        every { configFindService.findAttendanceCodeValue() } returns null

                        shouldThrow<BusinessException> {
                            attendanceService.checkIn(
                                request = request,
                                userId = user.id,
                                now = LocalDateTime.of(session.date, session.time)
                            )
                        }.error shouldBe AttendanceError.UNREGISTERED_ATTENDANCE_CODE
                    }

                    scenario("출석 코드가 일치하지 않으면 예외가 발생한다.") {
                        successConditionMocking()
                        val wrongAttendanceCode = "9999"
                        wrongAttendanceCode shouldNotBe attendanceCode

                        shouldThrow<BusinessException> {
                            attendanceService.checkIn(
                                request = AttendanceRequest(session.id, wrongAttendanceCode),
                                userId = user.id,
                                now = LocalDateTime.of(session.date, session.time)
                            )
                        }.error.shouldBe(AttendanceError.ATTENDANCE_CODE_NOT_MATCH)
                    }
                }
            }
        }

        feature("출석 내역 조회 v2") {

            scenario("활성 기수의 전체 세션 출석 이력을 상태값과 함께 조회한다.") {
                val now = LocalDateTime.of(2025, 2, 15, 15, 0)
                val sessions = listOf(
                    getSessionWithAttendanceFixture(attendanceStatus = AttendanceStatus.LATE),
                    getSessionWithAttendanceFixture(
                        attendanceStatus = AttendanceStatus.PENDING,
                        checkedInAt = null,
                        date = now.toLocalDate().plusDays(1),
                        endDate = now.toLocalDate().plusDays(1)
                    )
                )
                every { generationFindService.findActiveGeneration() } returns activeGeneration
                every {
                    sessionFindService.findSessionsWithAttendanceStatus(
                        generation = activeGeneration,
                        userId = user.id,
                        now = now
                    )
                } returns sessions
                every { userFindService.findSessionAttendee(user.id, activeGeneration) } returns
                    getAttendeeFixture(getUserWithActivityUnitFixture(generation = activeGeneration), activeGeneration)

                attendanceService.getAttendancesHistoryV2(user.id, now) shouldBe
                    AttendancesHistoryResponseV2.of(sessions, now)
            }

            scenario("활성 기수의 참가자가 아니면 예외가 발생한다.") {
                val now = LocalDateTime.of(2025, 2, 15, 15, 0)
                every { generationFindService.findActiveGeneration() } returns activeGeneration
                every { userFindService.findSessionAttendee(user.id, activeGeneration) } throws
                    BusinessException(AttendanceError.NO_ATTENDEE_POSITION_ACTIVITY_IN_GENERATION)

                shouldThrow<BusinessException> {
                    attendanceService.getAttendancesHistoryV2(user.id, now)
                }.error shouldBe AttendanceError.NO_ATTENDEE_POSITION_ACTIVITY_IN_GENERATION
            }
        }
    })
