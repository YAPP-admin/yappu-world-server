package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.application.dto.request.SignUpApplicationApproveAppRequestDto
import co.yappuworld.user.application.dto.request.SignUpApplicationRejectAppRequestDto
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.ActivityUnitRepository
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAuthAdminService(
    private val userRepository: UserRepository,
    private val signUpApplicationRepository: UserSignUpApplicationRepository,
    private val activityUnitRepository: ActivityUnitRepository
) {

    @Transactional
    fun approveSignUpApplication(request: SignUpApplicationApproveAppRequestDto) {
        val application = signUpApplicationRepository.findByIdOrNull(request.applicationId)
            ?.apply { approve() }
            ?: throw BusinessException(UserError.NOT_FOUND_SIGN_UP_APPLICATION)

        signUpApplicationRepository.save(application)
        val user = userRepository.save(application.toUser(request.role))
        activityUnitRepository.saveAll(application.toActivityUnits(user))
    }

    @Transactional
    fun rejectSignUpApplication(request: SignUpApplicationRejectAppRequestDto) {
        val application = signUpApplicationRepository.findByIdOrNull(request.applicationId)
            ?.apply { reject(request.reason) }
            ?: throw BusinessException(UserError.NOT_FOUND_SIGN_UP_APPLICATION)

        signUpApplicationRepository.save(application)
    }
}
