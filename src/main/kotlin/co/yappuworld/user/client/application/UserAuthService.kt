package co.yappuworld.user.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.JwtResolver
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.user.client.dto.request.LoginRequest
import co.yappuworld.user.client.dto.request.ReissueTokenRequest
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class UserAuthService(
    private val userRepository: UserRepository,
    private val jwtGenerator: JwtGenerator,
    private val jwtResolver: JwtResolver,
    private val userLoginPermissionChecker: UserLoginPermissionChecker
) {

    @Transactional
    fun login(
        request: LoginRequest,
        now: LocalDateTime
    ): Token {
        val user = userRepository
            .findUserOrNullByEmail(request.email)
            .let { userLoginPermissionChecker.checkPermissionAndGetUser(it, request.email, request.password) }

        return jwtGenerator.generateToken(SecurityUser.from(user), now)
    }

    @Transactional
    fun reissueToken(
        request: ReissueTokenRequest,
        now: LocalDateTime
    ): Token {
        val userId = jwtResolver.extractUserIdFrom(request.accessToken)
        val user = userRepository.findByIdOrNull(userId)
            ?: throw BusinessException(UserError.FAIL_LOGIN_NOT_FOUND_USER)

        if (!user.isActive) {
            throw BusinessException(UserError.WITHDRAWN_USER)
        }

        return jwtGenerator.generateToken(SecurityUser.from(user), now)
    }

    @Transactional
    fun withdrawUser(userId: UUID) {
        userRepository
            .findByIdOrNull(userId)
            ?.apply { withdraw() }
            ?.let(userRepository::save)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)
    }
}
