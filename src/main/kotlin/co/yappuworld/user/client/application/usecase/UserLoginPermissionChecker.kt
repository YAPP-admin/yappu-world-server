package co.yappuworld.user.client.application.usecase

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.entity.UserEntity
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.SignUpApplicationFindService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger { }

@Component
class UserLoginPermissionChecker(
    private val signUpApplicationFindService: SignUpApplicationFindService
) {

    fun checkLoginAvailability(
        nullableUser: UserEntity?,
        email: String,
        plainPassword: String
    ) {
        val user = nullableUser ?: return processException(email)

        user.checkPassword(plainPassword)
        if (user.isWithdrawn()) throw BusinessException(UserError.WITHDRAWN_USER)
    }

    private fun processException(email: String) {
        val recentApplication = signUpApplicationFindService.findLatestSignUpApplication(email)

        if (recentApplication == null) {
            logger.warn { "${email}로 가입 시도조차 없습니다." }
            throw BusinessException(UserError.FAIL_LOGIN_NOT_FOUND_USER)
        }

        throw when (recentApplication.status) {
            SignUpApplicationStatus.PENDING -> {
                logger.warn { "${email}의 기존 가입 신청이 처리되지 않았습니다." }
                BusinessException(UserError.CANNOT_LOGIN_WITH_UNPROCESSED_SIGN_UP_APPLICATION)
            }
            SignUpApplicationStatus.REJECTED -> {
                logger.warn { "${email}의 기존 가입 신청이 거절된 상태입니다." }
                BusinessException(UserError.RECENT_SIGN_UP_APPLICATION_REJECTED)
            }
            SignUpApplicationStatus.APPROVED -> {
                logger.warn { "${email}의 가입 신청은 승인되었으나 유저 데이터가 생성되지 않았습니다." }
                BusinessException(UserError.CANNOT_LOGIN_WRONG_USER_STATE)
            }
        }
    }
}
