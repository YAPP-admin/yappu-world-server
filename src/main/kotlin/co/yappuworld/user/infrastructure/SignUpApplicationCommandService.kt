package co.yappuworld.user.infrastructure

import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SignUpApplicationCommandService(
    private val signUpApplicationRepository: SignUpApplicationRepository,
    private val signUpApplicationActivityUnitRepository: SignUpApplicationActivityUnitRepository
) {

    fun submit(signUpApplication: SignUpApplicationEntity) {
        val application = signUpApplicationRepository.save(signUpApplication)
        signUpApplicationActivityUnitRepository.saveAll(
            application.toSignUpApplicationActivityUnits()
        )
    }

    fun update(signUpApplication: SignUpApplicationEntity) {
        signUpApplicationRepository.save(signUpApplication)
    }
}
