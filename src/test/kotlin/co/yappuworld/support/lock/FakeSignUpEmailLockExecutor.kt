package co.yappuworld.support.lock

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.lock.SignUpEmailLockExecutor
import co.yappuworld.user.infrastructure.lock.SignUpEmailLockProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

@Component
@ConditionalOnProperty(
    name = ["app.lock.signup-email.strategy"],
    havingValue = "fake"
)
class FakeSignUpEmailLockExecutor(
    private val properties: SignUpEmailLockProperties
) : SignUpEmailLockExecutor {

    private val locks = ConcurrentHashMap<String, ReentrantLock>()

    override fun <T> executeWithLockReturning(
        email: String,
        action: () -> T
    ): T {
        val lock = locks.computeIfAbsent(email) { ReentrantLock() }
        val acquired = try {
            lock.tryLock(properties.timeoutSeconds, TimeUnit.SECONDS)
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IllegalStateException("Interrupted while acquiring sign-up email lock", exception)
        }

        if (!acquired) {
            throw BusinessException(UserError.ALREADY_PROCESSED_EMAIL)
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            registerUnlockAfterTransactionCompletion(lock)
            return action()
        }

        return try {
            action()
        } finally {
            lock.unlock()
        }
    }

    private fun registerUnlockAfterTransactionCompletion(lock: ReentrantLock) {
        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCompletion(status: Int) {
                    lock.unlock()
                }
            }
        )
    }
}
