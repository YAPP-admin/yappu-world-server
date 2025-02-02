package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.ActivityUnitParam
import co.yappuworld.user.domain.model.ApplicationDetails
import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.vo.Position
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.data.domain.Limit
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = ["test"])
class ApplicationDetailsRepositoryConverterTest {

    @Autowired
    lateinit var userSignUpApplicationRepository: UserSignUpApplicationRepository

    @Test
    fun `UserSignUpApplication Custom Converter 정상 동작 확인`() {
        val application = ApplicationDetails(
            "abc@abc.com",
            "abc",
            "abc",
            listOf(ActivityUnitParam(0, Position.PM)),
            "fcmToken"
        ).let { userSignUpApplicationRepository.save(SignUpApplication(it)) }

        assertThat(checkNotNull(userSignUpApplicationRepository.findByIdOrNull(application.id)))
            .isInstanceOf(SignUpApplication::class.java)
    }

    @Test
    fun `가장 최근에 신청된 신청서를 조회한다`() {
        val firstDetails = ApplicationDetails(
            "abc@abc.com",
            "abc",
            "abc",
            listOf(ActivityUnitParam(0, Position.PM)),
            "fcmToken"
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
    fun `회원가입 신청을 한 적이 없다면 Null을 반환한다`() {
        val findApplication = userSignUpApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
            "abc@abc.com",
            Limit.of(1)
        )

        assertNull(findApplication)
    }
}
