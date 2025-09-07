package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired

class AttendanceFindServiceTest @Autowired constructor(
    private val sessionRepository: ScheduleRepository,
    private val attendanceRepository: AttendanceRepository,
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext
) : CustomDataJpaTestFeatureSpec({

        val attendanceFindService = AttendanceFindService(attendanceRepository, entityManager, context)

        feature("특정 기수의 출석 정보를 조회한다.") {

            scenario("출석 정보가 없으면 빈 리스트를 반환한다.") {
                attendanceFindService
                    .findAttendancesOfGeneration(99)
                    .shouldBeEmpty()
            }

            scenario("특정 기수의 세션과 연계된 출석 정보만 조회한다.") {
                val generation = 99
                val sessions = mutableListOf<SessionEntity>()
                val attendances = mutableListOf<AttendanceEntity>()
                repeat(3) {
                    sessions.addAll(List(3) { getSessionEntityFixture(generation = generation + it) })
                }
                sessions.forEach { session ->
                    attendances.add(getAttendanceEntityFixture(session = session))
                }

                sessionRepository.saveAllAndFlush(sessions)
                attendanceRepository.saveAllAndFlush(attendances)

                val result = attendanceFindService.findAttendancesOfGeneration(generation)
                result.shouldHaveSize(3)
            }
        }

        feature("세션 참석자를 조회한다.") {

            scenario("세션 참석자 조회 시, 해당 세션에 참석한 직군 자격으로 조회된다.") {
                val user1 = getUserEntityFixture()
                val user1ActivityUnits = listOf(
                    getActivityUnitEntityFixture(generation = 25, position = Position.PM, userId = user1.id),
                    getActivityUnitEntityFixture(generation = 25, position = Position.STAFF, userId = user1.id),
                    getActivityUnitEntityFixture(generation = 24, position = Position.SERVER, userId = user1.id)
                )
                val user2 = getUserEntityFixture()
                val user2ActivityUnits = listOf(
                    getActivityUnitEntityFixture(generation = 25, position = Position.STAFF, userId = user2.id)
                )
                val session = getSessionEntityFixture()
                val attendance = getAttendanceEntityFixture(userId = user1.id, session = session)

                userRepository.saveAll(listOf(user1, user2))
                activityUnitRepository.saveAll(user1ActivityUnits.union(user2ActivityUnits))
                sessionRepository.save(session)
                attendanceRepository.saveAndFlush(attendance)

                val result = attendanceFindService.findAttendees(session.id)
                result.shouldHaveSize(1)
                result[0].let {
                    it.userId shouldBe user1.id
                    it.name shouldBe user1.name
                    it.generation shouldBe session.generation
                    it.position shouldBe Position.PM
                    it.sessionId shouldBe session.id
                }
            }
        }
    })
