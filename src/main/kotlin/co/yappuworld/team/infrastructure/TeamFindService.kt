package co.yappuworld.team.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.team.domain.vo.ServicePlatform
import co.yappuworld.team.domain.vo.TeamError
import co.yappuworld.team.infrastructure.entity.ServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamEntity
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

    fun existsName(name: String): Boolean = teamRepository.existsTeamByName(name)

    fun findTeams(
        generation: Int?,
        platform: ServicePlatform?,
        pageable: Pageable
    ): Page<TeamEntity> =
        teamRepository
            .findPage(pageable) {
                select(entity(TeamEntity::class))
                    .from(entity(TeamEntity::class))
                    .whereAnd(
                        generation?.let { path(TeamEntity::generation).equal(it) },
                        platform?.let {
                            when (it) {
                                ServicePlatform.APP -> path(TeamEntity::service)(ServiceEntity::hasApp).equal(true)
                                ServicePlatform.WEB -> path(TeamEntity::service)(ServiceEntity::hasWeb).equal(true)
                            }
                        }
                    ).orderBy(*CustomTeamDsl().teamSorting(platform).toTypedArray())
            }.let { page ->
                PageImpl(page.content.filterNotNull(), page.pageable, page.totalElements)
            }

    fun findByGeneration(generation: Int): List<TeamEntity> = teamRepository.findByGeneration(generation)
}
