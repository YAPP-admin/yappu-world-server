package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.JwtResolver
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.user.application.dto.request.CheckingEmailAvailabilityAppRequestDto
import co.yappuworld.user.application.dto.request.LatestSignUpApplicationAppRequestDto
import co.yappuworld.user.application.dto.request.LoginAppRequestDto
import co.yappuworld.user.application.dto.request.ReissueTokenAppRequestDto
import co.yappuworld.user.application.dto.request.UserSignUpAppRequestDto
import co.yappuworld.user.application.dto.response.LatestSignUpApplicationAppResponseDto
import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.domain.vo.UserSignUpApplicationStatus
import co.yappuworld.user.infrastructure.ActivityUnitRepository
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Limit
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger { }

@Service
class UserAuthService(
    private val userRepository: UserRepository,
    private val userSignUpApplicationRepository: UserSignUpApplicationRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val jwtGenerator: JwtGenerator,
    private val jwtResolver: JwtResolver,
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
        val user = request.toUser(role).also {
            activityUnitRepository.saveAll(request.toActivityUnits(it))
            userRepository.save(it)
        }

        return user.let {
            val securityUser = SecurityUser.from(it)
            jwtGenerator.generateToken(securityUser, now)
        }
    }

    @Transactional
    fun login(
        request: LoginAppRequestDto,
        now: LocalDateTime
    ): Token {
        return userRepository.findUserOrNullByEmail(request.email)
            ?.let {
                it.checkPassword(request.password)
                jwtGenerator.generateToken(SecurityUser.from(it), now)
            } ?: processLoginException(request.email)
    }

    @Transactional
    fun reissueToken(request: ReissueTokenAppRequestDto): Token {
        val userId = jwtResolver.extractUserIdFrom(request.accessToken)
        val userOrNull = userRepository.findByIdOrNull(userId)

        if (userOrNull == null) {
            logger.error { "$userId 유저를 찾을 수 없습니다." }
            throw BusinessException(UserError.FAIL_LOGIN_NOT_FOUND_USER)
        }

        // TODO - AT와 RT의 화이트리스트 또는 블랙리스트 전략 필요

        return jwtGenerator.generateToken(SecurityUser.from(userOrNull), request.now)
    }

    @Transactional(readOnly = true)
    fun checkEmailAvailability(request: CheckingEmailAvailabilityAppRequestDto) {
        if (userRepository.existsUserByEmail(request.email)) {
            throw BusinessException(UserError.DUPLICATE_EMAIL)
        }
    }

    @Transactional(readOnly = true)
    fun findLatestSignUpApplication(
        request: LatestSignUpApplicationAppRequestDto
    ): LatestSignUpApplicationAppResponseDto {
        val signUpApplication = userSignUpApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
            request.email,
            Limit.of(1)
        )?.apply { checkPassword(request.password) }
            ?: throw BusinessException(UserError.NO_SIGN_UP_APPLICATION)

        return LatestSignUpApplicationAppResponseDto.of(signUpApplication)
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
