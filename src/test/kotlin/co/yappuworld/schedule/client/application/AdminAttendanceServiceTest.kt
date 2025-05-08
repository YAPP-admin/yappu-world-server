package co.yappuworld.schedule.client.application

import co.yappuworld.schedule.client.dto.request.AdminSessionAttendanceUpdateRequest
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ABSENT
import co.yappuworld.schedule.infrastructure.AttendanceRepository
import co.yappuworld.schedule.infrastructure.ScheduleRepository
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitParamFixture
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.UserCommandService
import co.yappuworld.user.infrastructure.entity.UserEntity
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import org.springframework.beans.factory.annotation.Autowired

class AdminAttendanceServiceTest @Autowired constructor(
    private val adminAttendanceService: AdminAttendanceService,
    private val userCommandService: UserCommandService,
    private val scheduleRepository: ScheduleRepository,
    private val attendanceRepository: AttendanceRepository
) : SpringBootTestFeatureSpec({

        feature("특정 세션의 출석 일괄 업데이트") {
            val activeGeneration = 25

            lateinit var users: List<UserEntity>
            lateinit var session: SessionEntity

            beforeTest {
                users = List(2) {
                    getSignUpApplicationEntityFixture(
                        activityUnitParams = listOf(getActivityUnitParamFixture())
                    )
                }.map { userCommandService.signUp(it, UserRole.ACTIVE) }
                session = scheduleRepository.save(getSessionEntityFixture(generation = activeGeneration))
            }

            scenario("출석 데이터가 아무것도 없다면, 모두 새로 생성한다.") {
                adminAttendanceService.updateSessionAttendances(
                    AdminSessionAttendanceUpdateRequest(
                        sessionId = session.id,
                        attendanceStatus = ABSENT
                    )
                )

                attendanceRepository.findAllByScheduleId(session.id) shouldHaveSize 2
            }

            scenario("출석 데이터 중 일부 유저의 데이터가 없다면 새로 생성한다.") {
                val attendance = getAttendanceEntityFixture(
                    status = ABSENT,
                    scheduleId = session.id,
                    userId = users[0].id
                )
                attendanceRepository.save(attendance)

                adminAttendanceService.updateSessionAttendances(
                    AdminSessionAttendanceUpdateRequest(
                        sessionId = session.id,
                        attendanceStatus = ABSENT
                    )
                )

                val attendances = attendanceRepository.findAllByScheduleId(session.id)
                attendances shouldHaveSize 2
                attendances.filterNot { it.userId == users[0].id }.shouldNotBeEmpty()
            }

            scenario("존재하는 출석 데이터는 상태가 업데이트 된다.") {
                val attendance = getAttendanceEntityFixture(
                    status = ABSENT,
                    scheduleId = session.id,
                    userId = users[0].id
                )
                attendanceRepository.save(attendance)

                adminAttendanceService.updateSessionAttendances(
                    AdminSessionAttendanceUpdateRequest(
                        sessionId = session.id,
                        attendanceStatus = ABSENT
                    )
                )

                val attendances = attendanceRepository.findAllByScheduleId(session.id)
                attendances shouldHaveSize 2
                attendances.shouldForAll { it.status == ABSENT }
            }
        }
    })
