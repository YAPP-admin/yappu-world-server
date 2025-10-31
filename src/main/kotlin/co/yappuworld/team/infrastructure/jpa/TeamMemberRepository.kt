package co.yappuworld.team.infrastructure.jpa

import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TeamMemberRepository : JpaRepository<TeamMemberEntity, UUID> {
    fun findByTeam(team: TeamEntity): List<TeamMemberEntity>

    fun findByActivityUnitId(activityUnitId: UUID): TeamMemberEntity?
}
