package co.yappuworld.user.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.model.User
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Limit
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger { }

@Component
class UserLoginPermissionChecker(
    private val signUpApplicationRepository: UserSignUpApplicationRepository
) {

    fun checkPermissionAndGetUser(
        user: User?,
        email: String,
        plainPassword: String
    ): User {
        if (user == null) processException(email)

        requireNotNull(user)

        user.checkPassword(plainPassword)
        if (user.isWithdrawn()) {
            throw BusinessException(UserError.WITHDRAWN_USER)
        }

        return user
    }

    private fun processException(email: String) {
        val recentApplication = signUpApplicationRepository.findByApplicantEmailOrderByUpdatedAtDesc(
            email,
            Limit.of(1)
        )

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
