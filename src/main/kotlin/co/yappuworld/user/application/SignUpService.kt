package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.user.application.dto.request.CheckingEmailAvailabilityAppRequestDto
import co.yappuworld.user.application.dto.request.LatestSignUpApplicationAppRequestDto
import co.yappuworld.user.application.dto.request.SignUpApplicationApproveAppRequestDto
import co.yappuworld.user.application.dto.request.SignUpApplicationRejectAppRequestDto
import co.yappuworld.user.application.dto.request.UserSignUpAppRequestDto
import co.yappuworld.user.application.dto.response.LatestSignUpApplicationAppResponseDto
import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.model.User
import co.yappuworld.user.domain.model.UserAlarmSetting
import co.yappuworld.user.domain.model.UserDevice
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.domain.vo.UserSignUpApplicationStatus
import co.yappuworld.user.infrastructure.ActivityUnitRepository
import co.yappuworld.user.infrastructure.UserAlarmSettingRepository
import co.yappuworld.user.infrastructure.UserDeviceRepository
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import co.yappuworld.user.infrastructure.UserSystemNotifier
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Limit
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger { }

@Service
class SignUpService(
    private val userRepository: UserRepository,
    private val signUpApplicationRepository: UserSignUpApplicationRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val userAlarmSettingRepository: UserAlarmSettingRepository,
    private val userDeviceRepository: UserDeviceRepository,
    private val jwtGenerator: JwtGenerator,
    private val configInquiryComponent: ConfigInquiryComponent,
    private val userSystemNotifier: UserSystemNotifier
) {

    @Transactional
    fun submitSignUpRequest(
        request: UserSignUpAppRequestDto,
        now: LocalDateTime
    ) {
        checkApplication(request.email)
        signUpApplicationRepository.save(request.toApplication()).also {
            userSystemNotifier.notifySignUpRequestReceived(it.id, request.name)
        }
    }

    @Transactional
    fun signUpWithCode(
        request: UserSignUpAppRequestDto,
        now: LocalDateTime
    ): Token {
        val user = initializeUser(
            request.toApplication(),
            getUserRoleWithSignUpCode(request.signUpCode)
        )

        return user.let {
            val securityUser = SecurityUser.from(it)
            jwtGenerator.generateToken(securityUser, now)
        }
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
        val signUpApplication = signUpApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
            request.email,
            Limit.of(1)
        )?.apply { checkPassword(request.password) }
            ?: throw BusinessException(UserError.NO_SIGN_UP_APPLICATION)

        return LatestSignUpApplicationAppResponseDto.of(signUpApplication)
    }

    @Transactional
    fun approveSignUpApplication(request: SignUpApplicationApproveAppRequestDto) {
        val application = signUpApplicationRepository.findByIdOrNull(request.applicationId)
            ?.apply { approve() }
            ?: throw BusinessException(UserError.NOT_FOUND_SIGN_UP_APPLICATION)

        signUpApplicationRepository.save(application)
        initializeUser(application, request.role)
    }

    @Transactional
    fun rejectSignUpApplication(request: SignUpApplicationRejectAppRequestDto) {
        val application = signUpApplicationRepository.findByIdOrNull(request.applicationId)
            ?.apply { reject(request.reason) }
            ?: throw BusinessException(UserError.NOT_FOUND_SIGN_UP_APPLICATION)

        signUpApplicationRepository.save(application)
    }

    private fun initializeUser(
        application: SignUpApplication,
        role: UserRole
    ): User {
        val user = application.toUser(role)
        return userRepository.save(user).also {
            activityUnitRepository.saveAll(application.toActivityUnits(it.id))
            userAlarmSettingRepository.save(UserAlarmSetting(it.id, application.getDeviceAlarmToggle()))
            userDeviceRepository.save(UserDevice(it.id, application.getFcmToken()))
        }
    }

    private fun checkApplication(email: String) {
        if (userRepository.existsUserByEmail(email)) {
            logger.error { "${email}은 이미 가입된 이메일입니다." }
            throw BusinessException(UserError.ALREADY_SIGNED_UP_EMAIL)
        }

        val applications = signUpApplicationRepository.findByApplicantEmailAndStatus(
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
