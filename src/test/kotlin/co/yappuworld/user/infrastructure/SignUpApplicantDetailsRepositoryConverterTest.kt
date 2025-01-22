package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.Position
import co.yappuworld.user.domain.model.SignUpApplicantDetails
import co.yappuworld.user.domain.model.SignUpApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.data.domain.Limit
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SignUpApplicantDetailsRepositoryConverterTest {

    @Autowired
    lateinit var userSignUpApplicationRepository: UserSignUpApplicationRepository

    @Test
    @DisplayName("UserSignUpApplication Custom Converter 정상 동작 확인")
    fun testCustomConverter() {
        val application = SignUpApplicantDetails(
            "abc@abc.com",
            "abc",
            "abc",
            listOf(ActivityUnit(0, Position.PM))
        ).let { userSignUpApplicationRepository.save(SignUpApplication(it)) }

        assertThat(checkNotNull(userSignUpApplicationRepository.findByIdOrNull(application.id)))
            .isInstanceOf(SignUpApplication::class.java)
    }

    @Test
    @DisplayName("가장 최근에 신청된 신청서를 조회한다.")
    fun testFindingApplicationByApplicantEmailLimit1() {
        val firstDetails = SignUpApplicantDetails(
            "abc@abc.com",
            "abc",
            "abc",
            listOf(ActivityUnit(0, Position.PM))
        )
        val firstApplication = SignUpApplication(firstDetails).also { it.reject("거절") }
        val secondApplication = SignUpApplication(firstDetails)

        userSignUpApplicationRepository.save(firstApplication)
        userSignUpApplicationRepository.save(secondApplication)

        val findApplication = userSignUpApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
            firstDetails.email,
            Limit.of(1)
        )

        assertThat(firstApplication.id).isEqualTo(assertNotNull(findApplication).id)
    }

    @Test
    @DisplayName("회원가입 신청을 한 적이 없다면 Null을 반환한다.")
    fun testCannotFindingApplicationByApplicantEmailLimit1() {
        val findApplication = userSignUpApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
            "abc@abc.com",
            Limit.of(1)
        )

        assertNull(findApplication)
    }
}
