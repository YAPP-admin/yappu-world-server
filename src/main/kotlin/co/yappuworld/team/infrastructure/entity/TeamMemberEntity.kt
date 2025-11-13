package co.yappuworld.team.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Table
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn

@Entity
@Table(name = "team_members")
class TeamMemberEntity(
    team: TeamEntity,
    activityUnit: ActivityUnitEntity
) : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: TeamEntity = team
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_unit_id")
    var activityUnit: ActivityUnitEntity = activityUnit
        private set
}
