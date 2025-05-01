package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.ConfigEntity
import co.yappuworld.user.domain.model.SignUpCodeBook
import co.yappuworld.user.domain.vo.UserRole
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
}
