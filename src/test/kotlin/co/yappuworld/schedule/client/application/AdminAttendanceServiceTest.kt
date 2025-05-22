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
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.UserCommandService
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserEntity
import co.yappuworld.user.infrastructure.jpa.UserRepository
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.util.UUID

class AdminAttendanceServiceTest @Autowired constructor(
    private val adminAttendanceService: AdminAttendanceService,
    private val userCommandService: UserCommandService,
    private val activityUnitRepository: ActivityUnitRepository,
    private val scheduleRepository: ScheduleRepository,
    private val sessionParticipantRepository: SessionParticipantRepository,
    private val userRepository: UserRepository
) : SpringBootTestFeatureSpec({

        feature("세션과 활동 기록 ID로 출석 업데이트") {

            scenario("존재하지 않는 세션 ID와 활동 기록 ID를 넘기더라도 예외는 발생하지 않는다.") {
                shouldNotThrowAny {
                    adminAttendanceService.updateAttendance(
                        AdminAttendanceUpdateRequest(
                            listOf(
                                AdminAttendanceUpdateTargetRequest(
                                    userActivityUnitId = UUID.randomUUID(),
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
                                userActivityUnitId = activityUnit1.id,
                                sessionId = session1.id,
                                attendanceStatus = AttendanceStatus.ON_TIME
                            ),
                            AdminAttendanceUpdateTargetRequest(
                                userActivityUnitId = activityUnit1.id,
                                sessionId = session2.id,
                                attendanceStatus = AttendanceStatus.LATE
                            ),
                            AdminAttendanceUpdateTargetRequest(
                                userActivityUnitId = activityUnit2.id,
                                sessionId = session1.id,
                                attendanceStatus = AttendanceStatus.LATE
                            ),
                            AdminAttendanceUpdateTargetRequest(
                                userActivityUnitId = activityUnit2.id,
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

        feature("출석 조회") {

            val generation = 25
            val now = LocalDateTime.of(2024, 12, 12, 0, 0)
            val sessions = listOf(
                getSessionEntityFixture(
                    date = now.toLocalDate().minusDays(2),
                    endDate = now.toLocalDate().minusDays(2),
                    generation = generation
                ),
                getSessionEntityFixture(
                    date = now.toLocalDate().minusDays(1),
                    endDate = now.toLocalDate().minusDays(1),
                    generation = generation
                ),
                getSessionEntityFixture(
                    date = now.toLocalDate().plusDays(1),
                    endDate = now.toLocalDate().plusDays(1),
                    generation = generation
                )
            )
            val users = List(2) { getUserEntityFixture() }
            val activityUnits = users.map { getActivityUnitEntityFixture(generation = generation, userId = it.id) }
            val sessionParticipants = listOf(
                SessionParticipantEntity(
                    session = sessions[0],
                    activityUnit = activityUnits[0]
                ).apply { forceUpdateStatus(AttendanceStatus.ABSENT) },
                SessionParticipantEntity(
                    session = sessions[1],
                    activityUnit = activityUnits[0]
                ).apply { forceUpdateStatus(AttendanceStatus.ON_TIME) },
                SessionParticipantEntity(
                    session = sessions[2],
                    activityUnit = activityUnits[0]
                ).apply { forceUpdateStatus(AttendanceStatus.PENDING) },
                SessionParticipantEntity(
                    session = sessions[0],
                    activityUnit = activityUnits[1]
                ).apply { forceUpdateStatus(AttendanceStatus.PENDING) },
                SessionParticipantEntity(
                    session = sessions[1],
                    activityUnit = activityUnits[1]
                ).apply { forceUpdateStatus(AttendanceStatus.LATE) }
            )

            fun initialize() {
                scheduleRepository.saveAll(sessions)
                userRepository.saveAll(users)
                activityUnitRepository.saveAll(activityUnits)
                sessionParticipantRepository.saveAllAndFlush(sessionParticipants)
            }

            scenario("출석 상태 조회") {
                initialize()
                val result = adminAttendanceService.findAttendances(now)

                result.users.shouldHaveSize(2)
                result.sessions.shouldHaveSize(3)

                result.attendancesGroupedBySession[0].attendances.let {
                    it[0].status shouldBe AttendanceStatus.ABSENT
                    it[1].status shouldBe AttendanceStatus.ABSENT
                }

                result.attendancesGroupedBySession[1].attendances.let {
                    it[0].status shouldBe AttendanceStatus.ON_TIME
                    it[1].status shouldBe AttendanceStatus.LATE
                }

                result.attendancesGroupedBySession[2].attendances.let {
                    it[0].status shouldBe AttendanceStatus.PENDING
                    it[1].status.shouldBeNull()
                }
            }

            scenario("세션 통계를 조회") {
                initialize()
                val result = adminAttendanceService.findAttendances(now)

                result.sessions[0].totalPersonCount shouldBe 2
                result.sessions[0].totalOnTimeCount shouldBe 0
                result.sessions[0].totalLateCount shouldBe 0
                result.sessions[0].totalAbsentCount shouldBe 2
                result.sessions[0].totalExcusedAbsenceCount shouldBe 0
                result.sessions[0].totalEarlyCheckOutCount shouldBe 0

                result.sessions[1].totalPersonCount shouldBe 2
                result.sessions[1].totalOnTimeCount shouldBe 1
                result.sessions[1].totalLateCount shouldBe 1
                result.sessions[1].totalAbsentCount shouldBe 0
                result.sessions[1].totalExcusedAbsenceCount shouldBe 0
                result.sessions[1].totalEarlyCheckOutCount shouldBe 0

                result.sessions[2].totalPersonCount shouldBe 1
            }
        }
    })
