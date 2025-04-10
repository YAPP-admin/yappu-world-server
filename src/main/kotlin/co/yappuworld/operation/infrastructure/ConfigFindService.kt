package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.ConfigEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ConfigFindService(
    private val configRepository: ConfigRepository
) {

    fun findConfig(id: String): ConfigEntity? = configRepository.findByIdOrNull(id)
}
