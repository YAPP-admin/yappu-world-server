package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.jpa.ScheduleRepository
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantRepository
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import org.springframework.beans.factory.annotation.Autowired

class SessionParticipantCommandServiceTest @Autowired constructor(
    private val sessionParticipantRepository: SessionParticipantRepository,
    private val sessionRepository: ScheduleRepository,
    private val activityUnitRepository: ActivityUnitRepository
) : CustomDataJpaTestFeatureSpec({

        val sessionParticipantCommandService = SessionParticipantCommandService(sessionParticipantRepository)

        fun saveSessionAndActivityUnit(
            sessions: List<SessionEntity>,
            activityUnits: List<ActivityUnitEntity>
        ) {
            sessionRepository.saveAll(sessions)
            activityUnitRepository.saveAll(activityUnits)
        }

        feature("특정 세션의 모든 참가자 삭제") {

            scenario("세션 참가자가 없더라도 예외가 발생하지 않는다.") {
                shouldNotThrowAny {
                    sessionParticipantCommandService.deleteAllSessionParticipants(getSessionEntityFixture())
                }
            }

            scenario("세션의 모든 참가자들이 삭제된다.") {
                val session1 = getSessionEntityFixture()
                val session2 = getSessionEntityFixture()
                val generationMember1 = getActivityUnitEntityFixture()
                val generationMember2 = getActivityUnitEntityFixture()
                saveSessionAndActivityUnit(
                    listOf(session1, session2),
                    listOf(generationMember1, generationMember2)
                )
                sessionParticipantRepository.saveAllAndFlush(
                    listOf(
                        SessionParticipantEntity(session1, generationMember1),
                        SessionParticipantEntity(session1, generationMember2),
                        SessionParticipantEntity(session2, generationMember1),
                        SessionParticipantEntity(session2, generationMember2)
                    )
                )

                sessionParticipantCommandService.deleteAllSessionParticipants(session1)

                val result = sessionParticipantRepository.findAll()
                result.shouldHaveSize(2)
                result.shouldForAll { it.session.id != session1.id }
            }
        }

        feature("여러 세션의 참가자들을 삭제") {

            scenario("여러 세션의 참가자들을 모두 삭제한다.") {
                val session1 = getSessionEntityFixture()
                val session2 = getSessionEntityFixture()
                val generationMember1 = getActivityUnitEntityFixture()
                val generationMember2 = getActivityUnitEntityFixture()
                saveSessionAndActivityUnit(
                    listOf(session1, session2),
                    listOf(generationMember1, generationMember2)
                )
                sessionParticipantRepository.saveAllAndFlush(
                    listOf(
                        SessionParticipantEntity(session1, generationMember1),
                        SessionParticipantEntity(session1, generationMember2),
                        SessionParticipantEntity(session2, generationMember1),
                        SessionParticipantEntity(session2, generationMember2)
                    )
                )

                sessionParticipantCommandService.deleteAllSessionParticipants(listOf(session1, session2))

                val result = sessionParticipantRepository.findAll()
                result.shouldBeEmpty()
            }
        }
    })
