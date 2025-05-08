package co.yappuworld.user.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.ActivityUnitFixture.getActivityUnitFixture
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class ActivityUnitCommandServiceTest @Autowired constructor(
    private val activityUnitRepository: ActivityUnitRepository
) : CustomDataJpaTestFeatureSpec({

        val activityUnitCommandService = ActivityUnitCommandService(activityUnitRepository)

        feature("saveAll") {

            scenario("저장한 데이터대로 조회된다") {
                val userId = UUID.randomUUID()
                val activityUnits = listOf(
                    getActivityUnitFixture(generation = 22, position = Position.SERVER, userId = userId),
                    getActivityUnitFixture(generation = 23, position = Position.IOS, userId = userId)
                )

                activityUnitCommandService.saveAll(activityUnits)

                val result = activityUnitRepository.findAllByUserId(userId).sortedBy { it.generation }
                result.shouldContainInOrder(activityUnits)
            }

            scenario("저장한 데이터가 영속화된다.") {
                val userId = UUID.randomUUID()
                val activityUnits = listOf(
                    getActivityUnitFixture(generation = 22, position = Position.SERVER, userId = userId),
                    getActivityUnitFixture(generation = 23, position = Position.IOS, userId = userId)
                )

                activityUnits.forEach { activityUnit -> activityUnit.isNew shouldBe true }

                activityUnitCommandService.saveAll(activityUnits)
                activityUnitRepository.flush()

                activityUnits.forEach { activityUnit -> activityUnit.isNew shouldBe false }
            }

            scenario("빈 배열을 저장 요청하면 예외가 발생한다.") {
                shouldThrowExactly<IllegalArgumentException> {
                    activityUnitCommandService.saveAll(emptyList())
                }
            }
        }

        feature("ID로 삭제") {

            scenario("삭제 후에는 조회가 되지 않는다.") {
                val userId = UUID.randomUUID()
                val activityUnits = activityUnitRepository.saveAllAndFlush(
                    listOf(
                        getActivityUnitFixture(generation = 22, position = Position.SERVER, userId = userId),
                        getActivityUnitFixture(generation = 23, position = Position.IOS, userId = userId)
                    )
                )

                activityUnitCommandService.deleteAll(activityUnits.map { it.id })

                activityUnitRepository.findAllById(activityUnits.map { it.id }).shouldBeEmpty()
            }

            scenario("삭제 대상 ID가 비어 있으면 예외가 발생한다.") {
                shouldThrowExactly<IllegalArgumentException> {
                    activityUnitCommandService.deleteAll(emptyList())
                }
            }
        }
    })
