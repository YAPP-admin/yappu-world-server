package co.yappuworld.team.infrastructure.jpa

import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TeamServiceRepository : JpaRepository<TeamServiceEntity, UUID> {
    fun findByTeam(team: TeamEntity): List<TeamServiceEntity>

    fun findByTeamIdIn(teamIds: List<UUID>): List<TeamServiceEntity>
}
