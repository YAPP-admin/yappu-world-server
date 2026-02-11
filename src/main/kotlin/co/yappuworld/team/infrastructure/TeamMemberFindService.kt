package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.dto.TeamMemberDetailDto
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.team.infrastructure.jpa.TeamMemberRepository
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class TeamMemberFindService(
    private val teamMemberRepository: TeamMemberRepository
) {

    fun findMembers(team: TeamEntity): List<TeamMemberEntity> = teamMemberRepository.findByTeam(team)

    fun findTeamMembersDetail(teamId: UUID): List<TeamMemberDetailDto?> =
        teamMemberRepository.findAll(CustomTeamDsl) {
            selectTeamMemberDetail()
                .from(
                    entity(TeamMemberEntity::class),
                    join(UserEntity::class).on(
                        path(TeamMemberEntity::activityUnit)
                            .path(ActivityUnitEntity::userId)
                            .equal(path(UserEntity::getId))
                    )
                ).where(
                    path(TeamMemberEntity::team).path(TeamEntity::getId).equal(teamId)
                )
        }
}
