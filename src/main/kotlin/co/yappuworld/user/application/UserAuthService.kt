package co.yappuworld.user.application

import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.global.vo.UserRole
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.user.application.dto.request.UserSignUpAppRequestDto
import co.yappuworld.user.domain.UserSignUpApplicationStatus
import co.yappuworld.user.domain.UserSignUpApplications
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
        validateExistApplication(request.email)

        UserSignUpApplications(request.toSignUpApplication()).let {
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

    private fun validateExistApplication(email: String) {
        val applications = userSignUpApplicationRepository.findByApplicantEmailAndStatusIn(
            email,
            listOf(UserSignUpApplicationStatus.PENDING, UserSignUpApplicationStatus.APPROVED)
        )

        if (applications.isEmpty()) {
            return
        }

        if (applications.any { it.status == UserSignUpApplicationStatus.APPROVED }) {
            logger.error { "${email}의 기존 요청이 이미 승인되었습니다." }
            throw IllegalStateException("${email}의 기존 요청이 이미 승인되었습니다.")
        }

        if (applications.any { it.status == UserSignUpApplicationStatus.PENDING }) {
            logger.error { "${email}의 처리되지 않은 기존 신청이 존재합니다." }
            throw IllegalStateException("${email}의 처리되지 않은 기존 신청이 존재합니다.")
        }
    }

    private fun getUserRoleWithSignUpCode(signUpCode: String): UserRole {
        val configs = configInquiryComponent.findConfigsByKey(
            listOf("authenticationCodeAdmin", "authenticationCodeAlumni", "authenticationCodeActive")
        )

        val config = configs.singleOrNull { it.value == signUpCode }
            ?: throw NoSuchElementException("요청한 가입코드에 매칭되는 권한이 없습니다.")

        return when (config.value) {
            "authenticationCodeAdmin" -> UserRole.ADMIN
            "authenticationCodeAlumni" -> UserRole.ALUMNI
            else -> UserRole.ACTIVE
        }
    }
}
