package co.yappuworld.team.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "teams")
class TeamEntity(
    generation: Int,
    name: String,
    hasApp: Boolean = false,
    hasWeb: Boolean = false
) : BaseEntity() {

    @Column(nullable = false)
    var generation: Int = generation
        private set

    @Column(nullable = false)
    var name: String = name
        private set

    @Column(name = "has_app", nullable = false)
    var hasApp: Boolean = hasApp
        private set

    @Column(name = "has_web", nullable = false)
    var hasWeb: Boolean = hasWeb
        private set

    fun update(
        generation: Int,
        name: String,
        hasApp: Boolean,
        hasWeb: Boolean
    ) {
        this.generation = generation
        this.name = name
        this.hasApp = hasApp
        this.hasWeb = hasWeb
    }
}
