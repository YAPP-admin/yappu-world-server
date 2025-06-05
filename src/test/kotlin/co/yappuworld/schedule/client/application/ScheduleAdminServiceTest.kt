package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.client.dto.request.AdminSessionDeleteRequest
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.infrastructure.AttendanceRepository
import co.yappuworld.schedule.infrastructure.ScheduleRepository
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.ScheduleDtoFixture.getAdminSessionCreateRequestFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class ScheduleAdminServiceTest @Autowired constructor(
    private val adminScheduleService: AdminScheduleService,
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val scheduleRepository: ScheduleRepository,
    private val attendanceRepository: AttendanceRepository
) : SpringBootTestFeatureSpec({

        feature("세션 생성") {

            scenario("세션을 생성하면 해당 세션에 참석하는 유저들의 출석 정보가 생성된다") {
                // given
                val users = userRepository.saveAll(listOf(getUserEntityFixture(), getUserEntityFixture()))
                val request = getAdminSessionCreateRequestFixture(attendeeIds = users.map { it.id })

                // when
                val scheduleId = adminScheduleService.createSchedule(request)

                val userIds = users.map { user -> user.id }
                val result = attendanceRepository.findAllByScheduleId(scheduleId)
                result.shouldHaveSize(2)
                result.shouldForAll {
                    it.userId in userIds
                    it.status shouldBe AttendanceStatus.PENDING
                }
            }
        }

        feature("세션 삭제") {

            scenario("존재하지 않는 세션에 대한 삭제를 요청하면 예외가 발생한다.") {
                // given
                val request = AdminSessionDeleteRequest(ids = listOf(UUID.randomUUID()))

                // when, then
                shouldThrowExactly<BusinessException> {
                    adminScheduleService.deleteSessions(request)
                }.error shouldBe ScheduleError.CONTAIN_IMPROPER_ID_FOR_DELETE_SESSION
            }

            scenario("세션 삭제 시, 출석 정보도 함께 삭제된다.") {
                // given
                val sessions = listOf(getSessionEntityFixture(), getSessionEntityFixture())
                    .also { scheduleRepository.saveAll(it) }
                val users = userRepository.saveAll(listOf(getUserEntityFixture(), getUserEntityFixture()))
                listOf(
                    AttendanceEntity(userId = users[0].id, scheduleId = sessions[0].id),
                    AttendanceEntity(userId = users[0].id, scheduleId = sessions[1].id),
                    AttendanceEntity(userId = users[1].id, scheduleId = sessions[1].id)
                ).also { attendanceRepository.saveAll(it) }

                // when
                adminScheduleService.deleteSessions(AdminSessionDeleteRequest(ids = sessions.map { it.id }))

                // then
                attendanceRepository.findAllByScheduleId(sessions[0].id).shouldHaveSize(0)
                attendanceRepository.findAllByScheduleId(sessions[1].id).shouldHaveSize(0)
            }
        }

        feature("세션 상세 조회") {

            scenario("참석자 정보가 포함된다") {
                val user1 = getUserEntityFixture(name = "김개똥")
                val user1ActivityUnits = listOf(
                    getActivityUnitEntityFixture(generation = 25, position = Position.PM, userId = user1.id),
                    getActivityUnitEntityFixture(generation = 25, position = Position.STAFF, userId = user1.id),
                    getActivityUnitEntityFixture(generation = 24, position = Position.SERVER, userId = user1.id)
                )
                val user2 = getUserEntityFixture(name = "홍길동")
                val user2ActivityUnits = listOf(
                    getActivityUnitEntityFixture(generation = 25, position = Position.ANDROID, userId = user2.id)
                )
                val session = getSessionEntityFixture()
                val attendances = listOf(
                    getAttendanceEntityFixture(userId = user1.id, scheduleId = session.id),
                    getAttendanceEntityFixture(userId = user2.id, scheduleId = session.id)
                )

                userRepository.saveAll(listOf(user1, user2))
                activityUnitRepository.saveAll(user1ActivityUnits.union(user2ActivityUnits))
                scheduleRepository.save(session)
                attendanceRepository.saveAllAndFlush(attendances)

                val result = adminScheduleService.getSession(session.id)

                result.attendees.single { it.position == Position.PM }.let {
                    it.attendees.shouldHaveSize(1)
                    it.attendees[0].name shouldBe user1.name
                }
                result.attendees.single { it.position == Position.ANDROID }.let {
                    it.attendees.shouldHaveSize(1)
                    it.attendees[0].name shouldBe user2.name
                }
            }
        }
    })
