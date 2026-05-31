package co.yappuworld.user.client.application

import co.yappuworld.user.client.dto.response.UserPersonHistoryResponse
import co.yappuworld.user.client.dto.response.UserPersonProfileResponse
import co.yappuworld.team.infrastructure.TeamServiceFindService
import co.yappuworld.user.infrastructure.UserFindService
import co.yappuworld.user.infrastructure.model.UserPersonProfileHistoryProjection
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserPersonProfileService(
    private val userFindService: UserFindService,
    private val teamServiceFindService: TeamServiceFindService
) {

    @Transactional(readOnly = true)
    fun getUserPersonProfile(userId: UUID): UserPersonProfileResponse {
        val user = userFindService.findUser(userId)
        val historyProjections = userFindService
            .findUserPersonProfileHistories(userId)
            .sortedWith(
                compareByDescending<UserPersonProfileHistoryProjection> { it.generation }
                    .thenBy { it.position.order }
            )
        val servicesById = historyProjections
            .mapNotNull(UserPersonProfileHistoryProjection::serviceId)
            .distinct()
            .takeIf { it.isNotEmpty() }
            ?.let(teamServiceFindService::findTeamServices)
            ?.associateBy { it.id }
            ?: emptyMap()
        val histories = historyProjections.map { projection ->
            UserPersonHistoryResponse.from(
                projection = projection,
                service = projection.serviceId?.let(servicesById::get)
            )
        }

        return UserPersonProfileResponse.of(
            user = user,
            histories = histories
        )
    }
}
