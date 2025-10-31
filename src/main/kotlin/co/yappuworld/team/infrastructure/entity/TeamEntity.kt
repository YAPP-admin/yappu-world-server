package co.yappuworld.team.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "teams")
class TeamEntity(
    generation: Int,
    name: String,
    service: ServiceEntity? = null
) : BaseEntity() {

    @Column(nullable = false)
    var generation: Int = generation
        private set

    @Column(nullable = false)
    var name: String = name
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    var service: ServiceEntity? = service
        private set

    fun update(
        generation: Int,
        name: String,
        service: ServiceEntity?
    ) {
        this.generation = generation
        this.name = name
        this.service = service
    }
}
