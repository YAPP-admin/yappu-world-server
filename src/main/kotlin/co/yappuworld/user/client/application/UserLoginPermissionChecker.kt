package co.yappuworld.user.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.entity.UserEntity
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger { }

@Component
class UserLoginPermissionChecker(
    private val signUpApplicationRepository: SignUpApplicationRepository
) {

    fun checkPermissionAndGetUser(
        user: UserEntity?,
        email: String,
        plainPassword: String
    ): UserEntity {
        if (user == null) processException(email)

        requireNotNull(user)

        user.checkPassword(plainPassword)
        if (user.isWithdrawn()) {
            throw BusinessException(UserError.WITHDRAWN_USER)
        }

        return user
    }

    private fun processException(email: String) {
        val recentApplication = signUpApplicationRepository.findFirstByApplicantEmailOrderByUpdatedAtDesc(email)

        if (recentApplication == null) {
            logger.error { "${email}로 가입 시도조차 없습니다." }
            throw BusinessException(UserError.FAIL_LOGIN_NOT_FOUND_USER)
        }

        throw when (recentApplication.status) {
            SignUpApplicationStatus.PENDING -> {
                logger.error { "${email}의 기존 가입 신청이 처리되지 않았습니다." }
                BusinessException(UserError.CANNOT_LOGIN_WITH_UNPROCESSED_SIGN_UP_APPLICATION)
            }
            SignUpApplicationStatus.REJECTED -> {
                logger.error { "${email}의 기존 가입 신청이 처리되지 않았습니다." }
                BusinessException(UserError.RECENT_SIGN_UP_APPLICATION_REJECTED)
            }
            SignUpApplicationStatus.APPROVED -> {
                logger.error { "${email}의 가입 프로세스에 문제가 생겼습니다." }
                BusinessException(UserError.CANNOT_LOGIN_WRONG_USER_STATE)
            }
        }
    }
}
