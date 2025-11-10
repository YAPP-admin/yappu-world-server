package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TeamServiceFindService(
    private val teamServiceRepository: TeamServiceRepository
) {

    fun findServiceOrNull(team: TeamEntity): TeamServiceEntity? = teamServiceRepository.findByTeam(team).singleOrNull()
}
