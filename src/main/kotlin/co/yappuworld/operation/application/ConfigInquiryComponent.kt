package co.yappuworld.operation.application

import co.yappuworld.operation.domain.Config
import co.yappuworld.operation.infrastructure.ConfigRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Transactional(readOnly = true)
@Component
class ConfigInquiryComponent(
    private val configRepository: ConfigRepository
) {

    fun findConfigBy(key: String): Config {
        return configRepository.findById(key).getOrNull()
            ?: throw NoSuchElementException("해당하는 값을 찾을 수 없습니다.")
    }

    fun findConfigsBy(keys: List<String>): List<Config> {
        return configRepository.findAllByIdIn(keys)
    }
}
