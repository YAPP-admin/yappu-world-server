package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseJpaEntity
import co.yappuworld.user.domain.vo.Position
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "activity_units")
class ActivityUnitEntity(
    generation: Int,
    position: Position,
    val userId: UUID
) : BaseJpaEntity() {

    var generation: Int = generation
        private set

    @Enumerated(EnumType.STRING)
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
