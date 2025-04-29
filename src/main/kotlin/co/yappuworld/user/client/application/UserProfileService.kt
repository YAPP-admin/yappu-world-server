package co.yappuworld.user.client.application

import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.user.client.dto.response.UserActivityHistoriesResponse
import co.yappuworld.user.client.dto.response.UserProfileResponse
import co.yappuworld.user.infrastructure.ActivityUnitFindService
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserProfileService(
    private val userFindService: UserFindService,
    private val activityUnitFindService: ActivityUnitFindService,
    private val generationFindService: GenerationFindService
) {

    @Transactional(readOnly = true)
    fun findUserProfile(userId: UUID): UserProfileResponse =
        UserProfileResponse(
            userFindService.findUser(userId),
            activityUnitFindService.findActivityUnits(userId)
        )

    @Transactional(readOnly = true)
    fun findUserActivityHistories(userId: UUID): UserActivityHistoriesResponse {
        val activityUnits = activityUnitFindService.findActivityUnits(userId)
        val generationByValue = generationFindService
            .findGenerations(activityUnits.map { it.generation })
            .associateBy { it.value }

        return UserActivityHistoriesResponse(activityUnits, generationByValue)
    }
}
