package co.yappuworld.team.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "team_service_images")
class TeamServiceImageEntity(
    teamService: TeamServiceEntity,
    objectKey: String,
    isThumbnail: Boolean = false,
    displayOrder: Int = 0
) : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_service_id", nullable = false)
    var teamService: TeamServiceEntity = teamService
        private set

    @Column(name = "object_key", nullable = false, length = 2000)
    var objectKey: String = objectKey
        private set

    @Column(name = "is_thumbnail", nullable = false)
    var isThumbnail: Boolean = isThumbnail
        private set

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int = displayOrder
        private set

    fun updateObjectKey(objectKey: String) {
        this.objectKey = objectKey
    }
}
