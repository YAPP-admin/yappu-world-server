package co.yappuworld.team.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.team.domain.vo.TeamError
import co.yappuworld.team.infrastructure.dto.TeamServiceListDto
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class TeamServiceFindService(
    private val teamServiceRepository: TeamServiceRepository
) {

    fun findServiceOrNull(team: TeamEntity): TeamServiceEntity? = teamServiceRepository.findByTeam(team).singleOrNull()

    fun findTeamService(id: UUID): TeamServiceEntity =
        teamServiceRepository.findByIdOrNull(id)
            ?: throw BusinessException(TeamError.SERVICE_NOT_FOUND)

    fun findTeamServices(teams: List<TeamEntity>): List<TeamServiceEntity> = teamServiceRepository.findByTeamIn(teams)

    fun findTeamServices(
        generation: Int?,
        pageable: Pageable
    ): Page<TeamServiceListDto> =
        teamServiceRepository
            .findPage(CustomTeamDsl, pageable) {
                selectTeamServiceList()
                    .from(
                        entity(TeamServiceEntity::class),
                        join(TeamEntity::class).on(
                            path(TeamServiceEntity::team)
                                .path(TeamEntity::getId)
                                .equal(path(TeamEntity::getId))
                        )
                    ).whereAnd(
                        generation?.let { path(TeamEntity::generation).equal(it) }
                    ).orderBy(path(TeamEntity::generation).desc())
            }.let { page ->
                PageImpl(page.content.filterNotNull(), page.pageable, page.totalElements)
            }
}
