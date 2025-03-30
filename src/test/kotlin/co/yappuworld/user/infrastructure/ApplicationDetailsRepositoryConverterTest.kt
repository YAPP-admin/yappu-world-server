package co.yappuworld.user.infrastructure

import co.yappuworld.support.fixture.user.UserFixture.getApplicationDetailsFixture
import co.yappuworld.support.fixture.user.UserFixture.getSignUpApplicationFixture
import co.yappuworld.user.domain.model.SignUpApplicationEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Limit
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApplicationDetailsRepositoryConverterTest {

    @Autowired
    lateinit var signUpApplicationRepository: SignUpApplicationRepository

    @Test
    fun `UserSignUpApplication Custom Converter 정상 동작 확인`() {
        val application = signUpApplicationRepository.save(getSignUpApplicationFixture())

        assertThat(checkNotNull(signUpApplicationRepository.findByIdOrNull(application.id)))
            .isInstanceOf(SignUpApplicationEntity::class.java)
    }

    @Test
    fun `가장 최근에 신청된 신청서를 조회한다`() {
        val firstDetails = getApplicationDetailsFixture()
        val firstApplication = SignUpApplicationEntity(firstDetails).also {
            it.reject("거절")
            signUpApplicationRepository.save(it)
        }

        val secondApplication = SignUpApplicationEntity(firstDetails).also {
            signUpApplicationRepository.save(it)
        }

        val findApplication = signUpApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
            firstDetails.email,
            Limit.of(1)
        )

        assertThat(secondApplication.id).isEqualTo(assertNotNull(findApplication).id)
    }

    @Test
    fun `회원가입 신청을 한 적이 없다면 Null을 반환한다`() {
        val findApplication = signUpApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
            "abc@abc.com",
            Limit.of(1)
        )

        assertNull(findApplication)
    }
}
