package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TeamServiceFindService(
    private val teamServiceRepository: TeamServiceRepository
) {

    fun findServiceOrNull(team: TeamEntity): TeamServiceEntity? = teamServiceRepository.findByTeam(team).firstOrNull()

    fun findServices(teamIds: List<UUID>): List<TeamServiceEntity> {
        if (teamIds.isEmpty()) return emptyList()
        return teamServiceRepository.findByTeamIdIn(teamIds)
    }
}
