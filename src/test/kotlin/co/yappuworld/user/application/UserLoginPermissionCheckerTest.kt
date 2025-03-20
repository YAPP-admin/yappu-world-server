package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.support.fixture.user.UserFixture.getUserFixture
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class UserLoginPermissionCheckerTest {

    private val authApplicationRepository = mockk<UserSignUpApplicationRepository>()
    private val userLoginPermissionChecker = UserLoginPermissionChecker(authApplicationRepository)

    @Test
    fun `비밀번호가 다르면 로그인에 실패한다`() {
        val plainPassword = "abcabC!!"
        val wrongPassword = "abcabCC!!"
        val user = getUserFixture(password = EncryptUtils.encrypt(plainPassword))

        assertThatThrownBy {
            userLoginPermissionChecker.checkPermissionAndGetUser(user, user.email, wrongPassword)
        }.isInstanceOf(BusinessException::class.java)
            .hasMessageMatching(UserError.WRONG_LOGIN_USER_INFORMATION.message)
    }
}
