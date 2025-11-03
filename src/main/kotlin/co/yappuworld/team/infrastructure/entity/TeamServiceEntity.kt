package co.yappuworld.team.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.team.client.application.ServiceLinksConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "team_services")
class TeamServiceEntity(
    team: TeamEntity,
    name: String? = null,
    hasApp: Boolean = false,
    hasWeb: Boolean = false,
    serviceLinks: ServiceLinks? = null
) : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    var team: TeamEntity = team
        private set

    var name: String? = name
        private set

    @Column(name = "has_app")
    var hasApp: Boolean = hasApp
        private set

    @Column(name = "has_web")
    var hasWeb: Boolean = hasWeb
        private set

    @Convert(converter = ServiceLinksConverter::class)
    @Column(name = "service_links", columnDefinition = "JSON")
    var serviceLinks: ServiceLinks? = serviceLinks
        private set

    fun update(
        name: String?,
        hasApp: Boolean,
        hasWeb: Boolean,
        serviceLinks: ServiceLinks?
    ) {
        this.name = name
        this.hasApp = hasApp
        this.hasWeb = hasWeb
        this.serviceLinks = serviceLinks
    }
}
