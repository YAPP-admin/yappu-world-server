package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.ActivityUnit
import co.yappuworld.user.domain.Position
import co.yappuworld.user.domain.UserSignUpApplication
import co.yappuworld.user.domain.UserSignUpApplications
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.data.repository.findByIdOrNull

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserSignUpApplicationRepositoryConverterTest {

    @Autowired
    lateinit var userSignUpApplicationRepository: UserSignUpApplicationRepository

    @Test
    @DisplayName("UserSignUpApplication Custom Converter 정상 동작 확인")
    fun test() {
        val application = UserSignUpApplication(
            "abc@abc.com",
            "abc",
            "abc",
            listOf(ActivityUnit(0, Position.PM))
        ).let { userSignUpApplicationRepository.save(UserSignUpApplications(it)) }

        assertThat(checkNotNull(userSignUpApplicationRepository.findByIdOrNull(application.id)))
            .isInstanceOf(UserSignUpApplications::class.java)
    }
}
