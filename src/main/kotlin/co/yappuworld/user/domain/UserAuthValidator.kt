package co.yappuworld.user.domain

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.model.User
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserSignUpApplicationStatus
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger { }

fun User.checkLoginAvailability(requestPassword: String) {
    this.checkPassword(requestPassword)
    if (isWithdrawn()) {
        throw BusinessException(UserError.WITHDRAWN_USER)
    }
}

fun List<SignUpApplication>.checkNewApplications(email: String) {
    if (this.isEmpty()) {
        return
    }

    if (this.any { it.status == UserSignUpApplicationStatus.PENDING }) {
        logger.error { "${email}의 처리되지 않은 기존 신청이 존재합니다." }
        throw BusinessException(UserError.UNPROCESSED_APPLICATION_EXISTS)
    }
}
