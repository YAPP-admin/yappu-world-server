package co.yappuworld.team.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.team.domain.vo.Platform
import co.yappuworld.team.infrastructure.dto.HistoricalServiceSummaryProjection
import co.yappuworld.team.domain.vo.TeamError
import co.yappuworld.team.infrastructure.dto.TeamServiceSummary
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
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
    private data class HistoricalServiceCursor(
        val serviceId: UUID,
        val generation: Int,
        val isApp: Boolean,
        val serviceName: String?
    )

    fun findServiceOrNull(team: TeamEntity): TeamServiceEntity? = teamServiceRepository.findByTeam(team).singleOrNull()

    fun findTeamService(id: UUID): TeamServiceEntity =
        teamServiceRepository.findByIdOrNull(id)
            ?: throw BusinessException(TeamError.SERVICE_NOT_FOUND)

    fun findTeamServices(teams: List<TeamEntity>): List<TeamServiceEntity> = teamServiceRepository.findByTeamIn(teams)

    fun findTeamServices(serviceIds: Collection<UUID>): List<TeamServiceEntity> {
        require(serviceIds.isNotEmpty()) { "서비스 조회 요청에 들어오는 ID는 최소 하나 이상이어야 합니다." }
        return teamServiceRepository.findAllById(serviceIds)
    }

    fun findHistoricalServices(
        generation: Int?,
        platform: Platform?,
        lastServiceId: UUID?,
        limit: Int
    ): List<HistoricalServiceSummaryProjection> {
        val cursor = lastServiceId?.let { createHistoricalServiceCursor(it) }

        return teamServiceRepository
            .findAll(CustomTeamDsl, Pageable.ofSize(limit)) {
                val predicates = mutableListOf<Predicate>()

                generation?.let { predicates.add(path(TeamEntity::generation).equal(it)) }
                platform?.let {
                    predicates.add(
                        when (it) {
                            Platform.APP -> path(TeamServiceEntity::hasApp).equal(true)
                            Platform.WEB -> path(TeamServiceEntity::hasWeb).equal(true)
                        }
                    )
                }
                cursor?.let {
                    predicates.add(createHistoricalServiceCursorPredicate(cursor, platform))
                }

                selectHistoricalServices()
                    .from(
                        entity(TeamServiceEntity::class),
                        join(TeamEntity::class).on(
                            path(TeamServiceEntity::team)
                                .path(TeamEntity::getId)
                                .equal(path(TeamEntity::getId))
                        )
                    ).whereAnd(*predicates.toTypedArray())
                    .orderBy(*historicalServiceSorting(platform).toTypedArray())
            }.filterNotNull()
    }

    fun findTeamServices(
        generation: Int?,
        pageable: Pageable
    ): Page<TeamServiceSummary> =
        teamServiceRepository
            .findPage(CustomTeamDsl, pageable) {
                selectTeamServices()
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
                PageImpl(page.content, page.pageable, page.totalElements)
            }

    private fun createHistoricalServiceCursor(serviceId: UUID): HistoricalServiceCursor =
        findTeamService(serviceId).let { service ->
            HistoricalServiceCursor(
                serviceId = service.id,
                generation = service.team.generation,
                isApp = service.hasApp,
                serviceName = service.name
            )
        }

    private fun CustomTeamDsl.createHistoricalServiceCursorPredicate(
        cursor: HistoricalServiceCursor,
        platform: Platform?
    ): Predicate =
        when {
            cursor.isApp && platform == null ->
                or(
                    createHistoricalServiceCommonCursorPredicate(cursor, platform),
                    createRemainingWebOfSameGenerationPredicate(cursor)
                )
            else -> createHistoricalServiceCommonCursorPredicate(cursor, platform)
        }

    private fun CustomTeamDsl.createHistoricalServiceCommonCursorPredicate(
        cursor: HistoricalServiceCursor,
        platform: Platform?
    ): Predicate =
        or(
            createLowerGenerationPredicate(cursor),
            createSameOrderingBucketAfterCursorPredicate(cursor, platform)
        )

    private fun CustomTeamDsl.createLowerGenerationPredicate(cursor: HistoricalServiceCursor): Predicate =
        path(TeamEntity::generation).lessThan(cursor.generation)

    private fun CustomTeamDsl.createSameOrderingBucketAfterCursorPredicate(
        cursor: HistoricalServiceCursor,
        platform: Platform?
    ): Predicate =
        when (platform) {
            null ->
                and(
                    path(TeamEntity::generation).equal(cursor.generation),
                    path(TeamServiceEntity::hasApp).equal(cursor.isApp),
                    createNameAndIdAfterCursorPredicate(cursor)
                )
            else ->
                and(
                    path(TeamEntity::generation).equal(cursor.generation),
                    createNameAndIdAfterCursorPredicate(cursor)
                )
        }

    private fun CustomTeamDsl.createNameAndIdAfterCursorPredicate(cursor: HistoricalServiceCursor): Predicate =
        or(
            path(TeamServiceEntity::name).lessThan(cursor.serviceName),
            createSameNameAfterCursorIdPredicate(cursor)
        )

    private fun CustomTeamDsl.createSameNameAfterCursorIdPredicate(cursor: HistoricalServiceCursor): Predicate =
        and(
            path(TeamServiceEntity::name).equal(cursor.serviceName),
            path(TeamServiceEntity::getId).lessThan(cursor.serviceId)
        )

    private fun CustomTeamDsl.createRemainingWebOfSameGenerationPredicate(cursor: HistoricalServiceCursor): Predicate =
        and(
            path(TeamEntity::generation).equal(cursor.generation),
            path(TeamServiceEntity::hasApp).equal(false)
        )
}
