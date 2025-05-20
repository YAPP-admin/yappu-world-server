package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.jpa.ScheduleRepository
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantRepository
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class SessionParticipantFindServiceTest @Autowired constructor(
    private val sessionParticipantRepository: SessionParticipantRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val sessionRepository: ScheduleRepository,
    private val userRepository: UserRepository
) : CustomDataJpaTestFeatureSpec({

        val sessionParticipantFindService = SessionParticipantFindService(sessionParticipantRepository)

        feature("세션 참가자 조회") {

            scenario("참가자가 없으면 빈 리스트를 반환한다.") {
                sessionParticipantFindService.findSessionParticipantEntieis(UUID.randomUUID()).shouldBeEmpty()
            }

            scenario("특정 세션의 모든 참가자들을 조회한다.") {
                val session1 = getSessionEntityFixture()
                val session2 = getSessionEntityFixture()
                val activityUnit1 = getActivityUnitEntityFixture()
                val activityUnit2 = getActivityUnitEntityFixture()
                sessionRepository.saveAll(listOf(session1, session2))
                activityUnitRepository.saveAll(listOf(activityUnit1, activityUnit2))

                sessionParticipantRepository.saveAllAndFlush(
                    listOf(
                        SessionParticipantEntity(session1, activityUnit1),
                        SessionParticipantEntity(session1, activityUnit2),
                        SessionParticipantEntity(session2, activityUnit1),
                        SessionParticipantEntity(session2, activityUnit2)
                    )
                )

                sessionParticipantFindService
                    .findSessionParticipantEntieis(session1.id)
                    .shouldForAll { it.session.id shouldBe session1.id }
            }
        }

        feature("세션 참가자를 도메인 모델로 조회") {

            scenario("지정한 세션 ID에 매칭되는 대상만 조회된다.") {
                val session1 = getSessionEntityFixture()
                val session2 = getSessionEntityFixture()
                val user1 = getUserEntityFixture()
                val user2 = getUserEntityFixture()
                val activityUnit1 = getActivityUnitEntityFixture(userId = user1.id)
                val activityUnit2 = getActivityUnitEntityFixture(userId = user2.id)
                sessionRepository.saveAll(listOf(session1, session2))
                userRepository.saveAll(listOf(user1, user2))
                activityUnitRepository.saveAll(listOf(activityUnit1, activityUnit2))

                sessionParticipantRepository.saveAllAndFlush(
                    listOf(
                        SessionParticipantEntity(session1, activityUnit1),
                        SessionParticipantEntity(session1, activityUnit2),
                        SessionParticipantEntity(session2, activityUnit1),
                        SessionParticipantEntity(session2, activityUnit2)
                    )
                )

                val sessionParticipants = sessionParticipantFindService.findSessionParticipants(session1.id)
                sessionParticipants.shouldForAll {
                    it.sessionId shouldBe session1.id
                    it.userId shouldBeIn listOf(user1.id, user2.id)
                }
            }
        }
    })
