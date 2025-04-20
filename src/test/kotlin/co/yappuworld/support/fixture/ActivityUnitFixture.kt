package co.yappuworld.support.fixture

import co.yappuworld.user.domain.entity.ActivityUnitEntity
import co.yappuworld.user.domain.vo.Position
import java.util.UUID

object ActivityUnitFixture {

    fun getActivityUnitFixture(
        generation: Int = 23,
        position: Position = Position.SERVER,
        userId: UUID = UUID.randomUUID()
    ) = ActivityUnitEntity(
        generation = generation,
        position = position,
        userId = userId
    )
}
