package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceImageEntity
import co.yappuworld.team.infrastructure.jpa.TeamServiceImageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TeamServiceImageCommandService(
    private val teamServiceImageRepository: TeamServiceImageRepository
) {
    fun save(teamServiceImage: TeamServiceImageEntity): TeamServiceImageEntity =
        teamServiceImageRepository.save(teamServiceImage)

    fun delete(teamServiceImage: TeamServiceImageEntity) {
        teamServiceImageRepository.delete(teamServiceImage)
    }

    fun deleteAll(teamService: TeamServiceEntity) {
        teamServiceImageRepository.deleteAllByTeamService(teamService)
    }
}
