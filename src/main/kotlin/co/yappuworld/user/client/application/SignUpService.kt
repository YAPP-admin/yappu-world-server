package co.yappuworld.user.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.operation.client.application.ConfigInquiryComponent
import co.yappuworld.user.client.dto.request.CheckingEmailAvailabilityRequest
import co.yappuworld.user.client.dto.request.LatestSignUpApplicationRequest
import co.yappuworld.user.client.dto.request.SignUpApplicationApproveRequest
import co.yappuworld.user.client.dto.request.SignUpApplicationRejectRequest
import co.yappuworld.user.client.dto.request.UserSignUpRequest
import co.yappuworld.user.client.dto.response.LatestSignUpApplicationResponse
import co.yappuworld.user.domain.model.SignUpApplicationEntity
import co.yappuworld.user.domain.model.UserAlarmSettingEntity
import co.yappuworld.user.domain.model.UserDeviceEntity
import co.yappuworld.user.domain.model.UserEntity
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.ActivityUnitJpaRepository
import co.yappuworld.user.infrastructure.SignUpApplicationRepository
import co.yappuworld.user.infrastructure.UserAlarmSettingRepository
import co.yappuworld.user.infrastructure.UserDeviceJpaRepository
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSystemNotifier
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Limit
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

private val logger = KotlinLogging.logger { }

@Service
class SignUpService(
    private val userRepository: UserRepository,
    private val signUpApplicationRepository: SignUpApplicationRepository,
    private val activityUnitJpaRepository: ActivityUnitJpaRepository,
    private val userAlarmSettingRepository: UserAlarmSettingRepository,
    private val userDeviceRepository: UserDeviceJpaRepository,
    private val jwtGenerator: JwtGenerator,
    private val configInquiryComponent: ConfigInquiryComponent,
    private val userSystemNotifier: UserSystemNotifier
) {

    @Transactional
    fun submitSignUpRequest(
        request: UserSignUpRequest,
        now: LocalDateTime
    ) {
        checkApplication(request.email)
        signUpApplicationRepository.save(request.toApplication()).also {
            userSystemNotifier.notifySignUpRequestReceived(it.id, request.name)
        }
    }

    @Transactional
    fun signUpWithCode(
        request: UserSignUpRequest,
        now: LocalDateTime
    ): Token {
        checkEmailDuplication(request.email)

        val user = initializeUser(
            request.toApplication(),
            getUserRoleWithSignUpCode(request.signUpCode!!)
        )

        return user.let {
            val securityUser = SecurityUser.from(it)
            jwtGenerator.generateToken(securityUser, now)
        }
    }

    @Transactional(readOnly = true)
    fun checkEmailAvailability(request: CheckingEmailAvailabilityRequest) {
        checkEmailDuplication(request.email)
    }

    @Transactional(readOnly = true)
    fun findLatestSignUpApplication(request: LatestSignUpApplicationRequest): LatestSignUpApplicationResponse {
        val signUpApplication = signUpApplicationRepository
            .findByApplicantEmailOrderByUpdatedAtDesc(
                request.email,
                Limit.of(1)
            )?.also { it.checkPassword(request.password) }
            ?: throw BusinessException(UserError.NO_SIGN_UP_APPLICATION)

        return LatestSignUpApplicationResponse(signUpApplication)
    }

    @Transactional
    fun approveSignUpApplication(request: SignUpApplicationApproveRequest) {
        val applications = getPendingApplications(request.applicationIds)

        applications.forEach { application ->
            checkEmailDuplication(application.applicantEmail)
            application.approve()

            signUpApplicationRepository.save(application)
            initializeUser(application, request.role)
        }
    }

    @Transactional
    fun rejectSignUpApplication(request: SignUpApplicationRejectRequest) {
        val applications = getPendingApplications(request.applicationIds)
        applications.forEach { it.reject(request.reason) }
        signUpApplicationRepository.saveAll(applications)
    }

    private fun initializeUser(
        application: SignUpApplicationEntity,
        role: UserRole
    ): UserEntity {
        val user = application.toUser(role)
        return userRepository.save(user).also {
            activityUnitJpaRepository.saveAll(application.toActivityUnits(it.id))
            userAlarmSettingRepository.save(UserAlarmSettingEntity(it.id, application.getDeviceAlarmToggle()))
            userDeviceRepository.save(UserDeviceEntity(it.id, application.getFcmToken()))
        }
    }

    private fun checkApplication(email: String) {
        checkEmailDuplication(email)

        val applications = signUpApplicationRepository.findByApplicantEmailAndStatus(
            email,
            SignUpApplicationStatus.PENDING
        )

        if (applications.isEmpty()) {
            return
        }

        if (applications.any { it.status == SignUpApplicationStatus.PENDING }) {
            logger.error { "${email}의 처리되지 않은 기존 신청이 존재합니다." }
            throw BusinessException(UserError.UNPROCESSED_APPLICATION_EXISTS)
        }
    }

    private fun checkEmailDuplication(email: String) {
        if (userRepository.existsUserByEmail(email)) {
            logger.error { "${email}은 이미 가입된 이메일입니다." }
            throw BusinessException(UserError.ALREADY_SIGNED_UP_EMAIL)
        }
    }

    private fun getUserRoleWithSignUpCode(signUpCode: String): UserRole {
        val configs = configInquiryComponent.findConfigsBy(
            listOf(
                "authenticationCodeAdmin",
                "authenticationCodeStaff",
                "authenticationCodeAlumni",
                "authenticationCodeActive"
            )
        )

        val config = configs.singleOrNull { it.value == signUpCode }
            ?: throw BusinessException(UserError.INVALID_SIGN_UP_CODE)

        return when (config.id) {
            "authenticationCodeAdmin" -> UserRole.ADMIN
            "authenticationCodeStaff" -> UserRole.STAFF
            "authenticationCodeAlumni" -> UserRole.ALUMNI
            else -> UserRole.ACTIVE
        }
    }

    private fun getPendingApplications(applicationIds: List<UUID>): List<SignUpApplicationEntity> {
        val applications = signUpApplicationRepository.findAllByIdIn(applicationIds)
        if (applicationIds.size != applications.size) {
            logger.error {
                """
                | 존재하지 않는 신청서 ID가 포함되어 있어서 처리에 실패했습니다.
                | 요청한 ID: $applicationIds
                | 조회한 ID: ${applications.map { it.id }}
                """.trimIndent()
            }
            throw BusinessException(UserError.CONTAIN_NOT_EXIST_APPLICATION_ID)
        }

        if (applications.any { it.status != SignUpApplicationStatus.PENDING }) {
            logger.error {
                """
                | 이미 처리된 신청서의 ID가 포함되어 있어서 처리에 실패했습니다.
                | 요청한 ID: $applicationIds
                | 조회한 ID: ${applications.map { it.id }}
                """.trimMargin()
            }
            throw BusinessException(UserError.CONTAIN_ALREADY_PROCESSED_APPLICATION)
        }

        return applications
    }
}
