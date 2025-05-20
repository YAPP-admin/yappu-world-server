package co.yappuworld.schedule.client.application

import co.yappuworld.schedule.client.dto.request.AdminSessionAttendanceUpdateRequest
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.jpa.ScheduleRepository
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantRepository
import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitParamFixture
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.UserCommandService
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserEntity
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

class AdminAttendanceServiceTest @Autowired constructor(
    private val adminAttendanceService: AdminAttendanceService,
    private val userCommandService: UserCommandService,
    private val activityUnitRepository: ActivityUnitRepository,
    private val scheduleRepository: ScheduleRepository,
    private val sessionParticipantRepository: SessionParticipantRepository
) : SpringBootTestFeatureSpec({

        feature("특정 세션의 출석 일괄 업데이트") {
            val activeGeneration = 25

            lateinit var users: List<UserEntity>
            lateinit var activityUnits: List<ActivityUnitEntity>
            lateinit var session: SessionEntity
            lateinit var sessionParticipants: List<SessionParticipantEntity>

            beforeTest {
                users = List(2) {
                    getSignUpApplicationEntityFixture(
                        activityUnitParams = listOf(getActivityUnitParamFixture())
                    )
                }.map { userCommandService.signUp(it, UserRole.ACTIVE) }
                activityUnits = activityUnitRepository.saveAll(
                    users.map {
                        getActivityUnitEntityFixture(
                            generation = activeGeneration,
                            userId = it.id
                        )
                    }
                )
                session = scheduleRepository.save(getSessionEntityFixture(generation = activeGeneration))
                sessionParticipants = sessionParticipantRepository.saveAllAndFlush(
                    activityUnits.map { activityUnit ->
                        SessionParticipantEntity(
                            session = session,
                            activityUnit = activityUnit
                        )
                    }
                )
            }

            scenario("출석 일괄 업데이트") {
                forAll(
                    row(AttendanceStatus.ON_TIME),
                    row(AttendanceStatus.LATE),
                    row(AttendanceStatus.ABSENT),
                    row(AttendanceStatus.EXCUSED_ABSENCE),
                    row(AttendanceStatus.EARLY_CHECK_OUT)
                ) { status ->
                    adminAttendanceService.updateSessionAttendances(
                        AdminSessionAttendanceUpdateRequest(
                            sessionId = session.id,
                            attendanceStatus = status
                        )
                    )

                    sessionParticipantRepository
                        .findAllBySessionId(session.id)
                        .shouldForAll { it.attendanceStatus shouldBe status }
                }
            }
        }
    })
