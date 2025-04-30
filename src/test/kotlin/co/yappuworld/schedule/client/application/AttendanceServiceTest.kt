package co.yappuworld.schedule.client.application

import co.yappuworld.schedule.client.dto.request.AttendanceRequest
import co.yappuworld.schedule.domain.AttendanceEntity
import co.yappuworld.schedule.domain.AttendanceError
import co.yappuworld.schedule.domain.AttendanceStatus
import co.yappuworld.schedule.infrastructure.AttendanceCommandService
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.LatePassFindService
import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.support.fixture.AttendanceFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getTaskEntityFixture
import co.yappuworld.support.fixture.UserFixture
import co.yappuworld.user.infrastructure.UserFindService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import java.util.UUID

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

        feature("출석 체크") {
            scenario("활성화 된 기수가 없으면 예외가 발생한다.") {
                every { generationFindService.findActiveGenerationOrNull() } returns null

                shouldThrow<BusinessException> {
                    attendanceService.checkIn(
                        AttendanceFixture.getAttendRequestFixture(),
                        UUID.randomUUID(),
                        LocalDateTime.now()
                    )
                }.error.shouldBe(AttendanceError.NO_ACTIVE_GENERATION)
            }

            feature("활성화 된 기수가 있을 때") {
                val attendanceCode = "1234"
                val userId = UUID.randomUUID()
                val sessionId = UUID.randomUUID()
                val request = AttendanceFixture.getAttendRequestFixture(
                    attendanceCode = attendanceCode,
                    sessionId = sessionId
                )
                val activeGeneration = 25
                every { generationFindService.findActiveGenerationOrNull() } returns activeGeneration

                feature("출석 관련 검증을 진행한다.") {
                    scenario("이미 출석을 완료한 유저면 예외가 발생한다.") {
                        every { attendanceFindService.hasAlreadyCheckedIn(userId, sessionId) } returns true

                        shouldThrow<BusinessException> {
                            attendanceService.checkIn(request, userId, LocalDateTime.now())
                        }.error.shouldBe(AttendanceError.ALREADY_CHECKED_IN)
                    }

                    scenario("출석 코드가 등록되지 않은 상황에는 예외가 발생한다.") {
                        every { attendanceFindService.hasAlreadyCheckedIn(any(), any()) } returns false
                        every { configFindService.findConfig("attendanceCode") } returns null

                        shouldThrow<BusinessException> {
                            attendanceService.checkIn(
                                request = AttendanceRequest(UUID.randomUUID(), "sessionId"),
                                userId = UUID.randomUUID(),
                                now = LocalDateTime.now()
                            )
                        }.error.shouldBe(AttendanceError.ATTENDANCE_CODE_NOT_FOUND)
                    }

                    scenario("출석 코드가 일치하지 않으면 예외가 발생한다.") {
                        every { attendanceFindService.hasAlreadyCheckedIn(any(), any()) } returns false
                        every { configFindService.findConfig("attendanceCode")?.value } returns "1234"

                        shouldThrow<BusinessException> {
                            attendanceService.checkIn(
                                request = AttendanceRequest(sessionId, "9999"),
                                userId = UUID.randomUUID(),
                                now = LocalDateTime.now()
                            )
                        }.error.shouldBe(AttendanceError.ATTENDANCE_CODE_NOT_MATCH)
                    }
                }

                feature("세션 관련 검증을 진행한다.") {
                    every { attendanceFindService.hasAlreadyCheckedIn(any(), any()) } returns false
                    every { configFindService.findConfig("attendanceCode")?.value } returns attendanceCode

                    scenario("세션을 찾을 수 없으면 예외가 발생한다.") {
                        every { sessionFindService.findSession(sessionId) } returns null

                        shouldThrow<BusinessException> {
                            attendanceService.checkIn(request, userId, LocalDateTime.now())
                        }.error.shouldBe(AttendanceError.SESSION_NOT_FOUND)
                    }

                    scenario("세션이 세션 타입이 아니면 예외가 발생한다.") {
                        every { sessionFindService.findSession(any()) } returns getTaskEntityFixture()

                        shouldThrow<BusinessException> {
                            attendanceService.checkIn(request, userId, LocalDateTime.now())
                        }.error.shouldBe(AttendanceError.CHECK_IN_ONLY_FOR_SESSION)
                    }

                    scenario("세션의 기수와 활성화된 기수가 일치하지 않으면 예외가 발생한다.") {
                        val session = getSessionEntityFixture(generation = activeGeneration - 1)
                        every { sessionFindService.findSession(sessionId) } returns session

                        shouldThrow<BusinessException> {
                            attendanceService.checkIn(request, userId, LocalDateTime.now())
                        }.error.shouldBe(AttendanceError.GENERATION_NOT_MATCH)
                    }
                }

                feature("유저 관련 검증을 진행한다.") {
                    every { attendanceFindService.hasAlreadyCheckedIn(any(), any()) } returns false
                    every { configFindService.findConfig("attendanceCode")?.value } returns attendanceCode
                    val session = getSessionEntityFixture(generation = activeGeneration)
                    every { sessionFindService.findSession(sessionId) } returns session

                    scenario("유저의 기수와 활성화된 기수가 일치하지 않으면 예외가 발생한다.") {
                        every { userFindService.findUserWithLastActivityUnit(userId) } returns
                            UserFixture.getUserWithLastActivityUnitFixture(generation = activeGeneration - 1)

                        shouldThrow<BusinessException> {
                            attendanceService.checkIn(request, userId, LocalDateTime.now())
                        }.error.shouldBe(AttendanceError.GENERATION_NOT_MATCH)
                    }
                }

                feature("출석 정보를 생성 시 상태 정보") {
                    val session = getSessionEntityFixture(generation = activeGeneration)

                    scenario("지각 시간 이전에 출석을 했다면, 상태가 정상으로 생성된다.") {
                        val now = LocalDateTime.of(session.date, session.time).plusMinutes(20).minusNanos(1)

                        AttendanceEntity
                            .checkInSession(now = now, userId = userId, session = session)
                            .status
                            .shouldBe(AttendanceStatus.ON_TIME)
                    }

                    scenario("세션 시작 20분 후부터 2시간 미만까지는 지각 상태로 설정된다.") {
                        forAll(
                            row(session.time.shouldNotBeNull().plusMinutes(20)),
                            row(
                                session.time
                                    .shouldNotBeNull()
                                    .plusHours(2)
                                    .minusNanos(1)
                            )
                        ) { time ->
                            val now = LocalDateTime.of(session.date, time)

                            AttendanceEntity
                                .checkInSession(now = now, userId = userId, session = session)
                                .status
                                .shouldBe(AttendanceStatus.LATE)
                        }
                    }

                    scenario("2시간을 초과하면 결석 상태로 설정된다.") {
                        val now = LocalDateTime.of(session.date, session.time).plusHours(2).plusNanos(1)

                        AttendanceEntity
                            .checkInSession(now = now, userId = userId, session = session)
                            .status
                            .shouldBe(AttendanceStatus.ABSENT)
                    }
                }
            }
        }

        feature("출석 통계 조회") {

            scenario("활성화된 기수가 없으면 예외가 발생한다.") {
                every { generationFindService.findActiveGenerationOrNull() } returns null

                shouldThrow<BusinessException> {
                    attendanceService.getAttendanceStatistics(UUID.randomUUID(), LocalDateTime.now())
                }.error.shouldBe(AttendanceError.NO_ACTIVE_GENERATION)
            }
        }
    })
