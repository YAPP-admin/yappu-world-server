package co.yappuworld.user.infrastructure

import co.yappuworld.support.fixture.user.UserFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.transaction.annotation.Transactional

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activityUnitRepository: ActivityUnitRepository

    @Test
    @Transactional
    fun `유저가 없으면 빈 배열이 반환된다`() {
        assertThat(userRepository.findUsersWithActivityUnit(10, 0)).isEmpty()
    }

    @Test
    @Transactional
    fun `유저가 있으면 조건에 해당하는 배열이 반환된다`() {
        repeat(22) {
            val user = userRepository.save(UserFixture.getUserFixture(email = "email$it@naver.com"))
            activityUnitRepository.save(UserFixture.getActivityUnit(userId = user.id))
        }

        val users = userRepository.findUsersWithActivityUnit(10, 0)
        assertThat(users).hasSize(10)
    }
}
