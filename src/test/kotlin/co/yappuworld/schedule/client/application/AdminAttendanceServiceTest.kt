package co.yappuworld.schedule.client.application

import co.yappuworld.schedule.client.dto.request.AdminAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminAttendanceUpdateTargetRequest
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
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class AdminAttendanceServiceTest @Autowired constructor(
    private val adminAttendanceService: AdminAttendanceService,
    private val userCommandService: UserCommandService,
    private val activityUnitRepository: ActivityUnitRepository,
    private val scheduleRepository: ScheduleRepository,
    private val sessionParticipantRepository: SessionParticipantRepository
) : SpringBootTestFeatureSpec({

        feature("세션과 활동 기록 ID로 출석 업데이트") {

            scenario("존재하지 않는 세션 ID와 활동 기록 ID를 넘기더라도 예외는 발생하지 않는다.") {
                shouldNotThrowAny {
                    adminAttendanceService.updateAttendance(
                        AdminAttendanceUpdateRequest(
                            listOf(
                                AdminAttendanceUpdateTargetRequest(
                                    generationMemberId = UUID.randomUUID(),
                                    sessionId = UUID.randomUUID(),
                                    attendanceStatus = AttendanceStatus.ON_TIME
                                )
                            )
                        )
                    )
                }
            }

            scenario("세션 ID와 활동 기록이 매칭되는 경우 출석 상태가 업데이트 된다.") {
                val session1 = scheduleRepository.save(getSessionEntityFixture())
                val session2 = scheduleRepository.save(getSessionEntityFixture())
                val activityUnit1 = activityUnitRepository.save(getActivityUnitEntityFixture())
                val activityUnit2 = activityUnitRepository.save(getActivityUnitEntityFixture())
                sessionParticipantRepository.saveAllAndFlush(
                    listOf(
                        SessionParticipantEntity(session1, activityUnit1)
                    )
                )

                adminAttendanceService.updateAttendance(
                    AdminAttendanceUpdateRequest(
                        listOf(
                            AdminAttendanceUpdateTargetRequest(
                                generationMemberId = activityUnit1.id,
                                sessionId = session1.id,
                                attendanceStatus = AttendanceStatus.ON_TIME
                            ),
                            AdminAttendanceUpdateTargetRequest(
                                generationMemberId = activityUnit1.id,
                                sessionId = session2.id,
                                attendanceStatus = AttendanceStatus.LATE
                            ),
                            AdminAttendanceUpdateTargetRequest(
                                generationMemberId = activityUnit2.id,
                                sessionId = session1.id,
                                attendanceStatus = AttendanceStatus.LATE
                            ),
                            AdminAttendanceUpdateTargetRequest(
                                generationMemberId = activityUnit2.id,
                                sessionId = session2.id,
                                attendanceStatus = AttendanceStatus.LATE
                            )
                        )
                    )
                )

                val result = sessionParticipantRepository.findAll()
                result.shouldHaveSize(1)
                result[0].session.id shouldBe session1.id
                result[0].activityUnit.id shouldBe activityUnit1.id
                result[0].attendanceStatus shouldBe AttendanceStatus.ON_TIME
            }
        }

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
