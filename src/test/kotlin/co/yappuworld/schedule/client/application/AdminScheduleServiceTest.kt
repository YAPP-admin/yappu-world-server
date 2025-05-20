package co.yappuworld.schedule.client.application

import co.yappuworld.schedule.client.dto.request.AdminSessionDeleteRequest
import co.yappuworld.schedule.infrastructure.jpa.ScheduleRepository
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantRepository
import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import co.yappuworld.support.fixture.ScheduleDtoFixture.getAdminSessionCreateRequestFixture
import co.yappuworld.support.fixture.ScheduleDtoFixture.getAdminSessionUpdateRequestFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

class AdminScheduleServiceTest @Autowired constructor(
    private val adminScheduleService: AdminScheduleService,
    private val sessionRepository: ScheduleRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val sessionParticipantRepository: SessionParticipantRepository
) : SpringBootTestFeatureSpec({

        feature("세션 생성") {

            scenario("세션을 생성할 때 참여자 목록이 null이면 해당 기수의 유저들도 모두 참석 처리된다.") {
                val activityUnit = getActivityUnitEntityFixture(generation = 25, position = Position.SERVER)
                val request = getAdminSessionCreateRequestFixture(generation = 25)
                activityUnitRepository.saveAndFlush(activityUnit)
                adminScheduleService.createSession(request)

                val result = sessionParticipantRepository.findAll()
                result.shouldHaveSize(1)
                result[0].activityUnit.id shouldBe activityUnit.id
            }
        }

        feature("세션 수정") {

            scenario("세션 참가자 목록에 따라 기존 참여자가 삭제, 유지되거나 신규 참가자가 등록된다.") {
                val session = sessionRepository.save(getSessionEntityFixture(generation = 25))
                val activityUnits = activityUnitRepository.saveAll(
                    List(3) { getActivityUnitEntityFixture(generation = 25, position = Position.SERVER) }
                )
                sessionParticipantRepository.saveAllAndFlush(
                    listOf(
                        SessionParticipantEntity(session, activityUnits[0]),
                        SessionParticipantEntity(session, activityUnits[1])
                    )
                )

                val request = getAdminSessionUpdateRequestFixture(
                    id = session.id,
                    participantIds = listOf(activityUnits[1].userId, activityUnits[2].userId)
                )
                adminScheduleService.updateSession(request)

                val result = sessionParticipantRepository.findAll()
                result.shouldHaveSize(2)
                result.shouldForAll { it.activityUnit.id != activityUnits[0].id }
            }
        }

        feature("세션 삭제") {

            scenario("세션을 삭제하면 해당 세션 참석자 데이터도 모두 삭제된다.") {
                val session1 = getSessionEntityFixture(generation = 25)
                val session2 = getSessionEntityFixture(generation = 25)
                val activityUnit1 = getActivityUnitEntityFixture(generation = 25, position = Position.SERVER)
                val activityUnit2 = getActivityUnitEntityFixture(generation = 25, position = Position.SERVER)

                sessionRepository.saveAllAndFlush(listOf(session1, session2))
                activityUnitRepository.saveAllAndFlush(listOf(activityUnit1, activityUnit2))
                sessionParticipantRepository.saveAllAndFlush(
                    listOf(
                        SessionParticipantEntity(session1, activityUnit1),
                        SessionParticipantEntity(session1, activityUnit2),
                        SessionParticipantEntity(session2, activityUnit1),
                        SessionParticipantEntity(session2, activityUnit2)
                    )
                )

                adminScheduleService.deleteSession(AdminSessionDeleteRequest(listOf(session1, session2).map { it.id }))

                sessionParticipantRepository.findAll().shouldBeEmpty()
            }
        }
    })
