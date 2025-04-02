package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.UserEntity
import co.yappuworld.user.infrastructure.jpa.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userRepository: UserRepository
) {

    fun save(user: UserEntity): UserEntity = userRepository.save(user)
}
