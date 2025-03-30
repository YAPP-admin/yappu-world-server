package co.yappuworld.user.infrastructure

import co.yappuworld.support.fixture.user.UserFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activityUnitRepository: ActivityUnitJpaRepository

    @Test
    @Transactional
    fun `요청한 것보다 많은 개수의 데이터가 있다면 요청 개수만큼 반환한다`() {
        repeat(12) {
            val user = userRepository.save(UserFixture.getUserFixture(email = "email$it@naver.com"))
            activityUnitRepository.save(UserFixture.getActivityUnit(userId = user.id))
        }

        val users = userRepository.findUsersWithActivityUnit(10, 0)
        assertThat(users).hasSize(10)
    }

    @Test
    @Transactional
    fun `두 페이지를 조회했을 때 중복되는 데이터는 없다`() {
        repeat(20) {
            val user = userRepository.save(UserFixture.getUserFixture(email = "email$it@naver.com"))
            activityUnitRepository.save(UserFixture.getActivityUnit(userId = user.id))
        }

        val distinctUserIds = mutableSetOf<UUID>().apply {
            addAll(userRepository.findUsersWithActivityUnit(10, 0).map { it.userId })
            addAll(userRepository.findUsersWithActivityUnit(10, 10).map { it.userId })
        }

        assertThat(distinctUserIds).hasSize(20)
    }
}
