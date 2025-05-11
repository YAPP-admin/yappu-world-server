package co.yappuworld.user.client.application.usecase

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.SignUpApplicationCommandService
import co.yappuworld.user.infrastructure.SignUpApplicationFindService
import co.yappuworld.user.infrastructure.UserCommandService
import co.yappuworld.user.infrastructure.UserFindService
import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

private val logger = KotlinLogging.logger { }

@Component
@Transactional
class SignUpExecutor(
    private val userFindService: UserFindService,
    private val userCommandService: UserCommandService,
    private val signUpApplicationFindService: SignUpApplicationFindService,
    private val signUpApplicationCommandService: SignUpApplicationCommandService,
    private val jwtGenerator: JwtGenerator
) {

    fun submit(application: SignUpApplicationEntity) {
        executeWithLock(application.applicantEmail) {
            checkSubmitApplicationAvailability(application)
            signUpApplicationCommandService.submit(application)
        }
    }

    fun signUp(
        application: SignUpApplicationEntity,
        role: UserRole,
        now: LocalDateTime
    ): Token =
        executeWithLockReturning(application.applicantEmail) {
            checkEmailAvailability(application.applicantEmail)
            val user = userCommandService.signUp(application, role)
            rejectPendingSignUpApplication(application.applicantEmail)
            jwtGenerator.generateToken(SecurityUser.from(user), now)
        }

    private fun rejectPendingSignUpApplication(applicantEmail: String) {
        signUpApplicationFindService
            .findPendingApplicationOrNull(applicantEmail)
            ?.apply { reject(reason = "가입 코드를 통해 회원가입을 완료하였습니다.") }
            ?.also { signUpApplicationCommandService.update(it) }
    }

    fun approve(
        applicationIds: List<UUID>,
        role: UserRole
    ) {
        getPendingApplications(applicationIds)
            .onEach { application ->
                checkEmailAvailability(application.applicantEmail)
                application.approve()
                userCommandService.signUp(application, role)
            }
    }

    fun reject(
        applicationIds: List<UUID>,
        reason: String? = null
    ) {
        getPendingApplications(applicationIds)
            .onEach { it.reject(reason) }
    }

    fun checkEmailAvailability(email: String) {
        if (userFindService.existsEmail(email)) {
            logger.warn { "${email}은 이미 가입된 이메일입니다." }
            throw BusinessException(UserError.ALREADY_SIGNED_UP_EMAIL)
        }
    }

    private fun checkSubmitApplicationAvailability(application: SignUpApplicationEntity) {
        checkEmailAvailability(application.applicantEmail)

        if (signUpApplicationFindService.existsPendingApplication(application.applicantEmail)) {
            logger.warn { "${application.applicantEmail}의 처리되지 않은 기존 신청이 존재합니다." }
            throw BusinessException(UserError.UNPROCESSED_APPLICATION_EXISTS)
        }
    }

    private fun getPendingApplications(applicationIds: List<UUID>): List<SignUpApplicationEntity> {
        val applications = signUpApplicationFindService.findSignUpApplications(applicationIds)

        if (applicationIds.size != applications.size) {
            logger.warn {
                buildString {
                    appendLine("존재하지 않는 신청서 ID가 포함되어 있어서 처리에 실패했습니다.")
                    appendLine("요청한 ID: $applicationIds")
                    appendLine("조회한 ID: ${applications.map { it.id }}")
                }
            }
            throw BusinessException(UserError.CONTAIN_NOT_EXIST_APPLICATION_ID)
        }

        val processedApplications = applications.filter(SignUpApplicationEntity::isProcessed)
        if (processedApplications.isNotEmpty()) {
            logger.warn {
                buildString {
                    appendLine("이미 처리된 신청서의 ID가 포함되어 있어서 처리에 실패했습니다.")
                    appendLine("처리된 신청서 ID: ${processedApplications.map { it.id }}")
                }
            }
            throw BusinessException(UserError.CONTAIN_ALREADY_PROCESSED_APPLICATION)
        }

        return applications
    }

    private fun executeWithLock(
        email: String,
        action: () -> Unit
    ) {
        if (signUpApplicationCommandService.getLock(email) != 1) {
            logger.warn { "${email}의 잠금을 획득하지 못했습니다." }
            throw BusinessException(UserError.ALREADY_PROCESSED_EMAIL)
        }

        action()
        /**
         * return try {
         *             action()
         *         } finally {
         *             TODO: 명시적으로 release를 처리하기 위해선 별도 클래스에서 action()을 수행해야 하고, Tx는 새롭게 열어야 함
         *             signUpApplicationCommandService.releaseLock(email)
         *         }
         */
    }

    private fun <T> executeWithLockReturning(
        email: String,
        action: () -> T
    ): T {
        if (userCommandService.getLock(email) != 1) {
            logger.warn { "${email}의 잠금을 획득하지 못했습니다." }
            throw BusinessException(UserError.ALREADY_PROCESSED_EMAIL)
        }

        return action()

        /**
         * return try {
         *             action()
         *         } finally {
         *             TODO: 명시적으로 release를 처리하기 위해선 별도 클래스에서 action()을 수행해야 하고, Tx는 새롭게 열어야 함
         *             userCommandService.releaseLock(email)
         *         }
         */
    }
}
