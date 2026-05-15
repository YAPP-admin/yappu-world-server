package co.yappuworld.user.infrastructure.lock

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["app.lock.signup-email.strategy"],
    havingValue = "none"
)
class NoopSignUpEmailLockExecutor : SignUpEmailLockExecutor {

    override fun <T> executeWithLockReturning(
        email: String,
        action: () -> T
    ): T = action()
}
