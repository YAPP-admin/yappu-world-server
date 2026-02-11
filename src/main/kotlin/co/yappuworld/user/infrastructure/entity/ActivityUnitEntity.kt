package co.yappuworld.user.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.user.domain.vo.Position
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Table
import jakarta.persistence.OneToOne
import java.util.UUID

@Entity
@Table(name = "activity_units")
class ActivityUnitEntity(
    generation: Int,
    position: Position,
    val userId: UUID
) : BaseEntity() {

    var generation: Int = generation
        private set

    @Enumerated(EnumType.STRING)
    var position: Position = position
        private set

    @OneToOne(mappedBy = "activityUnit", fetch = FetchType.LAZY)
    var teamMember: TeamMemberEntity? = null

    fun updateActivityUnit(
        generation: Int,
        position: Position
    ) {
        this.generation = generation
        this.position = position
    }
}
