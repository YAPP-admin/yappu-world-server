package co.yappuworld.team.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.team.domain.vo.TeamError
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class TeamCommandService(
    private val teamRepository: TeamRepository
) {

    fun save(team: TeamEntity) {
        validateTeamName(team)
        teamRepository.save(team)
    }

    fun delete(id: UUID) {
        teamRepository.deleteById(id)
    }

    fun deleteAll(ids: List<UUID>) {
        teamRepository.deleteAllById(ids)
    }

    private fun validateTeamName(team: TeamEntity) {
        val exists = teamRepository.existsByGenerationAndName(team.generation, team.name)
        if (exists) {
            throw BusinessException(TeamError.TEAM_ALREADY_EXISTS)
        }
    }
}
