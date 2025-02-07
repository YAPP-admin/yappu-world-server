package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.JwtResolver
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.user.application.dto.request.LoginAppRequestDto
import co.yappuworld.user.application.dto.request.ReissueTokenAppRequestDto
import co.yappuworld.user.domain.checkLoginAvailability
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserSignUpApplicationStatus
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Limit
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

private val logger = KotlinLogging.logger { }

@Service
class UserAuthService(
    private val userRepository: UserRepository,
    private val signUpApplicationRepository: UserSignUpApplicationRepository,
    private val jwtGenerator: JwtGenerator,
    private val jwtResolver: JwtResolver
) {

    @Transactional
    fun login(
        request: LoginAppRequestDto,
        now: LocalDateTime
    ): Token {
        val user = userRepository.findUserOrNullByEmail(request.email)
            ?.apply { this.checkLoginAvailability(request.password) }
            ?: processLoginException(request.email)

        return jwtGenerator.generateToken(SecurityUser.from(user), now)
    }

    @Transactional
    fun reissueToken(request: ReissueTokenAppRequestDto): Token {
        val userId = jwtResolver.extractUserIdFrom(request.accessToken)
        val user = userRepository.findByIdOrNull(userId)
            ?: throw BusinessException(UserError.FAIL_LOGIN_NOT_FOUND_USER)

        if (!user.isActive) {
            throw BusinessException(UserError.WITHDRAWN_USER)
        }

        return jwtGenerator.generateToken(SecurityUser.from(user), request.now)
    }

    @Transactional
    fun withdrawUser(userId: UUID) {
        userRepository.findByIdOrNull(userId)
            ?.apply { withdraw() }
            ?.let(userRepository::save)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)
    }

    private fun processLoginException(email: String): Nothing {
        val recentApplication = signUpApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
            email,
            Limit.of(1)
        )

        if (recentApplication == null) {
            logger.error { "${email}로 가입 시도조차 없습니다." }
            throw BusinessException(UserError.FAIL_LOGIN_NOT_FOUND_USER)
        }

        throw when (recentApplication.status) {
            UserSignUpApplicationStatus.PENDING -> {
                logger.error { "${email}의 기존 가입 신청이 처리되지 않았습니다." }
                BusinessException(UserError.CANNOT_LOGIN_WITH_UNPROCESSED_SIGN_UP_APPLICATION)
            }
            UserSignUpApplicationStatus.REJECTED -> {
                logger.error { "${email}의 기존 가입 신청이 처리되지 않았습니다." }
                BusinessException(UserError.RECENT_SIGN_UP_APPLICATION_REJECTED)
            }
            UserSignUpApplicationStatus.APPROVED -> {
                logger.error { "${email}의 가입 프로세스에 문제가 생겼습니다." }
                BusinessException(UserError.CANNOT_LOGIN_WRONG_USER_STATE)
            }
        }
    }
}
