package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.user.domain.UserRole
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.user.application.dto.request.UserSignUpAppRequestDto
import co.yappuworld.user.domain.UserSignUpApplicationStatus
import co.yappuworld.user.domain.SignUpApplication
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import io.github.oshai.kotlinlogging.KotlinLogging
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
}
