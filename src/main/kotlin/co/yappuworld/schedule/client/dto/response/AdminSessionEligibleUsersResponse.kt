package co.yappuworld.schedule.client.dto.response

import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import java.util.UUID

data class AdminSessionEligibleUsersResponse(
    val users: List<AdminSessionEligibleUsersGroupedByPositionResponse>
) {

    companion object {
        fun from(users: List<UserWithActivityUnit>): AdminSessionEligibleUsersResponse =
            AdminSessionEligibleUsersResponse(
                users = users
                    .groupBy { it.position }
                    .let { userByPosition ->
                        Position.activeUserPositions.map { position ->
                            AdminSessionEligibleUsersGroupedByPositionResponse(
                                position = position,
                                users = userByPosition[position]?.map { user -> AdminSessionEligibleUserResponse(user) }
                                    ?: emptyList()
                            )
                        }
                    }
            )
    }
}

data class AdminSessionEligibleUsersGroupedByPositionResponse(
    val position: Position,
    val users: List<AdminSessionEligibleUserResponse>
)

data class AdminSessionEligibleUserResponse(
    val userId: UUID,
    val name: String
) {

    constructor(user: UserWithActivityUnit) : this(
        userId = user.userId,
        name = user.name
    )
}
