package co.yappuworld.user.client.application.usecase

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.entity.SignUpApplicationEntity
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.SignUpApplicationCommandService
import co.yappuworld.user.infrastructure.SignUpApplicationFindService
import co.yappuworld.user.infrastructure.UserCommandService
import co.yappuworld.user.infrastructure.UserFindService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private val logger = KotlinLogging.logger { }

@Component
@Transactional
class SignUpApplicationExecutor(
    private val userFindService: UserFindService,
    private val userCommandService: UserCommandService,
    private val signUpApplicationFindService: SignUpApplicationFindService,
    private val signUpApplicationCommandService: SignUpApplicationCommandService
) {

    fun submit(application: SignUpApplicationEntity) {
        checkSubmitApplicationAvailability(application)
        signUpApplicationCommandService.submit(application)
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
            logger.error { "${email}은 이미 가입된 이메일입니다." }
            throw BusinessException(UserError.ALREADY_SIGNED_UP_EMAIL)
        }
    }

    private fun checkSubmitApplicationAvailability(application: SignUpApplicationEntity) {
        checkEmailAvailability(application.applicantEmail)

        if (signUpApplicationFindService.existsPendingApplication(application.applicantEmail)) {
            logger.error { "${application.applicantEmail}의 처리되지 않은 기존 신청이 존재합니다." }
            throw BusinessException(UserError.UNPROCESSED_APPLICATION_EXISTS)
        }
    }

    private fun getPendingApplications(applicationIds: List<UUID>): List<SignUpApplicationEntity> {
        val applications = signUpApplicationFindService.findSignUpApplications(applicationIds)

        if (applicationIds.size != applications.size) {
            logger.error {
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
            logger.error {
                buildString {
                    appendLine("이미 처리된 신청서의 ID가 포함되어 있어서 처리에 실패했습니다.")
                    appendLine("처리된 신청서 ID: ${processedApplications.map { it.id }}")
                }
            }
            throw BusinessException(UserError.CONTAIN_ALREADY_PROCESSED_APPLICATION)
        }

        return applications
    }
}
