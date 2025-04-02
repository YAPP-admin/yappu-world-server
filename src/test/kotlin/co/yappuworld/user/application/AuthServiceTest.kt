package co.yappuworld.user.application

import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.JwtResolver
import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.support.fixture.property.PropertyFixture.getJwtProperty
import co.yappuworld.support.fixture.user.UserDtoFixture.getLoginRequest
import co.yappuworld.support.fixture.user.UserFixture.getUserFixture
import co.yappuworld.user.client.application.UserAuthService
import co.yappuworld.user.client.application.UserLoginPermissionChecker
import co.yappuworld.user.infrastructure.UserCommandService
import co.yappuworld.user.infrastructure.UserFindService
import co.yappuworld.user.infrastructure.jpa.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime

class AuthServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val userFindService = mockk<UserFindService>()
    private val userCommandService = mockk<UserCommandService>()
    private val userLoginPermissionChecker = mockk<UserLoginPermissionChecker>()
    private val jwtGenerator = JwtGenerator(getJwtProperty())
    private val jwtResolver = mockk<JwtResolver>()
    private val userAuthService = UserAuthService(
        userFindService = userFindService,
        userCommandService = userCommandService,
        jwtGenerator = jwtGenerator,
        jwtResolver = jwtResolver,
        userLoginPermissionChecker = userLoginPermissionChecker
    )

    @Test
    fun `로그인 성공`() {
        val plainPassword = "abcabC!!"
        val user = getUserFixture(password = EncryptUtils.encrypt(plainPassword))
        every { userFindService.findByEmailOrNull(user.email) } returns user
        every { userLoginPermissionChecker.checkPermissionAndGetUser(user, user.email, plainPassword) } returns user

        assertDoesNotThrow {
            userAuthService.login(
                getLoginRequest(
                    user.email,
                    plainPassword
                ),
                LocalDateTime.now()
            )
        }
    }
}
