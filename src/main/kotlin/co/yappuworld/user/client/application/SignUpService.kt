package co.yappuworld.user.client.application

import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.user.client.application.usecase.SignUpApplicationExecutor
import co.yappuworld.user.client.dto.request.CheckingEmailAvailabilityRequest
import co.yappuworld.user.client.dto.request.UserSignUpRequest
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
    private val configFindService: ConfigFindService,
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
                role = configFindService.findSignUpCodeBook().decideRole(checkNotNull(request.signUpCode))
            ).let { user -> jwtGenerator.generateToken(SecurityUser.from(user), now) }
    }

    @Transactional(readOnly = true)
    fun checkEmailAvailability(request: CheckingEmailAvailabilityRequest) =
        signUpApplicationExecutor.checkEmailAvailability(request.email)
}
