package co.yappuworld.team.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.team.infrastructure.dto.TeamWithServiceDto
import co.yappuworld.team.domain.vo.Platform
import co.yappuworld.team.domain.vo.TeamError
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class TeamFindService(
    private val teamRepository: TeamRepository
) {

    fun findTeam(id: UUID): TeamEntity =
        teamRepository.findByIdOrNull(id)
            ?: throw BusinessException(TeamError.TEAM_NOT_FOUND)

    fun findTeamWithService(id: UUID): TeamWithServiceDto =
        teamRepository
            .findAll(CustomTeamDsl) {
                selectTeamWithService()
                    .from(
                        entity(TeamEntity::class),
                        leftJoin(TeamServiceEntity::class).on(
                            path(TeamEntity::getId)
                                .equal(path(TeamServiceEntity::team).path(TeamEntity::getId))
                        )
                    ).where(
                        path(TeamEntity::getId).equal(id)
                    )
            }.singleOrNull()
            ?: throw BusinessException(TeamError.TEAM_NOT_FOUND)

    fun existsName(name: String): Boolean = teamRepository.existsTeamByName(name)

    fun findTeams(
        generation: Int?,
        platform: Platform?,
        pageable: Pageable
    ): Page<TeamWithServiceDto> =
        teamRepository
            .findPage(CustomTeamDsl, pageable) {
                selectTeamWithService()
                    .from(
                        entity(TeamEntity::class),
                        leftJoin(TeamServiceEntity::class).on(
                            path(TeamEntity::getId)
                                .equal(path(TeamServiceEntity::team).path(TeamEntity::getId))
                        )
                    ).whereAnd(
                        generation?.let { path(TeamEntity::generation).equal(it) },
                        platform?.let {
                            when (it) {
                                Platform.APP -> path(TeamEntity::hasApp).equal(true)
                                Platform.WEB -> path(TeamEntity::hasWeb).equal(true)
                            }
                        }
                    ).orderBy(*teamSorting(platform).toTypedArray())
            }.let { page ->
                PageImpl(page.content.filterNotNull(), page.pageable, page.totalElements)
            }
}
