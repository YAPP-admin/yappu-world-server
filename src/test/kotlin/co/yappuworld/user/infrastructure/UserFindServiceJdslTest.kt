package co.yappuworld.user.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.fixture.user.UserFixture.getActivityUnit
import co.yappuworld.support.fixture.user.UserFixture.getUserFixture
import co.yappuworld.user.domain.model.ActivityUnitEntity
import co.yappuworld.user.domain.model.UserEntity
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@CustomDataJpaTest
class UserFindServiceJdslTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activityUnitRepository: ActivityUnitRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var context: JpqlRenderContext

    lateinit var userFindService: UserFindService
    val users = mutableListOf<UserEntity>()
    val activityUnits = mutableListOf<ActivityUnitEntity>()
    val generationAndPosition = listOf(
        25 to Position.PM,
        24 to Position.ANDROID,
        23 to Position.SERVER
    )

    @BeforeTest
    fun setUp() {
        userFindService = UserFindService(userRepository, entityManager, context)
        val generationAndPosition = listOf(
            25 to Position.PM,
            24 to Position.ANDROID,
            23 to Position.SERVER
        )

        generationAndPosition.forEach { (generation, position) ->
            val user = userRepository
                .save(getUserFixture())
                .also { users.add(it) }
            activityUnits.add(
                activityUnitRepository.save(
                    getActivityUnit(generation = generation, position = position, userId = user.id)
                )
            )
        }
    }

    @AfterTest
    fun tearDown() {
        userRepository.deleteAll(users)
        activityUnitRepository.deleteAll(activityUnits)
    }

    @Test
    @Transactional
    fun `가장 최근의 활동 내역이 조회된다`() {
        val user = userRepository.save(getUserFixture())
        val lastActivityUnit = getActivityUnit(generation = 25, position = Position.ANDROID, userId = user.id)
        activityUnitRepository.saveAll(
            listOf(
                getActivityUnit(generation = 24, position = Position.SERVER, userId = user.id),
                lastActivityUnit
            )
        )

        val userWithActivityUnit = userFindService.findUserWithLastActivityUnit(user.id)
        assertEquals(userWithActivityUnit.generation, lastActivityUnit.generation)
    }

    @Test
    @Transactional
    fun `여러 명의 최근 활동 내역도 정상적으로 조회된다`() {
        userFindService
            .findAllUserWithLastActivityUnit(PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id")))
            .sortedByDescending { it.generation }
            .forEachIndexed { index, userWithLastActivityUnit ->
                assertEquals(userWithLastActivityUnit.generation, generationAndPosition[index].first)
                assertEquals(userWithLastActivityUnit.position, generationAndPosition[index].second)
            }
    }

    @Test
    @Transactional
    fun `몇 명의 유저가 지정된 경우 해당 유저들의 정보가 조회된다`() {
        userFindService
            .findAllUserWithLastActivityUnit(users.map { it.id })
            .sortedByDescending { it.generation }
            .forEachIndexed { index, userWithLastActivityUnit ->
                assertEquals(userWithLastActivityUnit.generation, generationAndPosition[index].first)
                assertEquals(userWithLastActivityUnit.position, generationAndPosition[index].second)
            }
    }

    @Test
    @Transactional
    fun `존재하지 않는 유저가 조회 대상에 포함되어 있는 경우, 존재하는 유저만 조회된다`() {
        val fakeUserId = UUID.randomUUID()
        val results = userFindService.findAllUserWithLastActivityUnit(users.map { it.id }.plus(fakeUserId))
        assertThat(results.map { it.userId }).doesNotContain(fakeUserId)
    }
}
