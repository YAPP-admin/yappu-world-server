package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseJpaEntity
import co.yappuworld.user.domain.vo.Position
import jakarta.persistence.Entity
import java.util.UUID

@Entity
class ActivityUnitEntity(
    generation: Int,
    position: Position,
    val userId: UUID
) : BaseJpaEntity() {

    var generation: Int = generation
        private set
    var position: Position = position
        private set

    fun updateActivityUnit(
        generation: Int,
        position: Position
    ) {
        this.generation = generation
        this.position = position
    }
}
