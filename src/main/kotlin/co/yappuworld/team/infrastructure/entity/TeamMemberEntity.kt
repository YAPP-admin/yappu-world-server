package co.yappuworld.team.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Table
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import java.util.UUID

@Entity
@Table(name = "team_members")
class TeamMemberEntity(
    team: TeamEntity,
    activityUnitId: UUID
) : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: TeamEntity = team
        private set

    @Column(name = "activity_unit_id")
    var activityUnitId: UUID = activityUnitId
        private set
}
