package co.yappuworld.user.infrastructure.lock

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.vo.UserError
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.SQLException

@Component
@ConditionalOnProperty(
    name = ["app.lock.signup-email.strategy"],
    havingValue = "oracle-row-lock"
)
class OracleSignUpEmailLockExecutor(
    private val jdbcTemplate: JdbcTemplate,
    private val properties: SignUpEmailLockProperties
) : SignUpEmailLockExecutor {

    override fun <T> executeWithLockReturning(
        email: String,
        action: () -> T
    ): T {
        createLockRowIfAbsent(email)
        lock(email)
        return action()
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
            if (exception.isOracleLockTimeout()) {
                throw BusinessException(UserError.ALREADY_PROCESSED_EMAIL)
            }
            throw exception
        }
    }

    private fun SignUpEmailLockProperties.getValidTimeoutSeconds(): Long =
        timeoutSeconds.coerceIn(MIN_TIMEOUT_SECONDS, MAX_TIMEOUT_SECONDS)

    private fun DataAccessException.isOracleDuplicateKey(): Boolean =
        findSqlException()?.errorCode == ORACLE_DUPLICATE_KEY_ERROR_CODE

    private fun DataAccessException.isOracleLockTimeout(): Boolean =
        findSqlException()?.errorCode == ORACLE_LOCK_TIMEOUT_ERROR_CODE

    private fun Throwable.findSqlException(): SQLException? =
        generateSequence(this) { it.cause }
            .filterIsInstance<SQLException>()
            .firstOrNull()

    companion object {
        private const val MIN_TIMEOUT_SECONDS = 1L
        private const val MAX_TIMEOUT_SECONDS = 60L
        private const val ORACLE_DUPLICATE_KEY_ERROR_CODE = 1
        private const val ORACLE_LOCK_TIMEOUT_ERROR_CODE = 30006
    }
}
