package co.yappuworld.user.client.application

import co.yappuworld.global.security.Token
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.user.client.application.usecase.SignUpExecutor
import co.yappuworld.user.client.dto.request.CheckingEmailAvailabilityRequest
import co.yappuworld.user.client.dto.request.UserSignUpRequest
import co.yappuworld.user.infrastructure.UserSystemNotifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SignUpService(
    private val signUpExecutor: SignUpExecutor,
    private val configFindService: ConfigFindService,
    private val userSystemNotifier: UserSystemNotifier
) {

    fun submitSignUpRequest(
        request: UserSignUpRequest,
        now: LocalDateTime
    ) {
        val signUpApplication = request.toSignUpApplication()
        signUpApplication.run {
            signUpExecutor.submit(this)
            userSystemNotifier.notifySignUpRequestReceived(this)
        }
    }

    fun signUpWithCode(
        request: UserSignUpRequest,
        now: LocalDateTime
    ): Token =
        signUpExecutor.signUp(
            application = request.toSignUpApplication(),
            role = configFindService.findSignUpCodeBook().decideRole(checkNotNull(request.signUpCode)),
            now = now
        )

    @Transactional(readOnly = true)
    fun checkEmailAvailability(request: CheckingEmailAvailabilityRequest) =
        signUpExecutor.checkEmailAvailability(request.email)
}
