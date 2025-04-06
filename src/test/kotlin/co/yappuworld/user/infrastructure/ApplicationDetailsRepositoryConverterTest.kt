package co.yappuworld.user.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.fixture.user.UserFixture.getApplicationDetailsFixture
import co.yappuworld.support.fixture.user.UserFixture.getSignUpApplicationFixture
import co.yappuworld.user.domain.model.SignUpApplicationEntity
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@CustomDataJpaTest
class ApplicationDetailsRepositoryConverterTest {

    @Autowired
    lateinit var signUpApplicationRepository: SignUpApplicationRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    fun `UserSignUpApplication Custom Converter 정상 동작 확인`() {
        val application = signUpApplicationRepository.save(getSignUpApplicationFixture())

        assertThat(checkNotNull(signUpApplicationRepository.findByIdOrNull(application.id)))
            .isInstanceOf(SignUpApplicationEntity::class.java)
    }

    @Test
    @Transactional
    fun `가장 최근에 신청된 신청서를 조회한다`() {
        val email = "abcdefghijklmnop@abc.com"
        val firstDetails = getApplicationDetailsFixture(email = email)
        val firstApplication = SignUpApplicationEntity(firstDetails).apply { reject("거절") }
        signUpApplicationRepository.save(firstApplication)

        val secondApplication = signUpApplicationRepository.save(SignUpApplicationEntity(firstDetails))

        entityManager.flush()
        entityManager.clear()

        val findApplication = assertNotNull(
            signUpApplicationRepository.findFirstByApplicantEmailOrderByUpdatedAtDesc(email)
        )

        println("First Application UpdatedAt: ${firstApplication.id}")
        println("First Application UpdatedAt: ${firstApplication.updatedAt}")
        println("Second Application UpdatedAt: ${secondApplication.id}")
        println("Second Application UpdatedAt: ${secondApplication.updatedAt}")
        println("Find Application UpdatedAt: ${findApplication.id}")
        println("Find Application UpdatedAt: ${findApplication.updatedAt}")

        assertThat(secondApplication.id).isEqualTo(findApplication.id)
    }

    @Test
    fun `회원가입 신청을 한 적이 없다면 Null을 반환한다`() {
        val findApplication = signUpApplicationRepository.findFirstByApplicantEmailOrderByUpdatedAtDesc("abc@abc.com")

        assertNull(findApplication)
    }
}
