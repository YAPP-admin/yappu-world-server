package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.AttendanceRequest
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
import co.yappuworld.support.fixture.UserFixture.getActivityUnitFixture
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
            attendanceCommandService = attendanceCommandService,
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
        val attendee = getAttendeeFixture(
            userWithActivityUnits = user,
            generation = activeGeneration
        )
        val session = getSessionEntityFixture()
        val request = AttendanceFixture.getAttendRequestFixture(
            attendanceCode = attendanceCode,
            sessionId = session.id
        )

        fun successConditionMocking() {
            every { generationFindService.findActiveGeneration() } returns activeGeneration
            every { userFindService.findSessionAttendee(any(), any()) } returns attendee
            every { sessionFindService.findSession(any()) } returns session
            every { attendanceFindService.findSessionAttendance(any(), any()) } returns getAttendanceEntityFixture()
            every { configFindService.findAttendanceCodeValue() } returns attendanceCode
            justRun { attendanceCommandService.checkIn(any()) }
        }

        feature("출석 체크") {

            feature("활성화 된 기수가 있을 때") {

                feature("출석 관련 검증을 진행한다.") {

                    scenario("이미 출석을 완료한 유저면 예외가 발생한다.") {
                        successConditionMocking()
                        every {
                            attendanceFindService.findSessionAttendance(user.id, session.id)
                        } returns getAttendanceEntityFixture(status = AttendanceStatus.ON_TIME)

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
    })
