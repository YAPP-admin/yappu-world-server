package co.yappuworld.support.fixture.user

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.vo.Position
import java.util.UUID

object ActivityUnitFixture {

    fun getActivityUnitFixture(
        generation: Int = 23,
        position: Position = Position.SERVER,
        userId: UUID = UUID.randomUUID()
    ) = ActivityUnit(
        generation = generation,
        position = position,
        userId = userId
    )
}
