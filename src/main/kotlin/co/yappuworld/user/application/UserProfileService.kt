package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.application.dto.response.UserProfileAppResponseDto
import co.yappuworld.user.domain.UserError
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserProfileService(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun findUserProfile(userId: UUID): UserProfileAppResponseDto {
        val user = userRepository.findByIdOrNull(userId) ?: throw BusinessException(UserError.USER_NOT_FOUND)
        return UserProfileAppResponseDto.of(user, emptyList())
    }
}
