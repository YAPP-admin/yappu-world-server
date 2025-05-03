package co.yappuworld.operation.infrastructure

import co.yappuworld.user.domain.model.SignUpCodeBook
import co.yappuworld.user.domain.vo.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ConfigCommandService(
    private val configRepository: ConfigRepository
) {

    fun update(signUpCodeBook: SignUpCodeBook) {
        val configBySignUpCodeKey = configRepository
            .findAllByIdIn(UserRole.entries.map { it.signUpCodeKey })
            .associateBy { it.id }

        UserRole.entries.forEach { entry ->
            configBySignUpCodeKey[entry.signUpCodeKey]?.update(signUpCodeBook.getCode(entry))
        }

        configRepository.saveAll(configBySignUpCodeKey.values)
    }
}
