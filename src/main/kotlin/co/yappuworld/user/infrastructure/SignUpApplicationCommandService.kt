package co.yappuworld.user.infrastructure

import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SignUpApplicationCommandService(
    private val signUpApplicationRepository: SignUpApplicationRepository
) {

    fun submit(signUpApplication: SignUpApplicationEntity) {
        signUpApplicationRepository.save(signUpApplication)
    }
}
