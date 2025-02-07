package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.property.JwtProperty
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.support.fixture.user.UserDtoFixture.getLatestSignUpApplicationAppRequestDtoFixture
import co.yappuworld.support.fixture.user.UserFixture.getApplicationDetailsFixture
import co.yappuworld.support.fixture.user.UserFixture.getSignUpApplicationFixture
import co.yappuworld.user.domain.model.ApplicationDetails
import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserSignUpApplicationStatus
import co.yappuworld.user.infrastructure.ActivityUnitRepository
import co.yappuworld.user.infrastructure.UserAlarmSettingRepository
import co.yappuworld.user.infrastructure.UserDeviceRepository
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

class SignUpServiceTest {

    private val jwtProperty = JwtProperty(
        "thisisforlocalsecretkeyonlyusinginlocalenvironmentthisisforlocalsecretkeyonlyusinginlocalenvironment",
        3600000,
        1209600000
    )
    private val userRepository = mockk<UserRepository>()
    private val authApplicationRepository = mockk<UserSignUpApplicationRepository>()
    private val activityUnitRepository = mockk<ActivityUnitRepository>()
    private val userDeviceRepository = mockk<UserDeviceRepository>()
    private val userAlarmSettingRepository = mockk<UserAlarmSettingRepository>()
    private val jwtGenerator = JwtGenerator(jwtProperty)
    private val configInquiryComponent = mockk<ConfigInquiryComponent>()
    private val signUpService = SignUpService(
        userRepository,
        authApplicationRepository,
        activityUnitRepository,
        userAlarmSettingRepository,
        jwtGenerator,
        configInquiryComponent
    )

    companion object {
        @JvmStatic
        private fun provideSignUpApplicationAndDetails(): List<Pair<SignUpApplication, ApplicationDetails>> {
            val details = getApplicationDetailsFixture()
            return listOf(
                Pair(getSignUpApplicationFixture(details), details),
                Pair(getSignUpApplicationFixture(details).apply { reject("거절") }, details),
                Pair(getSignUpApplicationFixture(details).apply { approve() }, details)
            )
        }
    }

    @Test
    fun `가장 최근 회원가입 신청이 존재하지 않으면 예외가 발생한다`() {
        every { authApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(any(), any()) } returns null

        val request = getLatestSignUpApplicationAppRequestDtoFixture()
        assertThatThrownBy { signUpService.findLatestSignUpApplication(request) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageMatching(UserError.NO_SIGN_UP_APPLICATION.message)
    }

    @Test
    fun `회원가입 신청 시 입력한 비밀번호와 로그인 시도 시 입력한 비밀번호가 다르면 예외가 발생한다`() {
        val details = getApplicationDetailsFixture()
        val application = SignUpApplication(details)
        every {
            authApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
                any(),
                any()
            )
        } returns application

        val request = getLatestSignUpApplicationAppRequestDtoFixture(
            email = application.applicantEmail,
            password = details.password + "a"
        )

        assertThatThrownBy { signUpService.findLatestSignUpApplication(request) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageMatching(UserError.MISMATCH_REQUEST_AND_SIGN_UP_APPLICATION.message)
    }

    @ParameterizedTest
    @MethodSource("provideSignUpApplicationAndDetails")
    fun `어플리케이션의 상태에 맞게 응답이 반환된다`(applicationAndDetails: Pair<SignUpApplication, ApplicationDetails>) {
        val application = applicationAndDetails.first
        val details = applicationAndDetails.second

        every {
            authApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
                application.applicantEmail,
                any()
            )
        } returns application

        val request = getLatestSignUpApplicationAppRequestDtoFixture(
            email = application.applicantEmail,
            password = details.password
        )

        signUpService.findLatestSignUpApplication(request).also {
            assertThat(it.status).isEqualTo(application.status)
            when (it.status) {
                UserSignUpApplicationStatus.REJECTED -> assertNotNull(it.rejectReason)
                else -> assertNull(it.rejectReason)
            }
        }
    }
}
