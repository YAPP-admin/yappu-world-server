package co.yappuworld.user.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class ActivityUnitFindServiceTest @Autowired constructor(
    private val activityUnitRepository: ActivityUnitRepository
) : CustomDataJpaTestFeatureSpec({

        val activityUnitFindService = ActivityUnitFindService(activityUnitRepository)

        feature("userId로 활동 기록 리스트 조회") {

            scenario("활동 기록이 없으면 빈 리스트를 반환한다.") {
                val userId = UUID.randomUUID()
                activityUnitFindService.findActivityUnits(userId).shouldBeEmpty()
            }

            scenario("활동 기록이 있으면 모두 반환한다.") {
                val userId = UUID.randomUUID()
                val activityUnits = listOf(
                    getActivityUnitEntityFixture(generation = 22, position = Position.SERVER, userId = userId),
                    getActivityUnitEntityFixture(generation = 23, position = Position.SERVER, userId = userId)
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

        feature("기수 내의 참여자 조회") {

            scenario("존재하지 않으면 빈 리스트를 반환한다.") {
                activityUnitFindService.findParticipants(25).shouldBeEmpty()
            }

            scenario("다른 기수 기록은 조회되지 않는다.") {
                activityUnitRepository.saveAllAndFlush(
                    listOf(
                        getActivityUnitEntityFixture(generation = 25, position = Position.STAFF),
                        getActivityUnitEntityFixture(generation = 24, position = Position.PM)
                    )
                )
                activityUnitFindService.findParticipants(23).shouldBeEmpty()
            }

            scenario("기수 내에 STAFF가 아닌 기록만 조회한다.") {
                activityUnitRepository.saveAllAndFlush(
                    listOf(
                        getActivityUnitEntityFixture(generation = 25, position = Position.STAFF),
                        getActivityUnitEntityFixture(generation = 25, position = Position.PM)
                    )
                )
                activityUnitFindService.findParticipants(25).let {
                    it.shouldHaveSize(1)
                    it[0].position shouldBe Position.PM
                }
            }
        }

        feature("유저 목록 내의 기수 참여자 조회") {

            scenario("존재하지 않으면 빈 리스트를 반환한다.") {
                val userIds = listOf(UUID.randomUUID())
                activityUnitFindService.findParticipants(25, userIds).shouldBeEmpty()
            }

            scenario("다른 기수 기록은 조회되지 않는다.") {
                val userId = UUID.randomUUID()
                activityUnitRepository.saveAllAndFlush(
                    listOf(
                        getActivityUnitEntityFixture(generation = 25, position = Position.SERVER, userId = userId),
                        getActivityUnitEntityFixture(generation = 24, position = Position.PM, userId = userId)
                    )
                )
                activityUnitFindService.findParticipants(23, listOf(userId)).shouldBeEmpty()
            }

            scenario("기수 내에 STAFF가 아닌 기록만 조회한다.") {
                val userId = UUID.randomUUID()
                activityUnitRepository.saveAllAndFlush(
                    listOf(
                        getActivityUnitEntityFixture(generation = 25, position = Position.STAFF, userId = userId),
                        getActivityUnitEntityFixture(generation = 25, position = Position.PM, userId = userId)
                    )
                )
                activityUnitFindService.findParticipants(25, listOf(userId)).let {
                    it.shouldHaveSize(1)
                    it[0].position shouldBe Position.PM
                }
            }

            scenario("다른 유저의 기록은 조회되지 않는다.") {
                val userId = UUID.randomUUID()
                val otherUserId = UUID.randomUUID()
                activityUnitRepository.saveAllAndFlush(
                    listOf(
                        getActivityUnitEntityFixture(generation = 25, position = Position.STAFF, userId = userId),
                        getActivityUnitEntityFixture(generation = 25, position = Position.WEB, userId = userId),
                        getActivityUnitEntityFixture(generation = 25, position = Position.PM, userId = otherUserId)
                    )
                )
                activityUnitFindService.findParticipants(25, listOf(userId)).let {
                    it.shouldHaveSize(1)
                    it[0].userId shouldBe userId
                    it[0].position shouldBe Position.WEB
                }
            }
        }
    })
