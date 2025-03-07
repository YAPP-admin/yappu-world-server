package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.user.domain.vo.Position
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

/**
 * @property generation 기수
 * @property position 직군
 */
@Table("activity_units")
class ActivityUnit(
    generation: Int,
    position: Position,
    val userId: UUID
) : BaseEntity() {

    var generation: Int = generation
        private set
    var position: Position = position
        private set

    fun withId(id: UUID): ActivityUnit = ActivityUnit(generation, position, userId).apply { this.id = id }

    fun updateActivityUnit(
        generation: Int,
        position: Position
    ) {
        this.generation = generation
        this.position = position
    }
}
