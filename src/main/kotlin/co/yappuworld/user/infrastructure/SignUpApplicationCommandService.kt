package co.yappuworld.user.infrastructure

import co.yappuworld.user.infrastructure.jpa.SignUpApplicationEntity
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

    fun update(signUpApplication: SignUpApplicationEntity) {
        signUpApplicationRepository.save(signUpApplication)
    }

    fun getLock(email: String): Int? = signUpApplicationRepository.getLock(email)

    fun releaseLock(email: String): Int? = signUpApplicationRepository.releaseLock(email)
}
