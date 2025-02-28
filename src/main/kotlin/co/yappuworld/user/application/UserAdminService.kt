package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.application.dto.request.UserRoleUpdateAppRequestDto
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAdminService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun updateUserRole(request: UserRoleUpdateAppRequestDto) {
        val user = userRepository.findByIdOrNull(request.userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)

        user.updateRole(request.role)
        userRepository.save(user)
    }
}
