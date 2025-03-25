package co.yappuworld.user.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.user.client.dto.response.UserActivityHistoriesResponse
import co.yappuworld.user.client.dto.response.UserProfileResponse
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
    fun findUserProfile(userId: UUID): UserProfileResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw BusinessException(UserError.USER_NOT_FOUND)
        val activityUnits = activityUnitRepository.findAllByUserId(userId)
        return UserProfileResponse(user, activityUnits)
    }

    @Transactional(readOnly = true)
    fun findUserActivityHistories(userId: UUID): UserActivityHistoriesResponse {
        val activityUnits = activityUnitRepository.findAllByUserId(userId)
        val generationByValue = generationRepository
            .findAllByValueIn(activityUnits.map { it.generation })
            .associateBy { it.id }

        return UserActivityHistoriesResponse(activityUnits, generationByValue)
    }
}
