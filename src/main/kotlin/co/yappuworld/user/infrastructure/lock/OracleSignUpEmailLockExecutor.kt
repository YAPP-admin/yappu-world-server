package co.yappuworld.user.infrastructure.lock

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.vo.UserError
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.support.TransactionTemplate
import java.sql.SQLException

@Component
@ConditionalOnProperty(
    name = ["app.lock.signup-email.strategy"],
    havingValue = "oracle-row-lock"
)
class OracleSignUpEmailLockExecutor(
    private val jdbcTemplate: JdbcTemplate,
    transactionManager: PlatformTransactionManager,
    private val properties: SignUpEmailLockProperties
) : SignUpEmailLockExecutor {

    private val requiresNewTransaction = TransactionTemplate(transactionManager).apply {
        propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }

    override fun <T> executeWithLockReturning(
        email: String,
        action: () -> T
    ): T {
        check(TransactionSynchronizationManager.isActualTransactionActive()) {
            "OracleSignUpEmailLockExecutor must be invoked within an active transaction; " +
                "otherwise the row lock is released immediately on auto-commit."
        }

        createLockRowIfAbsentInNewTransaction(email)
        lock(email)
        touch(email)
        return action()
    }

    private fun createLockRowIfAbsentInNewTransaction(email: String) {
        requiresNewTransaction.executeWithoutResult {
            createLockRowIfAbsent(email)
        }
    }

    private fun createLockRowIfAbsent(email: String) {
        try {
            jdbcTemplate.update(
                """
                INSERT INTO SIGN_UP_EMAIL_LOCK (
                    LOCK_KEY,
                    CREATED_AT,
                    UPDATED_AT
                )
                VALUES (
                    ?,
                    SYSTIMESTAMP,
                    SYSTIMESTAMP
                )
                """.trimIndent(),
                email
            )
        } catch (exception: DataIntegrityViolationException) {
            if (!exception.isOracleDuplicateKey()) {
                throw exception
            }
        }
    }

    private fun lock(email: String) {
        try {
            jdbcTemplate.queryForObject(
                """
                SELECT LOCK_KEY
                FROM SIGN_UP_EMAIL_LOCK
                WHERE LOCK_KEY = ?
                FOR UPDATE WAIT ${properties.getValidTimeoutSeconds()}
                """.trimIndent(),
                String::class.java,
                email
            )
        } catch (exception: DataAccessException) {
            if (exception.isOracleLockFailure()) {
                throw BusinessException(UserError.ALREADY_PROCESSED_EMAIL)
            }
            throw exception
        }
    }

    private fun touch(email: String) {
        jdbcTemplate.update(
            """
            UPDATE SIGN_UP_EMAIL_LOCK
            SET UPDATED_AT = SYSTIMESTAMP
            WHERE LOCK_KEY = ?
            """.trimIndent(),
            email
        )
    }

    private fun SignUpEmailLockProperties.getValidTimeoutSeconds(): Long =
        timeoutSeconds.coerceIn(MIN_TIMEOUT_SECONDS, MAX_TIMEOUT_SECONDS)

    private fun DataAccessException.isOracleDuplicateKey(): Boolean =
        findSqlException()?.errorCode == ORACLE_DUPLICATE_KEY_ERROR_CODE

    private fun DataAccessException.isOracleLockFailure(): Boolean =
        findSqlException()?.errorCode in ORACLE_LOCK_FAILURE_ERROR_CODES

    private fun Throwable.findSqlException(): SQLException? =
        generateSequence(this) { it.cause }
            .filterIsInstance<SQLException>()
            .firstOrNull()

    companion object {
        private const val MIN_TIMEOUT_SECONDS = 1L
        private const val MAX_TIMEOUT_SECONDS = 60L
        private const val ORACLE_DUPLICATE_KEY_ERROR_CODE = 1
        private const val ORACLE_LOCK_TIMEOUT_ERROR_CODE = 30006
        private const val ORACLE_DEADLOCK_ERROR_CODE = 60
        private val ORACLE_LOCK_FAILURE_ERROR_CODES = setOf(
            ORACLE_LOCK_TIMEOUT_ERROR_CODE,
            ORACLE_DEADLOCK_ERROR_CODE
        )
    }
}
