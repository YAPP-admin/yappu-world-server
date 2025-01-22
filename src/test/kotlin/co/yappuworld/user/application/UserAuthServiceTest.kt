package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.property.JwtProperty
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.JwtResolver
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.support.fixture.getUserFixture
import co.yappuworld.user.application.dto.request.ActivityUnitAppRequestDto
import co.yappuworld.user.application.dto.request.ReissueTokenAppRequestDto
import co.yappuworld.user.application.dto.request.UserSignUpAppRequestDto
import co.yappuworld.user.domain.SignUpApplication
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import co.yappuworld.user.presentation.vo.PositionApiType
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.test.Test

private val logger = KotlinLogging.logger { }

class UserAuthServiceTest {

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

    val email = "abc@abc.com"
    val request = UserSignUpAppRequestDto(
        email,
        "password",
        "name",
        listOf(ActivityUnitAppRequestDto(1, PositionApiType.PM)),
        ""
    )

    @Test
    @DisplayName("기존에 처리되지 않은 신청이 있다면 예외가 발생한다.")
    fun validateExistsPendingApplication() {
        every { userRepository.existsUserByEmail(any()) } returns false
        every {
            authApplicationRepository.findByApplicantEmailAndStatus(email, any())
        } returns listOf(SignUpApplication(request.toSignUpApplication()))

        assertThatThrownBy { userAuthService.submitSignUpRequest(request, LocalDateTime.now()) }
            .isInstanceOf(BusinessException::class.java)
            .message().isEqualTo(UserError.UNPROCESSED_APPLICATION_EXISTS.message)
    }

    @Test
    @DisplayName("이미 가입된 이메일이면 예외가 발생한다.")
    fun validateExistsApprovedApplication() {
        every { userRepository.existsUserByEmail(any()) } returns true

        assertThatThrownBy { userAuthService.submitSignUpRequest(request, LocalDateTime.now()) }
            .isInstanceOf(BusinessException::class.java)
            .message().isEqualTo(UserError.ALREADY_SIGNED_UP_EMAIL.message)
    }

    @Test
    @DisplayName("만료되지 않은 토큰 재발급이 정상적으로 처리된다.")
    fun successReissueValidAccessToken() {
        val user = getUserFixture()
        every { userRepository.findByIdOrNull(any()) } returns user
        val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))

        val reissuedToken = jwtGenerator.generateToken(SecurityUser.from(user), now)
            .run {
                userAuthService.reissueToken(
                    ReissueTokenAppRequestDto(accessToken, refreshToken!!, now.plusHours(1L).minusNanos(1L))
                )
            }

        assertDoesNotThrow {
            checkNotNull(jwtResolver.extractSecurityUserOrNull(reissuedToken.accessToken))
        }
    }

    @Test
    @DisplayName("만료된 토큰도 재발급이 정상적으로 처리된다.")
    fun successReissueExpiredAccessToken() {
        val user = getUserFixture()
        every { userRepository.findByIdOrNull(any()) } returns user
        val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))

        val token = jwtGenerator.generateToken(SecurityUser.from(user), now.minusHours(1L))
            .apply {
                assertThatThrownBy { jwtResolver.extractSecurityUserOrNull(this.accessToken) }
                    .isInstanceOf(ExpiredJwtException::class.java)
            }

        val reissuedToken = userAuthService.reissueToken(
            ReissueTokenAppRequestDto(token.accessToken, token.refreshToken!!, now)
        )

        assertDoesNotThrow {
            checkNotNull(jwtResolver.extractSecurityUserOrNull(reissuedToken.accessToken))
        }
    }
}
