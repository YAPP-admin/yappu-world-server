package co.yappuworld.user.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.support.environment.CustomDataJpaFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.util.UUID

class UserFindServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext,
    private val activityUnitRepository: ActivityUnitRepository
) : CustomDataJpaFeatureSpec({

        val userFindService = UserFindService(userRepository, entityManager, context)

        feature("유저 테이블에 이미 존재하는 이메일인지 확인") {

            scenario("존재하지 않는 이메일이면 FALSE") {
                userFindService.existsEmail("${UUID.randomUUID()}@email.com").shouldBeFalse()
            }

            scenario("존재하는 이메일이면 TRUE") {
                val email = "${UUID.randomUUID()}@email.com"
                userRepository.saveAndFlush(getUserEntityFixture(email = email))
                userFindService.existsEmail(email).shouldBeTrue()
            }
        }

        feature("유저를 조회하고 없으면 NULL 반환") {

            scenario("존재하지 않으면 NULL 반환") {
                userFindService.findUserOrNull(UUID.randomUUID()).shouldBeNull()
            }

            scenario("존재하면 유저 반환") {
                val user = getUserEntityFixture()
                userRepository.saveAndFlush(user)
                userFindService.findUserOrNull(user.id).shouldNotBeNull()
            }
        }

        feature("유저를 조회하고 없으면 예외 발생") {

            scenario("존재하지 않으면 예외 발생") {
                shouldThrowExactly<BusinessException> {
                    userFindService.findUser(UUID.randomUUID())
                }.error shouldBe UserError.USER_NOT_FOUND
            }

            scenario("존재하면 유저 반환") {
                val user = getUserEntityFixture()
                userRepository.saveAndFlush(user)
                shouldNotThrowAny { userFindService.findUser(user.id) }
            }
        }

        feature("이메일로 유저 검색, 없으면 NULL") {

            scenario("존재하지 않으면 NULL") {
                userFindService
                    .findUserOrNull("${UUID.randomUUID()}@email.com")
                    .shouldBeNull()
            }

            scenario("존재하면 유저 반환") {
                val email = "${UUID.randomUUID()}@email.com"
                val user = getUserEntityFixture(email = email)
                userRepository.saveAndFlush(user)
                userFindService.findUserOrNull(email).shouldNotBeNull()
            }
        }

        feature("유저 ID 목록으로 조회") {

            scenario("목록이 비어있으면 예외 발생") {
                shouldThrowExactly<IllegalArgumentException> {
                    userFindService.findAllByIdIn(emptyList())
                }
            }

            scenario("존재하지 않는 ID만 있으면 빈 리스트 반환") {
                userFindService.findAllByIdIn(listOf(UUID.randomUUID())).shouldBeEmpty()
            }

            scenario("존재하는 ID의 유저만 반환") {
                val user = getUserEntityFixture()
                userRepository.saveAndFlush(user)

                val users = userFindService.findAllByIdIn(listOf(user.id, UUID.randomUUID()))
                users.size shouldBe 1
                users.first().id shouldBe user.id
            }

            scenario("모두 존재하는 ID면 개수 맞춰 반환") {
                val users = List(2) { getUserEntityFixture() }
                userRepository.saveAllAndFlush(users)

                userFindService.findAllByIdIn(users.map { it.id }).let { result ->
                    result.shouldHaveSize(2)
                    result.map { it.id }.shouldContainInOrder(users.map { u -> u.id })
                }
            }
        }

        feature("유저와 마지막 활동 내역 조회") {

            scenario("유저가 존재하지 않으면 예외 발생") {
                shouldThrowExactly<BusinessException> {
                    userFindService.findUserWithLastActivityUnit(UUID.randomUUID())
                }.error shouldBe UserError.USER_NOT_FOUND
            }

            scenario("유저가 존재하지만 활동 이력이 없으면 예외 발생") {
                val user = getUserEntityFixture()
                userRepository.saveAndFlush(user)

                shouldThrowExactly<BusinessException> {
                    userFindService.findUserWithLastActivityUnit(user.id)
                }.error shouldBe UserError.USER_NOT_FOUND
            }

            scenario("유저가 하나의 활동 이력을 가지고 있으면 그걸 반환") {
                val user = getUserEntityFixture()
                userRepository.saveAndFlush(user)
                activityUnitRepository.save(
                    getActivityUnitEntityFixture(generation = 25, position = Position.ANDROID, userId = user.id)
                )

                userFindService.findUserWithLastActivityUnit(user.id).let {
                    it.userId shouldBe user.id
                    it.lastActiveGeneration shouldBe 25
                    it.lastActivePosition shouldBe Position.ANDROID
                }
            }

            scenario("유저가 두 개 이상의 활동 이력을 가지고 있으면, 최근 활동 이력을 반환") {
                val user = getUserEntityFixture()
                userRepository.saveAndFlush(user)
                activityUnitRepository.saveAll(
                    listOf(
                        getActivityUnitEntityFixture(generation = 24, position = Position.SERVER, userId = user.id),
                        getActivityUnitEntityFixture(generation = 25, position = Position.ANDROID, userId = user.id)
                    )
                )

                userFindService.findUserWithLastActivityUnit(user.id).let {
                    it.userId shouldBe user.id
                    it.lastActiveGeneration shouldBe 25
                    it.lastActivePosition shouldBe Position.ANDROID
                }
            }
        }

        feature("여러 유저를 마지막 활동 내역과 함께 조회") {

            scenario("빈 리스트로 요청하면 예외가 발생한다.") {
                shouldThrowExactly<IllegalArgumentException> {
                    userFindService.findAllUserWithLastActivityUnit(emptyList())
                }
            }

            scenario("존재하지 않는 유저만 조회하면 빈 리스트 반환") {
                userFindService.findAllUserWithLastActivityUnit(listOf(UUID.randomUUID())).shouldBeEmpty()
            }

            scenario("존재하는 것과 존재하지 않는 것이 함께 요청되면, 존재하는 것만 반환") {
                val user = getUserEntityFixture()
                userRepository.saveAndFlush(user)
                activityUnitRepository.save(
                    getActivityUnitEntityFixture(generation = 25, position = Position.ANDROID, userId = user.id)
                )

                userFindService.findAllUserWithLastActivityUnit(listOf(user.id, UUID.randomUUID())).let {
                    it.shouldHaveSize(1)
                    it.first().userId shouldBe user.id
                    it.first().lastActiveGeneration shouldBe 25
                    it.first().lastActivePosition shouldBe Position.ANDROID
                }
            }

            scenario("존재하는 두 명의 유저지만, 활동 내역이 존재하는 유저만 반환") {
                val userHasActivityUnit = getUserEntityFixture()
                val userWithoutActivityUnit = getUserEntityFixture()
                userRepository.saveAllAndFlush(listOf(userHasActivityUnit, userWithoutActivityUnit))
                activityUnitRepository.save(
                    getActivityUnitEntityFixture(
                        generation = 24,
                        position = Position.SERVER,
                        userId = userHasActivityUnit.id
                    )
                )

                userFindService
                    .findAllUserWithLastActivityUnit(listOf(userHasActivityUnit.id, userWithoutActivityUnit.id))
                    .let { result ->
                        result.shouldHaveSize(1)
                        result.first().let { user ->
                            user.userId shouldBe userHasActivityUnit.id
                            user.lastActiveGeneration shouldBe 24
                            user.lastActivePosition shouldBe Position.SERVER
                        }
                    }
            }
        }

        feature("유저의 마지막 활동 내역을 페이지로 조회") {

            scenario("데이터가 없으면 빈 페이지 반환") {
                userFindService.findAllUserWithLastActivityUnit(PageRequest.of(0, 10)).let {
                    it.content.shouldHaveSize(0)
                    it.number shouldBe 0
                    it.totalElements shouldBe 0
                    it.totalPages shouldBe 0
                    it.totalElements shouldBe 0
                }
            }

            scenario("5명에 10개씩 1페이지 조회, 그 중 한 명은 활동 이력이 없어서 제외") {
                val users = List(5) { getUserEntityFixture() }.sortedBy { it.id }
                val activityUnits = (0..3).map { i -> getActivityUnitEntityFixture(userId = users[i].id) }
                userRepository.saveAllAndFlush(users)
                activityUnitRepository.saveAll(activityUnits)

                userFindService
                    .findAllUserWithLastActivityUnit(PageRequest.of(0, 10))
                    .let { result ->
                        result.content.shouldHaveSize(4)
                        result.content
                            .map { it.userId }
                            .sortedBy { it }
                            .shouldContainInOrder(users.subList(0, 3).map { it.id })
                        result.numberOfElements shouldBe 4
                        result.number shouldBe 0
                        result.totalElements shouldBe 4
                        result.totalPages shouldBe 1
                    }
            }

            scenario("11명에 10개씩 2페이지로 조회, 모두의 이력이 존재") {
                val users = List(11) { getUserEntityFixture() }.sortedBy { it.id }
                val activityUnits = (0..10).map { i -> getActivityUnitEntityFixture(userId = users[i].id) }
                userRepository.saveAllAndFlush(users)
                activityUnitRepository.saveAllAndFlush(activityUnits)

                userFindService
                    .findAllUserWithLastActivityUnit(PageRequest.of(1, 10))
                    .let { result ->
                        result.content.shouldHaveSize(1)
                        result.content.last().userId shouldBeIn users.map { it.id }
                        result.numberOfElements shouldBe 1
                        result.number shouldBe 1
                        result.totalElements shouldBe 11
                        result.totalPages shouldBe 2
                    }
            }
        }

        feature("유저의 모든 이력 조회") {

            scenario("존재하지 않는 유저인 경우, 예외가 발생한다.") {
                shouldThrowExactly<BusinessException> {
                    userFindService.findUserWithActivities(UUID.randomUUID())
                }.error shouldBe UserError.USER_NOT_FOUND
            }

            scenario("유저는 존재하지만, 활동 기록이 없으면 예외가 발생한다.") {
                val user = getUserEntityFixture()
                userRepository.saveAndFlush(user)

                shouldThrowExactly<BusinessException> {
                    userFindService.findUserWithActivities(user.id)
                }.error shouldBe UserError.USER_NOT_FOUND
            }

            scenario("활동 기록이 하나만 있는 경우") {
                val user = getUserEntityFixture()
                userRepository.saveAndFlush(user)
                val activityUnit =
                    getActivityUnitEntityFixture(generation = 25, position = Position.ANDROID, userId = user.id)
                activityUnitRepository.save(activityUnit)

                shouldNotThrowAny { userFindService.findUserWithActivities(user.id) }
                    .activityUnits.size shouldBe 1
            }

            scenario("활동 기록이 여러 개 있는 경우") {
                val user = getUserEntityFixture()
                userRepository.saveAndFlush(user)
                val activityUnits = listOf(
                    getActivityUnitEntityFixture(generation = 25, position = Position.ANDROID, userId = user.id),
                    getActivityUnitEntityFixture(generation = 24, position = Position.SERVER, userId = user.id)
                )
                activityUnitRepository.saveAll(activityUnits)

                val result = shouldNotThrowAny { userFindService.findUserWithActivities(user.id) }
                result.activityUnits.size shouldBe 2
                result.activityGenerations shouldContainInOrder listOf(25, 24)
            }
        }

        feature("특정 기수에 활동한 모든 유저를 조회한다.") {

            scenario("해당 기수에 활동한 유저가 없다면 빈 리스트를 반환한다.") {
                userFindService.findUsersActiveOfGeneration(99).shouldBeEmpty()
            }

            scenario("특정 기수에 활동한 기록이 있는 모든 유저를 조회한다.") {
                val user1 = getUserEntityFixture().also {
                    userRepository.saveAndFlush(it)
                    activityUnitRepository.saveAllAndFlush(
                        listOf(
                            getActivityUnitEntityFixture(generation = 99, position = Position.PM, userId = it.id),
                            getActivityUnitEntityFixture(generation = 98, position = Position.PM, userId = it.id)
                        )
                    )
                }
                val user2 = getUserEntityFixture().also {
                    userRepository.saveAndFlush(it)
                    activityUnitRepository.saveAllAndFlush(
                        listOf(
                            getActivityUnitEntityFixture(generation = 99, position = Position.SERVER, userId = it.id),
                            getActivityUnitEntityFixture(generation = 100, position = Position.SERVER, userId = it.id)
                        )
                    )
                }

                val userIds = listOf(user1.id, user2.id).sorted()
                val result = userFindService
                    .findUsersActiveOfGeneration(99)
                val resultByUserId = result.associateBy { it.userId }

                result.shouldHaveSize(2)
                userIds.forEach { userId ->
                    val userWithActivityUnit = resultByUserId[userId].shouldNotBeNull()
                    userWithActivityUnit.generation shouldBe 99
                }
            }
        }
    })
