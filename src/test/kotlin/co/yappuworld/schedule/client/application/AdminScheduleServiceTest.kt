package co.yappuworld.schedule.client.application

import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantRepository
import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import co.yappuworld.support.fixture.ScheduleDtoFixture.getAdminSessionCreateRequestFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

class AdminScheduleServiceTest @Autowired constructor(
    private val adminScheduleService: AdminScheduleService,
    private val activityUnitRepository: ActivityUnitRepository,
    private val sessionParticipantRepository: SessionParticipantRepository
) : SpringBootTestFeatureSpec({

        feature("세션 생성") {

            scenario("세션을 생성할 때 해당 기수의 유저들도 모두 참석 처리된다.") {
                val activityUnit = getActivityUnitEntityFixture(generation = 25, position = Position.SERVER)
                val session = getAdminSessionCreateRequestFixture(generation = 25)
                activityUnitRepository.saveAndFlush(activityUnit)
                adminScheduleService.createSession(session)

                val result = sessionParticipantRepository.findAll()
                result.shouldHaveSize(1)
                result[0].activityUnit.id shouldBe activityUnit.id
            }
        }
    })
