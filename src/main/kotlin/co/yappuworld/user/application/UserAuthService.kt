package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.user.application.dto.request.LoginAppRequestDto
import co.yappuworld.user.application.dto.request.UserSignUpAppRequestDto
import co.yappuworld.user.domain.SignUpApplication
import co.yappuworld.user.domain.UserRole
import co.yappuworld.user.domain.UserSignUpApplicationStatus
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Limit
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger { }

@Service
class UserAuthService(
    private val userRepository: UserRepository,
    private val userSignUpApplicationRepository: UserSignUpApplicationRepository,
    private val jwtGenerator: JwtGenerator,
    private val configInquiryComponent: ConfigInquiryComponent
) {

    @Transactional
    fun submitSignUpRequest(
        request: UserSignUpAppRequestDto,
        now: LocalDateTime
    ) {
        validateApplication(request.email)

        SignUpApplication(request.toSignUpApplication()).let {
            userSignUpApplicationRepository.save(it)
        }
    }

    @Transactional
    fun signUpWithCode(
        request: UserSignUpAppRequestDto,
        now: LocalDateTime
    ): Token {
        val role = getUserRoleWithSignUpCode(request.signUpCode)

        return userRepository.save(request.toUser(role)).let { user ->
            val securityUser = SecurityUser.fromUser(user)
            jwtGenerator.generateToken(securityUser, now)
        }
    }

    @Transactional
    fun login(
        request: LoginAppRequestDto,
        now: LocalDateTime
    ): Token {
        return userRepository.findUserOrNullByEmail(request.email)?.let {
            jwtGenerator.generateToken(SecurityUser.fromUser(it), now)
        } ?: processLoginException(request.email)
    }

    private fun validateApplication(email: String) {
        if (userRepository.existsUserByEmail(email)) {
            logger.error { "${email}은 이미 가입된 이메일입니다." }
            throw BusinessException(UserError.ALREADY_SIGNED_UP_EMAIL)
        }

        val applications = userSignUpApplicationRepository.findByApplicantEmailAndStatus(
            email,
            UserSignUpApplicationStatus.PENDING
        )

        if (applications.isEmpty()) {
            return
        }

        if (applications.any { it.status == UserSignUpApplicationStatus.PENDING }) {
            logger.error { "${email}의 처리되지 않은 기존 신청이 존재합니다." }
            throw BusinessException(UserError.UNPROCESSED_APPLICATION_EXISTS)
        }
    }

    private fun getUserRoleWithSignUpCode(signUpCode: String): UserRole {
        val configs = configInquiryComponent.findConfigsBy(
            listOf("authenticationCodeAdmin", "authenticationCodeAlumni", "authenticationCodeActive")
        )

        val config = configs.singleOrNull { it.value == signUpCode }
            ?: throw BusinessException(UserError.INVALID_SIGN_UP_CODE)

        return when (config.id) {
            "authenticationCodeAdmin" -> UserRole.ADMIN
            "authenticationCodeAlumni" -> UserRole.ALUMNI
            else -> UserRole.ACTIVE
        }
    }

    private fun processLoginException(email: String): Nothing {
        val recentApplication = userSignUpApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
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
