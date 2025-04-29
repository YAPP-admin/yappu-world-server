package co.yappuworld.user.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.JwtResolver
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.user.client.application.usecase.UserLoginPermissionChecker
import co.yappuworld.user.client.dto.request.LatestSignUpApplicationRequest
import co.yappuworld.user.client.dto.request.LoginRequest
import co.yappuworld.user.client.dto.request.ReissueTokenRequest
import co.yappuworld.user.client.dto.response.LatestSignUpApplicationResponse
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.SignUpApplicationFindService
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class UserAuthService(
    private val jwtGenerator: JwtGenerator,
    private val jwtResolver: JwtResolver,
    private val userFindService: UserFindService,
    private val signUpApplicationFindService: SignUpApplicationFindService,
    private val userLoginPermissionChecker: UserLoginPermissionChecker
) {

    @Transactional(readOnly = true)
    fun findLatestSignUpApplication(request: LatestSignUpApplicationRequest): LatestSignUpApplicationResponse {
        val signUpApplication = signUpApplicationFindService.findLatestSignUpApplication(request.email)
            ?: throw BusinessException(UserError.NO_SIGN_UP_APPLICATION)

        signUpApplication.checkPassword(request.password)

        return LatestSignUpApplicationResponse(signUpApplication)
    }

    @Transactional
    fun login(
        request: LoginRequest,
        now: LocalDateTime
    ): Token =
        userFindService
            .findUserOrNull(request.email)
            .let { userOrNull ->
                userLoginPermissionChecker.checkLoginAvailability(
                    userOrNull,
                    request.email,
                    request.password
                )
                checkNotNull(userOrNull)
            }.let { user -> jwtGenerator.generateToken(SecurityUser.from(user), now) }

    @Transactional
    fun reissueToken(
        request: ReissueTokenRequest,
        now: LocalDateTime
    ): Token {
        val userId = jwtResolver.extractUserIdFrom(request.accessToken)
        val user = userFindService.findUserOrNull(userId)
            ?: throw BusinessException(UserError.FAIL_LOGIN_NOT_FOUND_USER)

        if (!user.isActive) {
            throw BusinessException(UserError.WITHDRAWN_USER)
        }

        return jwtGenerator.generateToken(SecurityUser.from(user), now)
    }

    @Transactional
    fun withdrawUser(userId: UUID) {
        userFindService
            .findUser(userId)
            .apply { withdraw() }
    }
}
