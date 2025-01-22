package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.user.domain.vo.Position
import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

/**
 * @property generation 기수
 * @property position 직군
 */
@Table("activity_units")
class ActivityUnit private constructor(
    val generation: Int,
    val position: Position,
    val userId: UUID,
    @Id
    @JvmField
    val id: UUID
) : BaseEntity(), Persistable<UUID> {

    constructor(
        generation: Int,
        position: Position,
        userId: UUID
    ) : this(generation, position, userId, UlidCreator.getMonotonicUlid().toUuid())

    fun withId(id: UUID): ActivityUnit {
        return ActivityUnit(generation, position, userId, id)
    }

    override fun getId(): UUID {
        return this.id
    }

    override fun isNew(): Boolean {
        return !isCreatedAtInitialized()
    }
}
