package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.JwtResolver
import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.support.fixture.property.PropertyFixture.getJwtProperty
import co.yappuworld.support.fixture.user.UserDtoFixture.getLoginApiRequestDto
import co.yappuworld.support.fixture.user.UserFixture.getUserFixture
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.ActivityUnitRepository
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime

class UserLoginServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val userSignUpApplicationRepository = mockk<UserSignUpApplicationRepository>()
    private val activityUnitRepository = mockk<ActivityUnitRepository>()
    private val jwtGenerator = JwtGenerator(getJwtProperty())
    private val jwtResolver = mockk<JwtResolver>()
    private val configInquiryComponent = mockk<ConfigInquiryComponent>()
    private val userAuthService = UserAuthService(
        userRepository,
        userSignUpApplicationRepository,
        activityUnitRepository,
        jwtGenerator,
        jwtResolver,
        configInquiryComponent
    )

    @Test
    fun `로그인 성공`() {
        val plainPassword = "abcabC!!"
        val user = getUserFixture(password = EncryptUtils.encrypt(plainPassword))
        every { userRepository.findUserOrNullByEmail(user.email) } returns user

        assertDoesNotThrow {
            userAuthService.login(
                getLoginApiRequestDto(
                    user.email,
                    plainPassword
                ).toAppRequest(),
                LocalDateTime.now()
            )
        }
    }

    @Test
    fun `비밀번호가 다르면 로그인에 실패한다`() {
        val plainPassword = "abcabC!!"
        val user = getUserFixture(password = EncryptUtils.encrypt(plainPassword))
        every { userRepository.findUserOrNullByEmail(user.email) } returns user

        assertThatThrownBy {
            userAuthService.login(
                getLoginApiRequestDto(
                    user.email,
                    "abcabcabaB!!"
                ).toAppRequest(),
                LocalDateTime.now()
            )
        }.isInstanceOf(BusinessException::class.java)
            .hasMessageMatching(UserError.WRONG_LOGIN_USER_INFORMATION.message)
    }
}
