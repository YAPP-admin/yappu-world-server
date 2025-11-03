package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.team.infrastructure.jpa.TeamMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class TeamMemberFindService(
    private val teamMemberRepository: TeamMemberRepository
) {

    fun findMembers(team: TeamEntity): List<TeamMemberEntity> = teamMemberRepository.findByTeam(team)

    fun findActivityUnitIds(team: TeamEntity): List<UUID> =
        teamMemberRepository.findByTeam(team).map {
            it.activityUnitId
        }

    fun findMemberOrNull(activityUnitId: UUID): TeamMemberEntity? =
        teamMemberRepository.findByActivityUnitId(activityUnitId)
}
