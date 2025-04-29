package co.yappuworld.user.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.operation.client.application.ConfigInquiryComponent
import co.yappuworld.user.client.application.usecase.SignUpApplicationExecutor
import co.yappuworld.user.client.dto.request.CheckingEmailAvailabilityRequest
import co.yappuworld.user.client.dto.request.UserSignUpRequest
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.UserCommandService
import co.yappuworld.user.infrastructure.UserSystemNotifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SignUpService(
    private val userCommandService: UserCommandService,
    private val signUpApplicationExecutor: SignUpApplicationExecutor,
    private val jwtGenerator: JwtGenerator,
    private val configInquiryComponent: ConfigInquiryComponent,
    private val userSystemNotifier: UserSystemNotifier
) {

    @Transactional
    fun submitSignUpRequest(
        request: UserSignUpRequest,
        now: LocalDateTime
    ) {
        val signUpApplication = request.toSignUpApplication()
        signUpApplication.run {
            signUpApplicationExecutor.submit(this)
            userSystemNotifier.notifySignUpRequestReceived(this)
        }
    }

    @Transactional
    fun signUpWithCode(
        request: UserSignUpRequest,
        now: LocalDateTime
    ): Token {
        signUpApplicationExecutor.checkEmailAvailability(request.email)

        return userCommandService
            .signUp(
                application = request.toSignUpApplication(),
                role = getUserRoleWithSignUpCode(request.signUpCode!!)
            ).let { user -> jwtGenerator.generateToken(SecurityUser.from(user), now) }
    }

    @Transactional(readOnly = true)
    fun checkEmailAvailability(request: CheckingEmailAvailabilityRequest) =
        signUpApplicationExecutor.checkEmailAvailability(request.email)

    private fun getUserRoleWithSignUpCode(signUpCode: String): UserRole {
        val config = configInquiryComponent
            .findConfigsBy(UserRole.entries.map { it.signUpCodeKey })
            .singleOrNull { it.value == signUpCode }
            ?: throw BusinessException(UserError.INVALID_SIGN_UP_CODE)

        return UserRole.entries.single { it.signUpCodeKey == config.id }
    }
}
