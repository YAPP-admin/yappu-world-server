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
    service: TeamServiceEntity? = null
) : BaseEntity() {

    @Column(nullable = false)
    var generation: Int = generation
        private set

    @Column(nullable = false)
    var name: String = name
        private set

    fun update(
        generation: Int,
        name: String
    ) {
        this.generation = generation
        this.name = name
    }
}
