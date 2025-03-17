package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.user.application.dto.response.UserActivityHistoriesAppResponseDto
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
    private val activityUnitRepository: ActivityUnitRepository,
    private val generationRepository: GenerationRepository
) {

    @Transactional(readOnly = true)
    fun findUserProfile(userId: UUID): UserProfileAppResponseDto {
        val user = userRepository.findByIdOrNull(userId) ?: throw BusinessException(UserError.USER_NOT_FOUND)
        val activityUnits = activityUnitRepository.findAllByUserId(userId)
        return UserProfileAppResponseDto.of(user, activityUnits)
    }

    @Transactional(readOnly = true)
    fun findUserActivityHistories(userId: UUID): UserActivityHistoriesAppResponseDto {
        val activityUnits = activityUnitRepository.findAllByUserId(userId)
        val generationByValue = generationRepository
            .findAllByValueIn(activityUnits.map { it.generation })
            .associateBy { it.id }

        return UserActivityHistoriesAppResponseDto.from(activityUnits, generationByValue)
    }
}
