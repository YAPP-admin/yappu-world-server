package co.yappuworld.user.application.dto.request

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.presentation.dto.request.AdminActivityUnitUpdateApiRequestDto
import java.util.UUID

data class AdminActivityUnitUpdateAppRequestDto(
    val id: UUID?,
    val generation: Int,
    val position: Position
) {

    constructor(request: AdminActivityUnitUpdateApiRequestDto) : this(
        id = request.id,
        generation = request.generation,
        position = request.position
    )

    fun toActivityUnit(userId: UUID): ActivityUnit {
        return ActivityUnit(
            generation = generation,
            position = position,
            userId = userId
        )
    }
}
