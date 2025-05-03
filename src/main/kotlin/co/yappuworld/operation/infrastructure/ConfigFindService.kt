package co.yappuworld.operation.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.ConfigEntity
import co.yappuworld.operation.domain.ConfigError
import co.yappuworld.user.domain.model.SignUpCodeBook
import co.yappuworld.user.domain.vo.UserRole
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class ConfigFindService(
    private val configRepository: ConfigRepository
) {

    fun findConfig(id: String): ConfigEntity? = configRepository.findByIdOrNull(id)

    fun findSignUpCodeBook(): SignUpCodeBook =
        SignUpCodeBook(
            configRepository.findAllByIdIn(UserRole.entries.map { it.signUpCodeKey })
        )

    fun findAttendanceCode(): String? {
        val config = configRepository.findByIdOrNull("attendanceCode")

        if (config == null) {
            logger.error { "출석 코드가 저장되어 있지 않습니다." }
            throw BusinessException(ConfigError.CONFIG_KEY_ERROR)
        }

        return config.value
    }

}
