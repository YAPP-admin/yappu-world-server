package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.property.JwtProperty
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.JwtResolver
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.support.fixture.user.dto.getLatestSignUpApplicationAppRequestDtoFixture
import co.yappuworld.support.fixture.user.getSignUpApplication
import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.domain.vo.UserSignUpApplicationStatus
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserAuthSignUpApplicationServiceTest {

    private val jwtProperty = JwtProperty(
        "thisisforlocalsecretkeyonlyusinginlocalenvironmentthisisforlocalsecretkeyonlyusinginlocalenvironment",
        3600000,
        1209600000
    )
    private val userRepository = mockk<UserRepository>()
    private val authApplicationRepository = mockk<UserSignUpApplicationRepository>()
    private val jwtGenerator = JwtGenerator(jwtProperty)
    private val jwtResolver = JwtResolver(jwtProperty)
    private val configInquiryComponent = mockk<ConfigInquiryComponent>()
    private val userAuthService = UserAuthService(
        userRepository,
        authApplicationRepository,
        jwtGenerator,
        jwtResolver,
        configInquiryComponent
    )

    companion object {
        @JvmStatic
        private fun provideSignUpApplications(): List<SignUpApplication> {
            return listOf(
                getSignUpApplication(),
                getSignUpApplication().apply { reject("거절") },
                getSignUpApplication().apply { approve(UserRole.ADMIN) }
            )
        }
    }

    @Test
    fun `가장 최근 회원가입 신청이 존재하지 않으면 예외가 발생한다`() {
        every { authApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(any(), any()) } returns null

        val request = getLatestSignUpApplicationAppRequestDtoFixture()
        assertThatThrownBy { userAuthService.findLatestSignUpApplication(request) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageMatching(UserError.NO_SIGN_UP_APPLICATION.message)
    }

    @Test
    fun `회원가입 신청 시 입력한 비밀번호와 로그인 시도 시 입력한 비밀번호가 다르면 예외가 발생한다`() {
        val application = getSignUpApplication()
        every {
            authApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
                any(),
                any()
            )
        } returns application

        val request = getLatestSignUpApplicationAppRequestDtoFixture(
            email = application.applicantEmail,
            password = application.applicantDetails.password + "a"
        )

        assertThatThrownBy { userAuthService.findLatestSignUpApplication(request) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageMatching(UserError.MISMATCH_REQUEST_AND_SIGN_UP_APPLICATION.message)
    }

    @ParameterizedTest
    @MethodSource("provideSignUpApplications")
    fun `어플리케이션의 상태에 맞게 응답이 반환된다`(application: SignUpApplication) {
        every {
            authApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
                application.applicantEmail,
                any()
            )
        } returns application

        val request = getLatestSignUpApplicationAppRequestDtoFixture(
            email = application.applicantEmail,
            password = application.applicantDetails.password
        )

        userAuthService.findLatestSignUpApplication(request).also {
            assertThat(it.status).isEqualTo(application.status)
            when (it.status) {
                UserSignUpApplicationStatus.REJECTED -> assertNotNull(it.rejectReason)
                else -> assertNull(it.rejectReason)
            }
        }
    }
}
