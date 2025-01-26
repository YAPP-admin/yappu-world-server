package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.application.dto.response.UserProfileAppResponseDto
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.ActivityUnitRepository
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserProfileService(
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository
) {

    @Transactional(readOnly = true)
    fun findUserProfile(userId: UUID): UserProfileAppResponseDto {
        val user = userRepository.findByIdOrNull(userId) ?: throw BusinessException(UserError.USER_NOT_FOUND)
        val activityUnits = activityUnitRepository.findAllByUserId(userId)
        return UserProfileAppResponseDto.of(user, activityUnits)
    }
}
