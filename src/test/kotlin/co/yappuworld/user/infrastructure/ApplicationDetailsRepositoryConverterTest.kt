package co.yappuworld.user.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.domain.entity.SignUpApplicationEntity
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

@CustomDataJpaTest
class ApplicationDetailsRepositoryConverterTest {

    @Autowired
    lateinit var signUpApplicationRepository: SignUpApplicationRepository

    @Test
    fun `UserSignUpApplication Custom Converter 정상 동작 확인`() {
        val application = signUpApplicationRepository.save(getSignUpApplicationEntityFixture())

        assertThat(checkNotNull(signUpApplicationRepository.findByIdOrNull(application.id)))
            .isInstanceOf(SignUpApplicationEntity::class.java)
    }
}
