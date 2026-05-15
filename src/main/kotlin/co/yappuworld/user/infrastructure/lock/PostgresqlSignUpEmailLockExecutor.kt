package co.yappuworld.user.infrastructure.lock

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.vo.UserError
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
@ConditionalOnProperty(
    name = ["app.lock.signup-email.strategy"],
    havingValue = "postgresql-advisory-lock"
)
class PostgresqlSignUpEmailLockExecutor(
    private val jdbcTemplate: JdbcTemplate
) : SignUpEmailLockExecutor {

    override fun <T> executeWithLockReturning(
        email: String,
        action: () -> T
    ): T {
        check(TransactionSynchronizationManager.isActualTransactionActive()) {
            "PostgresqlSignUpEmailLockExecutor must be invoked within an active transaction; " +
                "otherwise the transaction-scoped advisory lock is released immediately."
        }

        if (!tryLock(email)) {
            throw BusinessException(UserError.ALREADY_PROCESSED_EMAIL)
        }
        return action()
    }

    private fun tryLock(email: String): Boolean =
        jdbcTemplate.queryForObject(
            "SELECT pg_try_advisory_xact_lock(hashtext(CAST(? AS text)))",
            Boolean::class.java,
            email
        ) ?: false
}
