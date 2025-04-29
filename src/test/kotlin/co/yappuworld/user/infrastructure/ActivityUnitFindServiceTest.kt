package co.yappuworld.user.infrastructure

import co.yappuworld.support.environment.CustomDataJpaFeatureSpec
import co.yappuworld.support.fixture.ActivityUnitFixture.getActivityUnitFixture
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class ActivityUnitFindServiceTest @Autowired constructor(
    private val activityUnitRepository: ActivityUnitRepository
) : CustomDataJpaFeatureSpec({

        val activityUnitFindService = ActivityUnitFindService(activityUnitRepository)

        feature("userId로 활동 기록 리스트 조회") {

            scenario("활동 기록이 없으면 빈 리스트를 반환한다.") {
                val userId = UUID.randomUUID()
                activityUnitFindService.findActivityUnits(userId).shouldBeEmpty()
            }

            scenario("활동 기록이 있으면 모두 반환한다.") {
                val userId = UUID.randomUUID()
                val activityUnits = listOf(
                    getActivityUnitFixture(generation = 22, position = Position.SERVER, userId = userId),
                    getActivityUnitFixture(generation = 23, position = Position.SERVER, userId = userId)
                )

                activityUnitRepository.saveAllAndFlush(activityUnits)

                activityUnitFindService
                    .findActivityUnits(userId)
                    .sortedBy { it.generation }
                    .forEachIndexed { index, activityUnit ->
                        activityUnit.id shouldBe activityUnits[index].id
                        activityUnit.generation shouldBe activityUnits[index].generation
                        activityUnit.position shouldBe activityUnits[index].position
                        activityUnit.userId shouldBe userId
                    }
            }
        }
    })
